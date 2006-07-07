// $ANTLR 3.0ea8 /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g 2006-07-07 12:37:06

	package org.drools.lang;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class RuleParserLexer extends Lexer {
    public static final int T29=29;
    public static final int T36=36;
    public static final int T58=58;
    public static final int MISC=10;
    public static final int FLOAT=9;
    public static final int T35=35;
    public static final int T61=61;
    public static final int T45=45;
    public static final int T20=20;
    public static final int T34=34;
    public static final int T25=25;
    public static final int T18=18;
    public static final int T37=37;
    public static final int INT=6;
    public static final int T26=26;
    public static final int T32=32;
    public static final int T17=17;
    public static final int T51=51;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=12;
    public static final int T46=46;
    public static final int T16=16;
    public static final int T38=38;
    public static final int T41=41;
    public static final int T24=24;
    public static final int T19=19;
    public static final int T39=39;
    public static final int ID=5;
    public static final int T21=21;
    public static final int Synpred1_fragment=63;
    public static final int T44=44;
    public static final int T55=55;
    public static final int BOOL=7;
    public static final int T33=33;
    public static final int T22=22;
    public static final int T50=50;
    public static final int WS=11;
    public static final int STRING=8;
    public static final int T43=43;
    public static final int T23=23;
    public static final int T28=28;
    public static final int T42=42;
    public static final int T40=40;
    public static final int T57=57;
    public static final int T56=56;
    public static final int T59=59;
    public static final int T48=48;
    public static final int T15=15;
    public static final int T54=54;
    public static final int EOF=-1;
    public static final int T47=47;
    public static final int EOL=4;
    public static final int Tokens=62;
    public static final int T53=53;
    public static final int T60=60;
    public static final int T31=31;
    public static final int MULTI_LINE_COMMENT=14;
    public static final int T49=49;
    public static final int T27=27;
    public static final int T52=52;
    public static final int T30=30;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=13;
    public RuleParserLexer() {;} 
    public RuleParserLexer(CharStream input) {
        super(input);
        ruleMemo = new Map[60+1];
     }


    // $ANTLR start T15
    public void mT15() throws RecognitionException {
        int T15_StartIndex = input.index();
        try {
            int type = T15;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 1) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:6:7: ( ';' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:6:7: ';'
            {
            match(';'); if (failed) return ;

            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 1, T15_StartIndex); }
        }
    }
    // $ANTLR end T15


    // $ANTLR start T16
    public void mT16() throws RecognitionException {
        int T16_StartIndex = input.index();
        try {
            int type = T16;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 2) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:7:7: ( 'package' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:7:7: 'package'
            {
            match("package"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 2, T16_StartIndex); }
        }
    }
    // $ANTLR end T16


    // $ANTLR start T17
    public void mT17() throws RecognitionException {
        int T17_StartIndex = input.index();
        try {
            int type = T17;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 3) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:8:7: ( 'import' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:8:7: 'import'
            {
            match("import"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 3, T17_StartIndex); }
        }
    }
    // $ANTLR end T17


    // $ANTLR start T18
    public void mT18() throws RecognitionException {
        int T18_StartIndex = input.index();
        try {
            int type = T18;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 4) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:9:7: ( '.' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:9:7: '.'
            {
            match('.'); if (failed) return ;

            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 4, T18_StartIndex); }
        }
    }
    // $ANTLR end T18


    // $ANTLR start T19
    public void mT19() throws RecognitionException {
        int T19_StartIndex = input.index();
        try {
            int type = T19;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 5) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:10:7: ( '.*' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:10:7: '.*'
            {
            match(".*"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 5, T19_StartIndex); }
        }
    }
    // $ANTLR end T19


    // $ANTLR start T20
    public void mT20() throws RecognitionException {
        int T20_StartIndex = input.index();
        try {
            int type = T20;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 6) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:11:7: ( 'expander' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:11:7: 'expander'
            {
            match("expander"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 6, T20_StartIndex); }
        }
    }
    // $ANTLR end T20


    // $ANTLR start T21
    public void mT21() throws RecognitionException {
        int T21_StartIndex = input.index();
        try {
            int type = T21;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 7) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:12:7: ( 'global' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:12:7: 'global'
            {
            match("global"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 7, T21_StartIndex); }
        }
    }
    // $ANTLR end T21


    // $ANTLR start T22
    public void mT22() throws RecognitionException {
        int T22_StartIndex = input.index();
        try {
            int type = T22;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 8) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:13:7: ( 'function' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:13:7: 'function'
            {
            match("function"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 8, T22_StartIndex); }
        }
    }
    // $ANTLR end T22


    // $ANTLR start T23
    public void mT23() throws RecognitionException {
        int T23_StartIndex = input.index();
        try {
            int type = T23;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 9) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:14:7: ( '(' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:14:7: '('
            {
            match('('); if (failed) return ;

            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 9, T23_StartIndex); }
        }
    }
    // $ANTLR end T23


    // $ANTLR start T24
    public void mT24() throws RecognitionException {
        int T24_StartIndex = input.index();
        try {
            int type = T24;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 10) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:15:7: ( ',' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:15:7: ','
            {
            match(','); if (failed) return ;

            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 10, T24_StartIndex); }
        }
    }
    // $ANTLR end T24


    // $ANTLR start T25
    public void mT25() throws RecognitionException {
        int T25_StartIndex = input.index();
        try {
            int type = T25;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 11) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:16:7: ( ')' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:16:7: ')'
            {
            match(')'); if (failed) return ;

            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 11, T25_StartIndex); }
        }
    }
    // $ANTLR end T25


    // $ANTLR start T26
    public void mT26() throws RecognitionException {
        int T26_StartIndex = input.index();
        try {
            int type = T26;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 12) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:17:7: ( '{' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:17:7: '{'
            {
            match('{'); if (failed) return ;

            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 12, T26_StartIndex); }
        }
    }
    // $ANTLR end T26


    // $ANTLR start T27
    public void mT27() throws RecognitionException {
        int T27_StartIndex = input.index();
        try {
            int type = T27;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 13) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:18:7: ( '}' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:18:7: '}'
            {
            match('}'); if (failed) return ;

            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 13, T27_StartIndex); }
        }
    }
    // $ANTLR end T27


    // $ANTLR start T28
    public void mT28() throws RecognitionException {
        int T28_StartIndex = input.index();
        try {
            int type = T28;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 14) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:19:7: ( 'query' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:19:7: 'query'
            {
            match("query"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 14, T28_StartIndex); }
        }
    }
    // $ANTLR end T28


    // $ANTLR start T29
    public void mT29() throws RecognitionException {
        int T29_StartIndex = input.index();
        try {
            int type = T29;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 15) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:20:7: ( 'end' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:20:7: 'end'
            {
            match("end"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 15, T29_StartIndex); }
        }
    }
    // $ANTLR end T29


    // $ANTLR start T30
    public void mT30() throws RecognitionException {
        int T30_StartIndex = input.index();
        try {
            int type = T30;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 16) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:21:7: ( 'rule' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:21:7: 'rule'
            {
            match("rule"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 16, T30_StartIndex); }
        }
    }
    // $ANTLR end T30


    // $ANTLR start T31
    public void mT31() throws RecognitionException {
        int T31_StartIndex = input.index();
        try {
            int type = T31;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 17) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:22:7: ( 'when' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:22:7: 'when'
            {
            match("when"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 17, T31_StartIndex); }
        }
    }
    // $ANTLR end T31


    // $ANTLR start T32
    public void mT32() throws RecognitionException {
        int T32_StartIndex = input.index();
        try {
            int type = T32;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 18) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:23:7: ( ':' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:23:7: ':'
            {
            match(':'); if (failed) return ;

            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 18, T32_StartIndex); }
        }
    }
    // $ANTLR end T32


    // $ANTLR start T33
    public void mT33() throws RecognitionException {
        int T33_StartIndex = input.index();
        try {
            int type = T33;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 19) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:24:7: ( 'then' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:24:7: 'then'
            {
            match("then"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 19, T33_StartIndex); }
        }
    }
    // $ANTLR end T33


    // $ANTLR start T34
    public void mT34() throws RecognitionException {
        int T34_StartIndex = input.index();
        try {
            int type = T34;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 20) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:25:7: ( 'attributes' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:25:7: 'attributes'
            {
            match("attributes"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 20, T34_StartIndex); }
        }
    }
    // $ANTLR end T34


    // $ANTLR start T35
    public void mT35() throws RecognitionException {
        int T35_StartIndex = input.index();
        try {
            int type = T35;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 21) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:26:7: ( 'salience' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:26:7: 'salience'
            {
            match("salience"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 21, T35_StartIndex); }
        }
    }
    // $ANTLR end T35


    // $ANTLR start T36
    public void mT36() throws RecognitionException {
        int T36_StartIndex = input.index();
        try {
            int type = T36;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 22) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:27:7: ( 'no-loop' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:27:7: 'no-loop'
            {
            match("no-loop"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 22, T36_StartIndex); }
        }
    }
    // $ANTLR end T36


    // $ANTLR start T37
    public void mT37() throws RecognitionException {
        int T37_StartIndex = input.index();
        try {
            int type = T37;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 23) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:28:7: ( 'auto-focus' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:28:7: 'auto-focus'
            {
            match("auto-focus"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 23, T37_StartIndex); }
        }
    }
    // $ANTLR end T37


    // $ANTLR start T38
    public void mT38() throws RecognitionException {
        int T38_StartIndex = input.index();
        try {
            int type = T38;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 24) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:29:7: ( 'activation-group' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:29:7: 'activation-group'
            {
            match("activation-group"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 24, T38_StartIndex); }
        }
    }
    // $ANTLR end T38


    // $ANTLR start T39
    public void mT39() throws RecognitionException {
        int T39_StartIndex = input.index();
        try {
            int type = T39;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 25) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:30:7: ( 'agenda-group' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:30:7: 'agenda-group'
            {
            match("agenda-group"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 25, T39_StartIndex); }
        }
    }
    // $ANTLR end T39


    // $ANTLR start T40
    public void mT40() throws RecognitionException {
        int T40_StartIndex = input.index();
        try {
            int type = T40;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 26) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:31:7: ( 'duration' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:31:7: 'duration'
            {
            match("duration"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 26, T40_StartIndex); }
        }
    }
    // $ANTLR end T40


    // $ANTLR start T41
    public void mT41() throws RecognitionException {
        int T41_StartIndex = input.index();
        try {
            int type = T41;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 27) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:32:7: ( 'or' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:32:7: 'or'
            {
            match("or"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 27, T41_StartIndex); }
        }
    }
    // $ANTLR end T41


    // $ANTLR start T42
    public void mT42() throws RecognitionException {
        int T42_StartIndex = input.index();
        try {
            int type = T42;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 28) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:33:7: ( '||' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:33:7: '||'
            {
            match("||"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 28, T42_StartIndex); }
        }
    }
    // $ANTLR end T42


    // $ANTLR start T43
    public void mT43() throws RecognitionException {
        int T43_StartIndex = input.index();
        try {
            int type = T43;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 29) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:34:7: ( '==' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:34:7: '=='
            {
            match("=="); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 29, T43_StartIndex); }
        }
    }
    // $ANTLR end T43


    // $ANTLR start T44
    public void mT44() throws RecognitionException {
        int T44_StartIndex = input.index();
        try {
            int type = T44;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 30) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:35:7: ( '>' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:35:7: '>'
            {
            match('>'); if (failed) return ;

            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 30, T44_StartIndex); }
        }
    }
    // $ANTLR end T44


    // $ANTLR start T45
    public void mT45() throws RecognitionException {
        int T45_StartIndex = input.index();
        try {
            int type = T45;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 31) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:36:7: ( '>=' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:36:7: '>='
            {
            match(">="); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 31, T45_StartIndex); }
        }
    }
    // $ANTLR end T45


    // $ANTLR start T46
    public void mT46() throws RecognitionException {
        int T46_StartIndex = input.index();
        try {
            int type = T46;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 32) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:37:7: ( '<' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:37:7: '<'
            {
            match('<'); if (failed) return ;

            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 32, T46_StartIndex); }
        }
    }
    // $ANTLR end T46


    // $ANTLR start T47
    public void mT47() throws RecognitionException {
        int T47_StartIndex = input.index();
        try {
            int type = T47;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 33) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:38:7: ( '<=' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:38:7: '<='
            {
            match("<="); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 33, T47_StartIndex); }
        }
    }
    // $ANTLR end T47


    // $ANTLR start T48
    public void mT48() throws RecognitionException {
        int T48_StartIndex = input.index();
        try {
            int type = T48;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 34) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:39:7: ( '!=' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:39:7: '!='
            {
            match("!="); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 34, T48_StartIndex); }
        }
    }
    // $ANTLR end T48


    // $ANTLR start T49
    public void mT49() throws RecognitionException {
        int T49_StartIndex = input.index();
        try {
            int type = T49;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 35) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:40:7: ( 'contains' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:40:7: 'contains'
            {
            match("contains"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 35, T49_StartIndex); }
        }
    }
    // $ANTLR end T49


    // $ANTLR start T50
    public void mT50() throws RecognitionException {
        int T50_StartIndex = input.index();
        try {
            int type = T50;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 36) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:41:7: ( 'matches' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:41:7: 'matches'
            {
            match("matches"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 36, T50_StartIndex); }
        }
    }
    // $ANTLR end T50


    // $ANTLR start T51
    public void mT51() throws RecognitionException {
        int T51_StartIndex = input.index();
        try {
            int type = T51;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 37) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:42:7: ( 'excludes' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:42:7: 'excludes'
            {
            match("excludes"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 37, T51_StartIndex); }
        }
    }
    // $ANTLR end T51


    // $ANTLR start T52
    public void mT52() throws RecognitionException {
        int T52_StartIndex = input.index();
        try {
            int type = T52;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 38) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:43:7: ( 'null' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:43:7: 'null'
            {
            match("null"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 38, T52_StartIndex); }
        }
    }
    // $ANTLR end T52


    // $ANTLR start T53
    public void mT53() throws RecognitionException {
        int T53_StartIndex = input.index();
        try {
            int type = T53;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 39) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:44:7: ( '->' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:44:7: '->'
            {
            match("->"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 39, T53_StartIndex); }
        }
    }
    // $ANTLR end T53


    // $ANTLR start T54
    public void mT54() throws RecognitionException {
        int T54_StartIndex = input.index();
        try {
            int type = T54;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 40) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:45:7: ( 'and' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:45:7: 'and'
            {
            match("and"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 40, T54_StartIndex); }
        }
    }
    // $ANTLR end T54


    // $ANTLR start T55
    public void mT55() throws RecognitionException {
        int T55_StartIndex = input.index();
        try {
            int type = T55;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 41) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:46:7: ( '&&' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:46:7: '&&'
            {
            match("&&"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 41, T55_StartIndex); }
        }
    }
    // $ANTLR end T55


    // $ANTLR start T56
    public void mT56() throws RecognitionException {
        int T56_StartIndex = input.index();
        try {
            int type = T56;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 42) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:47:7: ( 'exists' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:47:7: 'exists'
            {
            match("exists"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 42, T56_StartIndex); }
        }
    }
    // $ANTLR end T56


    // $ANTLR start T57
    public void mT57() throws RecognitionException {
        int T57_StartIndex = input.index();
        try {
            int type = T57;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 43) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:48:7: ( 'not' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:48:7: 'not'
            {
            match("not"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 43, T57_StartIndex); }
        }
    }
    // $ANTLR end T57


    // $ANTLR start T58
    public void mT58() throws RecognitionException {
        int T58_StartIndex = input.index();
        try {
            int type = T58;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 44) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:49:7: ( 'eval' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:49:7: 'eval'
            {
            match("eval"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 44, T58_StartIndex); }
        }
    }
    // $ANTLR end T58


    // $ANTLR start T59
    public void mT59() throws RecognitionException {
        int T59_StartIndex = input.index();
        try {
            int type = T59;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 45) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:50:7: ( '[' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:50:7: '['
            {
            match('['); if (failed) return ;

            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 45, T59_StartIndex); }
        }
    }
    // $ANTLR end T59


    // $ANTLR start T60
    public void mT60() throws RecognitionException {
        int T60_StartIndex = input.index();
        try {
            int type = T60;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 46) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:51:7: ( ']' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:51:7: ']'
            {
            match(']'); if (failed) return ;

            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 46, T60_StartIndex); }
        }
    }
    // $ANTLR end T60


    // $ANTLR start T61
    public void mT61() throws RecognitionException {
        int T61_StartIndex = input.index();
        try {
            int type = T61;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 47) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:52:7: ( 'use' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:52:7: 'use'
            {
            match("use"); if (failed) return ;


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 47, T61_StartIndex); }
        }
    }
    // $ANTLR end T61


    // $ANTLR start MISC
    public void mMISC() throws RecognitionException {
        int MISC_StartIndex = input.index();
        try {
            int type = MISC;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 48) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1012:17: ( '!' | '@' | '$' | '%' | '^' | '&' | '*' | '_' | '-' | '+' | '|' | ',' | '{' | '}' | '[' | ']' | '=' | '/' | '(' | ')' | '\'' | '\\' | '||' | '&&' | '<<<' | '++' | '--' | '>>>' | '==' | '+=' | '=+' | '-=' | '=-' | '*=' | '=*' | '/=' | '=/' | '>>=' )
            int alt1=38;
            switch ( input.LA(1) ) {
            case '!':
                alt1=1;
                break;
            case '@':
                alt1=2;
                break;
            case '$':
                alt1=3;
                break;
            case '%':
                alt1=4;
                break;
            case '^':
                alt1=5;
                break;
            case '&':
                int LA1_6 = input.LA(2);
                if ( LA1_6=='&' ) {
                    alt1=24;
                }
                else {
                    alt1=6;}
                break;
            case '*':
                int LA1_7 = input.LA(2);
                if ( LA1_7=='=' ) {
                    alt1=34;
                }
                else {
                    alt1=7;}
                break;
            case '_':
                alt1=8;
                break;
            case '-':
                switch ( input.LA(2) ) {
                case '-':
                    alt1=27;
                    break;
                case '=':
                    alt1=32;
                    break;
                default:
                    alt1=9;}

                break;
            case '+':
                switch ( input.LA(2) ) {
                case '+':
                    alt1=26;
                    break;
                case '=':
                    alt1=30;
                    break;
                default:
                    alt1=10;}

                break;
            case '|':
                int LA1_11 = input.LA(2);
                if ( LA1_11=='|' ) {
                    alt1=23;
                }
                else {
                    alt1=11;}
                break;
            case ',':
                alt1=12;
                break;
            case '{':
                alt1=13;
                break;
            case '}':
                alt1=14;
                break;
            case '[':
                alt1=15;
                break;
            case ']':
                alt1=16;
                break;
            case '=':
                switch ( input.LA(2) ) {
                case '+':
                    alt1=31;
                    break;
                case '-':
                    alt1=33;
                    break;
                case '=':
                    alt1=29;
                    break;
                case '/':
                    alt1=37;
                    break;
                case '*':
                    alt1=35;
                    break;
                default:
                    alt1=17;}

                break;
            case '/':
                int LA1_18 = input.LA(2);
                if ( LA1_18=='=' ) {
                    alt1=36;
                }
                else {
                    alt1=18;}
                break;
            case '(':
                alt1=19;
                break;
            case ')':
                alt1=20;
                break;
            case '\'':
                alt1=21;
                break;
            case '\\':
                alt1=22;
                break;
            case '<':
                alt1=25;
                break;
            case '>':
                int LA1_24 = input.LA(2);
                if ( LA1_24=='>' ) {
                    int LA1_45 = input.LA(3);
                    if ( LA1_45=='=' ) {
                        alt1=38;
                    }
                    else if ( LA1_45=='>' ) {
                        alt1=28;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("1011:1: MISC : ( \'!\' | \'@\' | \'$\' | \'%\' | \'^\' | \'&\' | \'*\' | \'_\' | \'-\' | \'+\' | \'|\' | \',\' | \'{\' | \'}\' | \'[\' | \']\' | \'=\' | \'/\' | \'(\' | \')\' | \'\\\'\' | \'\\\\\' | \'||\' | \'&&\' | \'<<<\' | \'++\' | \'--\' | \'>>>\' | \'==\' | \'+=\' | \'=+\' | \'-=\' | \'=-\' | \'*=\' | \'=*\' | \'/=\' | \'=/\' | \'>>=\' );", 1, 45, input);

                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1011:1: MISC : ( \'!\' | \'@\' | \'$\' | \'%\' | \'^\' | \'&\' | \'*\' | \'_\' | \'-\' | \'+\' | \'|\' | \',\' | \'{\' | \'}\' | \'[\' | \']\' | \'=\' | \'/\' | \'(\' | \')\' | \'\\\'\' | \'\\\\\' | \'||\' | \'&&\' | \'<<<\' | \'++\' | \'--\' | \'>>>\' | \'==\' | \'+=\' | \'=+\' | \'-=\' | \'=-\' | \'*=\' | \'=*\' | \'/=\' | \'=/\' | \'>>=\' );", 1, 24, input);

                    throw nvae;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1011:1: MISC : ( \'!\' | \'@\' | \'$\' | \'%\' | \'^\' | \'&\' | \'*\' | \'_\' | \'-\' | \'+\' | \'|\' | \',\' | \'{\' | \'}\' | \'[\' | \']\' | \'=\' | \'/\' | \'(\' | \')\' | \'\\\'\' | \'\\\\\' | \'||\' | \'&&\' | \'<<<\' | \'++\' | \'--\' | \'>>>\' | \'==\' | \'+=\' | \'=+\' | \'-=\' | \'=-\' | \'*=\' | \'=*\' | \'/=\' | \'=/\' | \'>>=\' );", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1012:17: '!'
                    {
                    match('!'); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1012:23: '@'
                    {
                    match('@'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1012:29: '$'
                    {
                    match('$'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1012:35: '%'
                    {
                    match('%'); if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1012:41: '^'
                    {
                    match('^'); if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1012:47: '&'
                    {
                    match('&'); if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1012:53: '*'
                    {
                    match('*'); if (failed) return ;

                    }
                    break;
                case 8 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1012:59: '_'
                    {
                    match('_'); if (failed) return ;

                    }
                    break;
                case 9 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1012:65: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;
                case 10 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1012:71: '+'
                    {
                    match('+'); if (failed) return ;

                    }
                    break;
                case 11 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1013:19: '|'
                    {
                    match('|'); if (failed) return ;

                    }
                    break;
                case 12 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1013:25: ','
                    {
                    match(','); if (failed) return ;

                    }
                    break;
                case 13 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1013:31: '{'
                    {
                    match('{'); if (failed) return ;

                    }
                    break;
                case 14 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1013:37: '}'
                    {
                    match('}'); if (failed) return ;

                    }
                    break;
                case 15 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1013:43: '['
                    {
                    match('['); if (failed) return ;

                    }
                    break;
                case 16 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1013:49: ']'
                    {
                    match(']'); if (failed) return ;

                    }
                    break;
                case 17 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1013:55: '='
                    {
                    match('='); if (failed) return ;

                    }
                    break;
                case 18 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1013:61: '/'
                    {
                    match('/'); if (failed) return ;

                    }
                    break;
                case 19 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1013:67: '('
                    {
                    match('('); if (failed) return ;

                    }
                    break;
                case 20 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1013:73: ')'
                    {
                    match(')'); if (failed) return ;

                    }
                    break;
                case 21 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1013:79: '\''
                    {
                    match('\''); if (failed) return ;

                    }
                    break;
                case 22 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1013:86: '\\'
                    {
                    match('\\'); if (failed) return ;

                    }
                    break;
                case 23 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1014:19: '||'
                    {
                    match("||"); if (failed) return ;


                    }
                    break;
                case 24 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1014:26: '&&'
                    {
                    match("&&"); if (failed) return ;


                    }
                    break;
                case 25 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1014:33: '<<<'
                    {
                    match("<<<"); if (failed) return ;


                    }
                    break;
                case 26 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1014:41: '++'
                    {
                    match("++"); if (failed) return ;


                    }
                    break;
                case 27 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1014:48: '--'
                    {
                    match("--"); if (failed) return ;


                    }
                    break;
                case 28 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1014:55: '>>>'
                    {
                    match(">>>"); if (failed) return ;


                    }
                    break;
                case 29 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1014:63: '=='
                    {
                    match("=="); if (failed) return ;


                    }
                    break;
                case 30 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1014:70: '+='
                    {
                    match("+="); if (failed) return ;


                    }
                    break;
                case 31 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1014:77: '=+'
                    {
                    match("=+"); if (failed) return ;


                    }
                    break;
                case 32 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1014:84: '-='
                    {
                    match("-="); if (failed) return ;


                    }
                    break;
                case 33 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1014:91: '=-'
                    {
                    match("=-"); if (failed) return ;


                    }
                    break;
                case 34 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1014:97: '*='
                    {
                    match("*="); if (failed) return ;


                    }
                    break;
                case 35 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1014:104: '=*'
                    {
                    match("=*"); if (failed) return ;


                    }
                    break;
                case 36 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1015:19: '/='
                    {
                    match("/="); if (failed) return ;


                    }
                    break;
                case 37 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1015:26: '=/'
                    {
                    match("=/"); if (failed) return ;


                    }
                    break;
                case 38 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1015:33: '>>='
                    {
                    match(">>="); if (failed) return ;


                    }
                    break;

            }
            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 48, MISC_StartIndex); }
        }
    }
    // $ANTLR end MISC


    // $ANTLR start WS
    public void mWS() throws RecognitionException {
        int WS_StartIndex = input.index();
        try {
            int type = WS;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 49) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1019:17: ( (' '|'\t'|'\f'))
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1019:17: (' '|'\t'|'\f')
            {
            if ( input.LA(1)=='\t'||input.LA(1)=='\f'||input.LA(1)==' ' ) {
                input.consume();
            failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            if ( backtracking==0 ) {
               channel=99; 
            }

            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 49, WS_StartIndex); }
        }
    }
    // $ANTLR end WS


    // $ANTLR start EOL
    public void mEOL() throws RecognitionException {
        int EOL_StartIndex = input.index();
        try {
            int type = EOL;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 50) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1027:17: ( ( ( '\r\n' )=> '\r\n' | '\r' | '\n' ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1027:17: ( ( '\r\n' )=> '\r\n' | '\r' | '\n' )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1027:17: ( ( '\r\n' )=> '\r\n' | '\r' | '\n' )
            int alt2=3;
            int LA2_0 = input.LA(1);
            if ( LA2_0=='\r' ) {
                int LA2_1 = input.LA(2);
                if ( LA2_1=='\n' ) {
                    alt2=1;
                }
                else {
                    alt2=2;}
            }
            else if ( LA2_0=='\n' ) {
                alt2=3;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1027:17: ( ( \'\\r\\n\' )=> \'\\r\\n\' | \'\\r\' | \'\\n\' )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1027:25: ( '\r\n' )=> '\r\n'
                    {

                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1028:25: '\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1029:25: '\n'
                    {
                    match('\n'); if (failed) return ;

                    }
                    break;

            }


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 50, EOL_StartIndex); }
        }
    }
    // $ANTLR end EOL


    // $ANTLR start INT
    public void mINT() throws RecognitionException {
        int INT_StartIndex = input.index();
        try {
            int type = INT;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 51) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1034:17: ( ( '-' )? ( '0' .. '9' )+ )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1034:17: ( '-' )? ( '0' .. '9' )+
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1034:17: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( LA3_0=='-' ) {
                alt3=1;
            }
            else if ( (LA3_0>='0' && LA3_0<='9') ) {
                alt3=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1034:17: ( \'-\' )?", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1034:18: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1034:23: ( '0' .. '9' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);
                if ( (LA4_0>='0' && LA4_0<='9') ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1034:24: '0' .. '9'
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

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 51, INT_StartIndex); }
        }
    }
    // $ANTLR end INT


    // $ANTLR start FLOAT
    public void mFLOAT() throws RecognitionException {
        int FLOAT_StartIndex = input.index();
        try {
            int type = FLOAT;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 52) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1038:17: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1038:17: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1038:17: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( LA5_0=='-' ) {
                alt5=1;
            }
            else if ( (LA5_0>='0' && LA5_0<='9') ) {
                alt5=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1038:17: ( \'-\' )?", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1038:18: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1038:23: ( '0' .. '9' )+
            int cnt6=0;
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);
                if ( (LA6_0>='0' && LA6_0<='9') ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1038:24: '0' .. '9'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1038:39: ( '0' .. '9' )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);
                if ( (LA7_0>='0' && LA7_0<='9') ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1038:40: '0' .. '9'
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

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 52, FLOAT_StartIndex); }
        }
    }
    // $ANTLR end FLOAT


    // $ANTLR start STRING
    public void mSTRING() throws RecognitionException {
        int STRING_StartIndex = input.index();
        try {
            int type = STRING;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 53) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1042:17: ( ( '"' ( options {greedy=false; } : . )* '"' ) | ( '\'' ( options {greedy=false; } : . )* '\'' ) )
            int alt10=2;
            int LA10_0 = input.LA(1);
            if ( LA10_0=='"' ) {
                alt10=1;
            }
            else if ( LA10_0=='\'' ) {
                alt10=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1041:1: STRING : ( ( \'\"\' ( options {greedy=false; } : . )* \'\"\' ) | ( \'\\\'\' ( options {greedy=false; } : . )* \'\\\'\' ) );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1042:17: ( '"' ( options {greedy=false; } : . )* '"' )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1042:17: ( '"' ( options {greedy=false; } : . )* '"' )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1042:18: '"' ( options {greedy=false; } : . )* '"'
                    {
                    match('"'); if (failed) return ;
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1042:22: ( options {greedy=false; } : . )*
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);
                        if ( LA8_0=='"' ) {
                            alt8=2;
                        }
                        else if ( (LA8_0>='\u0000' && LA8_0<='!')||(LA8_0>='#' && LA8_0<='\uFFFE') ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1042:49: .
                    	    {
                    	    matchAny(); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop8;
                        }
                    } while (true);

                    match('"'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1042:61: ( '\'' ( options {greedy=false; } : . )* '\'' )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1042:61: ( '\'' ( options {greedy=false; } : . )* '\'' )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1042:62: '\'' ( options {greedy=false; } : . )* '\''
                    {
                    match('\''); if (failed) return ;
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1042:67: ( options {greedy=false; } : . )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);
                        if ( LA9_0=='\'' ) {
                            alt9=2;
                        }
                        else if ( (LA9_0>='\u0000' && LA9_0<='&')||(LA9_0>='(' && LA9_0<='\uFFFE') ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1042:94: .
                    	    {
                    	    matchAny(); if (failed) return ;

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
            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 53, STRING_StartIndex); }
        }
    }
    // $ANTLR end STRING


    // $ANTLR start BOOL
    public void mBOOL() throws RecognitionException {
        int BOOL_StartIndex = input.index();
        try {
            int type = BOOL;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 54) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1046:17: ( ( 'true' | 'false' ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1046:17: ( 'true' | 'false' )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1046:17: ( 'true' | 'false' )
            int alt11=2;
            int LA11_0 = input.LA(1);
            if ( LA11_0=='t' ) {
                alt11=1;
            }
            else if ( LA11_0=='f' ) {
                alt11=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1046:17: ( \'true\' | \'false\' )", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1046:18: 'true'
                    {
                    match("true"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1046:25: 'false'
                    {
                    match("false"); if (failed) return ;


                    }
                    break;

            }


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 54, BOOL_StartIndex); }
        }
    }
    // $ANTLR end BOOL


    // $ANTLR start ID
    public void mID() throws RecognitionException {
        int ID_StartIndex = input.index();
        try {
            int type = ID;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 55) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1050:17: ( ('a'..'z'|'A'..'Z'|'_'|'$'|'\u00c0'..'\u00ff') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff'))* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1050:17: ('a'..'z'|'A'..'Z'|'_'|'$'|'\u00c0'..'\u00ff') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff'))*
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

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1050:65: ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff'))*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);
                if ( (LA12_0>='0' && LA12_0<='9')||(LA12_0>='A' && LA12_0<='Z')||LA12_0=='_'||(LA12_0>='a' && LA12_0<='z')||(LA12_0>='\u00C0' && LA12_0<='\u00FF') ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1050:66: ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\u00c0'..'\u00ff')
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
            	    break loop12;
                }
            } while (true);


            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 55, ID_StartIndex); }
        }
    }
    // $ANTLR end ID


    // $ANTLR start SH_STYLE_SINGLE_LINE_COMMENT
    public void mSH_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        int SH_STYLE_SINGLE_LINE_COMMENT_StartIndex = input.index();
        try {
            int type = SH_STYLE_SINGLE_LINE_COMMENT;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 56) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1055:17: ( '#' ( options {greedy=false; } : . )* EOL )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1055:17: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (failed) return ;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1055:21: ( options {greedy=false; } : . )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);
                if ( LA13_0=='\r' ) {
                    alt13=2;
                }
                else if ( LA13_0=='\n' ) {
                    alt13=2;
                }
                else if ( (LA13_0>='\u0000' && LA13_0<='\t')||(LA13_0>='\u000B' && LA13_0<='\f')||(LA13_0>='\u000E' && LA13_0<='\uFFFE') ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1055:48: .
            	    {
            	    matchAny(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

            mEOL(); if (failed) return ;
            if ( backtracking==0 ) {
               channel=99; 
            }

            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 56, SH_STYLE_SINGLE_LINE_COMMENT_StartIndex); }
        }
    }
    // $ANTLR end SH_STYLE_SINGLE_LINE_COMMENT


    // $ANTLR start C_STYLE_SINGLE_LINE_COMMENT
    public void mC_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        int C_STYLE_SINGLE_LINE_COMMENT_StartIndex = input.index();
        try {
            int type = C_STYLE_SINGLE_LINE_COMMENT;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 57) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1061:17: ( '//' ( options {greedy=false; } : . )* EOL )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1061:17: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (failed) return ;

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1061:22: ( options {greedy=false; } : . )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);
                if ( LA14_0=='\r' ) {
                    alt14=2;
                }
                else if ( LA14_0=='\n' ) {
                    alt14=2;
                }
                else if ( (LA14_0>='\u0000' && LA14_0<='\t')||(LA14_0>='\u000B' && LA14_0<='\f')||(LA14_0>='\u000E' && LA14_0<='\uFFFE') ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1061:49: .
            	    {
            	    matchAny(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);

            mEOL(); if (failed) return ;
            if ( backtracking==0 ) {
               channel=99; 
            }

            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 57, C_STYLE_SINGLE_LINE_COMMENT_StartIndex); }
        }
    }
    // $ANTLR end C_STYLE_SINGLE_LINE_COMMENT


    // $ANTLR start MULTI_LINE_COMMENT
    public void mMULTI_LINE_COMMENT() throws RecognitionException {
        int MULTI_LINE_COMMENT_StartIndex = input.index();
        try {
            int type = MULTI_LINE_COMMENT;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            if ( backtracking>0 && alreadyParsedRule(input, 58) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1066:17: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1066:17: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (failed) return ;

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1066:22: ( options {greedy=false; } : . )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);
                if ( LA15_0=='*' ) {
                    int LA15_1 = input.LA(2);
                    if ( LA15_1=='/' ) {
                        alt15=2;
                    }
                    else if ( (LA15_1>='\u0000' && LA15_1<='.')||(LA15_1>='0' && LA15_1<='\uFFFE') ) {
                        alt15=1;
                    }


                }
                else if ( (LA15_0>='\u0000' && LA15_0<=')')||(LA15_0>='+' && LA15_0<='\uFFFE') ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1066:48: .
            	    {
            	    matchAny(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

            match("*/"); if (failed) return ;

            if ( backtracking==0 ) {
               channel=99; 
            }

            }

            if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
        }
        finally {
            if ( backtracking>0 ) { memoize(input, 58, MULTI_LINE_COMMENT_StartIndex); }
        }
    }
    // $ANTLR end MULTI_LINE_COMMENT

    public void mTokens() throws RecognitionException {
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:10: ( T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | T33 | T34 | T35 | T36 | T37 | T38 | T39 | T40 | T41 | T42 | T43 | T44 | T45 | T46 | T47 | T48 | T49 | T50 | T51 | T52 | T53 | T54 | T55 | T56 | T57 | T58 | T59 | T60 | T61 | MISC | WS | EOL | INT | FLOAT | STRING | BOOL | ID | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT )
        int alt16=58;
        alt16 = dfa16.predict(input); if (failed) return ;
        switch (alt16) {
            case 1 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:10: T15
                {
                mT15(); if (failed) return ;

                }
                break;
            case 2 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:14: T16
                {
                mT16(); if (failed) return ;

                }
                break;
            case 3 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:18: T17
                {
                mT17(); if (failed) return ;

                }
                break;
            case 4 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:22: T18
                {
                mT18(); if (failed) return ;

                }
                break;
            case 5 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:26: T19
                {
                mT19(); if (failed) return ;

                }
                break;
            case 6 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:30: T20
                {
                mT20(); if (failed) return ;

                }
                break;
            case 7 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:34: T21
                {
                mT21(); if (failed) return ;

                }
                break;
            case 8 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:38: T22
                {
                mT22(); if (failed) return ;

                }
                break;
            case 9 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:42: T23
                {
                mT23(); if (failed) return ;

                }
                break;
            case 10 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:46: T24
                {
                mT24(); if (failed) return ;

                }
                break;
            case 11 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:50: T25
                {
                mT25(); if (failed) return ;

                }
                break;
            case 12 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:54: T26
                {
                mT26(); if (failed) return ;

                }
                break;
            case 13 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:58: T27
                {
                mT27(); if (failed) return ;

                }
                break;
            case 14 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:62: T28
                {
                mT28(); if (failed) return ;

                }
                break;
            case 15 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:66: T29
                {
                mT29(); if (failed) return ;

                }
                break;
            case 16 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:70: T30
                {
                mT30(); if (failed) return ;

                }
                break;
            case 17 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:74: T31
                {
                mT31(); if (failed) return ;

                }
                break;
            case 18 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:78: T32
                {
                mT32(); if (failed) return ;

                }
                break;
            case 19 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:82: T33
                {
                mT33(); if (failed) return ;

                }
                break;
            case 20 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:86: T34
                {
                mT34(); if (failed) return ;

                }
                break;
            case 21 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:90: T35
                {
                mT35(); if (failed) return ;

                }
                break;
            case 22 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:94: T36
                {
                mT36(); if (failed) return ;

                }
                break;
            case 23 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:98: T37
                {
                mT37(); if (failed) return ;

                }
                break;
            case 24 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:102: T38
                {
                mT38(); if (failed) return ;

                }
                break;
            case 25 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:106: T39
                {
                mT39(); if (failed) return ;

                }
                break;
            case 26 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:110: T40
                {
                mT40(); if (failed) return ;

                }
                break;
            case 27 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:114: T41
                {
                mT41(); if (failed) return ;

                }
                break;
            case 28 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:118: T42
                {
                mT42(); if (failed) return ;

                }
                break;
            case 29 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:122: T43
                {
                mT43(); if (failed) return ;

                }
                break;
            case 30 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:126: T44
                {
                mT44(); if (failed) return ;

                }
                break;
            case 31 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:130: T45
                {
                mT45(); if (failed) return ;

                }
                break;
            case 32 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:134: T46
                {
                mT46(); if (failed) return ;

                }
                break;
            case 33 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:138: T47
                {
                mT47(); if (failed) return ;

                }
                break;
            case 34 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:142: T48
                {
                mT48(); if (failed) return ;

                }
                break;
            case 35 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:146: T49
                {
                mT49(); if (failed) return ;

                }
                break;
            case 36 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:150: T50
                {
                mT50(); if (failed) return ;

                }
                break;
            case 37 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:154: T51
                {
                mT51(); if (failed) return ;

                }
                break;
            case 38 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:158: T52
                {
                mT52(); if (failed) return ;

                }
                break;
            case 39 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:162: T53
                {
                mT53(); if (failed) return ;

                }
                break;
            case 40 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:166: T54
                {
                mT54(); if (failed) return ;

                }
                break;
            case 41 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:170: T55
                {
                mT55(); if (failed) return ;

                }
                break;
            case 42 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:174: T56
                {
                mT56(); if (failed) return ;

                }
                break;
            case 43 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:178: T57
                {
                mT57(); if (failed) return ;

                }
                break;
            case 44 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:182: T58
                {
                mT58(); if (failed) return ;

                }
                break;
            case 45 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:186: T59
                {
                mT59(); if (failed) return ;

                }
                break;
            case 46 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:190: T60
                {
                mT60(); if (failed) return ;

                }
                break;
            case 47 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:194: T61
                {
                mT61(); if (failed) return ;

                }
                break;
            case 48 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:198: MISC
                {
                mMISC(); if (failed) return ;

                }
                break;
            case 49 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:203: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 50 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:206: EOL
                {
                mEOL(); if (failed) return ;

                }
                break;
            case 51 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:210: INT
                {
                mINT(); if (failed) return ;

                }
                break;
            case 52 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:214: FLOAT
                {
                mFLOAT(); if (failed) return ;

                }
                break;
            case 53 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:220: STRING
                {
                mSTRING(); if (failed) return ;

                }
                break;
            case 54 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:227: BOOL
                {
                mBOOL(); if (failed) return ;

                }
                break;
            case 55 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:232: ID
                {
                mID(); if (failed) return ;

                }
                break;
            case 56 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:235: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 57 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:264: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 58 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1:292: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (failed) return ;

                }
                break;

        }

    }


    // $ANTLR start Synpred1_fragment
    public void mSynpred1_fragment() throws RecognitionException {
        int Synpred1_fragment_StartIndex = input.index();
        try {
            if ( backtracking>0 && alreadyParsedRule(input, 60) ) { return ; }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1027:25: ( '\r\n' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1027:27: '\r\n'
            {
            match("\r\n"); if (failed) return ;


            }

        }
        finally {
            if ( backtracking>0 ) { memoize(input, 60, Synpred1_fragment_StartIndex); }
        }
    }
    // $ANTLR end Synpred1_fragment

    class Synpred1Ptr implements GrammarFragmentPtr {
        public void invoke() throws RecognitionException {mSynpred1_fragment();}
    }
    Synpred1Ptr Synpred1 = new Synpred1Ptr();


    protected DFA16 dfa16 = new DFA16();
    class DFA16 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s1 = new DFA.State() {{alt=1;}};
        DFA.State s466 = new DFA.State() {{alt=2;}};
        DFA.State s50 = new DFA.State() {{alt=55;}};
        DFA.State s427 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_427 = input.LA(1);
                if ( (LA16_427>='0' && LA16_427<='9')||(LA16_427>='A' && LA16_427<='Z')||LA16_427=='_'||(LA16_427>='a' && LA16_427<='z')||(LA16_427>='\u00C0' && LA16_427<='\u00FF') ) {return s50;}
                return s466;

            }
        };
        DFA.State s381 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_381 = input.LA(1);
                if ( LA16_381=='e' ) {return s427;}
                return s50;

            }
        };
        DFA.State s318 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_318 = input.LA(1);
                if ( LA16_318=='g' ) {return s381;}
                return s50;

            }
        };
        DFA.State s241 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_241 = input.LA(1);
                if ( LA16_241=='a' ) {return s318;}
                return s50;

            }
        };
        DFA.State s158 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_158 = input.LA(1);
                if ( LA16_158=='k' ) {return s241;}
                return s50;

            }
        };
        DFA.State s52 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_52 = input.LA(1);
                if ( LA16_52=='c' ) {return s158;}
                return s50;

            }
        };
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_2 = input.LA(1);
                if ( LA16_2=='a' ) {return s52;}
                return s50;

            }
        };
        DFA.State s430 = new DFA.State() {{alt=3;}};
        DFA.State s384 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_384 = input.LA(1);
                if ( (LA16_384>='0' && LA16_384<='9')||(LA16_384>='A' && LA16_384<='Z')||LA16_384=='_'||(LA16_384>='a' && LA16_384<='z')||(LA16_384>='\u00C0' && LA16_384<='\u00FF') ) {return s50;}
                return s430;

            }
        };
        DFA.State s321 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_321 = input.LA(1);
                if ( LA16_321=='t' ) {return s384;}
                return s50;

            }
        };
        DFA.State s244 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_244 = input.LA(1);
                if ( LA16_244=='r' ) {return s321;}
                return s50;

            }
        };
        DFA.State s161 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_161 = input.LA(1);
                if ( LA16_161=='o' ) {return s244;}
                return s50;

            }
        };
        DFA.State s55 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_55 = input.LA(1);
                if ( LA16_55=='p' ) {return s161;}
                return s50;

            }
        };
        DFA.State s3 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_3 = input.LA(1);
                if ( LA16_3=='m' ) {return s55;}
                return s50;

            }
        };
        DFA.State s58 = new DFA.State() {{alt=5;}};
        DFA.State s59 = new DFA.State() {{alt=4;}};
        DFA.State s4 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_4 = input.LA(1);
                if ( LA16_4=='*' ) {return s58;}
                return s59;

            }
        };
        DFA.State s247 = new DFA.State() {{alt=15;}};
        DFA.State s164 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_164 = input.LA(1);
                if ( (LA16_164>='0' && LA16_164<='9')||(LA16_164>='A' && LA16_164<='Z')||LA16_164=='_'||(LA16_164>='a' && LA16_164<='z')||(LA16_164>='\u00C0' && LA16_164<='\u00FF') ) {return s50;}
                return s247;

            }
        };
        DFA.State s60 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_60 = input.LA(1);
                if ( LA16_60=='d' ) {return s164;}
                return s50;

            }
        };
        DFA.State s432 = new DFA.State() {{alt=42;}};
        DFA.State s387 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_387 = input.LA(1);
                if ( (LA16_387>='0' && LA16_387<='9')||(LA16_387>='A' && LA16_387<='Z')||LA16_387=='_'||(LA16_387>='a' && LA16_387<='z')||(LA16_387>='\u00C0' && LA16_387<='\u00FF') ) {return s50;}
                return s432;

            }
        };
        DFA.State s324 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_324 = input.LA(1);
                if ( LA16_324=='s' ) {return s387;}
                return s50;

            }
        };
        DFA.State s249 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_249 = input.LA(1);
                if ( LA16_249=='t' ) {return s324;}
                return s50;

            }
        };
        DFA.State s167 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_167 = input.LA(1);
                if ( LA16_167=='s' ) {return s249;}
                return s50;

            }
        };
        DFA.State s494 = new DFA.State() {{alt=37;}};
        DFA.State s468 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_468 = input.LA(1);
                if ( (LA16_468>='0' && LA16_468<='9')||(LA16_468>='A' && LA16_468<='Z')||LA16_468=='_'||(LA16_468>='a' && LA16_468<='z')||(LA16_468>='\u00C0' && LA16_468<='\u00FF') ) {return s50;}
                return s494;

            }
        };
        DFA.State s434 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_434 = input.LA(1);
                if ( LA16_434=='s' ) {return s468;}
                return s50;

            }
        };
        DFA.State s390 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_390 = input.LA(1);
                if ( LA16_390=='e' ) {return s434;}
                return s50;

            }
        };
        DFA.State s327 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_327 = input.LA(1);
                if ( LA16_327=='d' ) {return s390;}
                return s50;

            }
        };
        DFA.State s252 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_252 = input.LA(1);
                if ( LA16_252=='u' ) {return s327;}
                return s50;

            }
        };
        DFA.State s168 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_168 = input.LA(1);
                if ( LA16_168=='l' ) {return s252;}
                return s50;

            }
        };
        DFA.State s496 = new DFA.State() {{alt=6;}};
        DFA.State s471 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_471 = input.LA(1);
                if ( (LA16_471>='0' && LA16_471<='9')||(LA16_471>='A' && LA16_471<='Z')||LA16_471=='_'||(LA16_471>='a' && LA16_471<='z')||(LA16_471>='\u00C0' && LA16_471<='\u00FF') ) {return s50;}
                return s496;

            }
        };
        DFA.State s437 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_437 = input.LA(1);
                if ( LA16_437=='r' ) {return s471;}
                return s50;

            }
        };
        DFA.State s393 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_393 = input.LA(1);
                if ( LA16_393=='e' ) {return s437;}
                return s50;

            }
        };
        DFA.State s330 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_330 = input.LA(1);
                if ( LA16_330=='d' ) {return s393;}
                return s50;

            }
        };
        DFA.State s255 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_255 = input.LA(1);
                if ( LA16_255=='n' ) {return s330;}
                return s50;

            }
        };
        DFA.State s169 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_169 = input.LA(1);
                if ( LA16_169=='a' ) {return s255;}
                return s50;

            }
        };
        DFA.State s61 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 'i':
                    return s167;

                case 'c':
                    return s168;

                case 'p':
                    return s169;

                default:
                    return s50;
        	        }
            }
        };
        DFA.State s333 = new DFA.State() {{alt=44;}};
        DFA.State s258 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_258 = input.LA(1);
                if ( (LA16_258>='0' && LA16_258<='9')||(LA16_258>='A' && LA16_258<='Z')||LA16_258=='_'||(LA16_258>='a' && LA16_258<='z')||(LA16_258>='\u00C0' && LA16_258<='\u00FF') ) {return s50;}
                return s333;

            }
        };
        DFA.State s172 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_172 = input.LA(1);
                if ( LA16_172=='l' ) {return s258;}
                return s50;

            }
        };
        DFA.State s62 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_62 = input.LA(1);
                if ( LA16_62=='a' ) {return s172;}
                return s50;

            }
        };
        DFA.State s5 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 'n':
                    return s60;

                case 'x':
                    return s61;

                case 'v':
                    return s62;

                default:
                    return s50;
        	        }
            }
        };
        DFA.State s440 = new DFA.State() {{alt=7;}};
        DFA.State s396 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_396 = input.LA(1);
                if ( (LA16_396>='0' && LA16_396<='9')||(LA16_396>='A' && LA16_396<='Z')||LA16_396=='_'||(LA16_396>='a' && LA16_396<='z')||(LA16_396>='\u00C0' && LA16_396<='\u00FF') ) {return s50;}
                return s440;

            }
        };
        DFA.State s335 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_335 = input.LA(1);
                if ( LA16_335=='l' ) {return s396;}
                return s50;

            }
        };
        DFA.State s261 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_261 = input.LA(1);
                if ( LA16_261=='a' ) {return s335;}
                return s50;

            }
        };
        DFA.State s175 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_175 = input.LA(1);
                if ( LA16_175=='b' ) {return s261;}
                return s50;

            }
        };
        DFA.State s65 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_65 = input.LA(1);
                if ( LA16_65=='o' ) {return s175;}
                return s50;

            }
        };
        DFA.State s6 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_6 = input.LA(1);
                if ( LA16_6=='l' ) {return s65;}
                return s50;

            }
        };
        DFA.State s498 = new DFA.State() {{alt=8;}};
        DFA.State s474 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_474 = input.LA(1);
                if ( (LA16_474>='0' && LA16_474<='9')||(LA16_474>='A' && LA16_474<='Z')||LA16_474=='_'||(LA16_474>='a' && LA16_474<='z')||(LA16_474>='\u00C0' && LA16_474<='\u00FF') ) {return s50;}
                return s498;

            }
        };
        DFA.State s442 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_442 = input.LA(1);
                if ( LA16_442=='n' ) {return s474;}
                return s50;

            }
        };
        DFA.State s399 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_399 = input.LA(1);
                if ( LA16_399=='o' ) {return s442;}
                return s50;

            }
        };
        DFA.State s338 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_338 = input.LA(1);
                if ( LA16_338=='i' ) {return s399;}
                return s50;

            }
        };
        DFA.State s264 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_264 = input.LA(1);
                if ( LA16_264=='t' ) {return s338;}
                return s50;

            }
        };
        DFA.State s178 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_178 = input.LA(1);
                if ( LA16_178=='c' ) {return s264;}
                return s50;

            }
        };
        DFA.State s68 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_68 = input.LA(1);
                if ( LA16_68=='n' ) {return s178;}
                return s50;

            }
        };
        DFA.State s353 = new DFA.State() {{alt=54;}};
        DFA.State s341 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_341 = input.LA(1);
                if ( (LA16_341>='0' && LA16_341<='9')||(LA16_341>='A' && LA16_341<='Z')||LA16_341=='_'||(LA16_341>='a' && LA16_341<='z')||(LA16_341>='\u00C0' && LA16_341<='\u00FF') ) {return s50;}
                return s353;

            }
        };
        DFA.State s267 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_267 = input.LA(1);
                if ( LA16_267=='e' ) {return s341;}
                return s50;

            }
        };
        DFA.State s181 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_181 = input.LA(1);
                if ( LA16_181=='s' ) {return s267;}
                return s50;

            }
        };
        DFA.State s69 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_69 = input.LA(1);
                if ( LA16_69=='l' ) {return s181;}
                return s50;

            }
        };
        DFA.State s7 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 'u':
                    return s68;

                case 'a':
                    return s69;

                default:
                    return s50;
        	        }
            }
        };
        DFA.State s72 = new DFA.State() {{alt=9;}};
        DFA.State s8 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_8 = input.LA(1);
                return s72;

            }
        };
        DFA.State s73 = new DFA.State() {{alt=10;}};
        DFA.State s9 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_9 = input.LA(1);
                return s73;

            }
        };
        DFA.State s74 = new DFA.State() {{alt=11;}};
        DFA.State s10 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_10 = input.LA(1);
                return s74;

            }
        };
        DFA.State s75 = new DFA.State() {{alt=12;}};
        DFA.State s11 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_11 = input.LA(1);
                return s75;

            }
        };
        DFA.State s76 = new DFA.State() {{alt=13;}};
        DFA.State s12 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_12 = input.LA(1);
                return s76;

            }
        };
        DFA.State s404 = new DFA.State() {{alt=14;}};
        DFA.State s344 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_344 = input.LA(1);
                if ( (LA16_344>='0' && LA16_344<='9')||(LA16_344>='A' && LA16_344<='Z')||LA16_344=='_'||(LA16_344>='a' && LA16_344<='z')||(LA16_344>='\u00C0' && LA16_344<='\u00FF') ) {return s50;}
                return s404;

            }
        };
        DFA.State s270 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_270 = input.LA(1);
                if ( LA16_270=='y' ) {return s344;}
                return s50;

            }
        };
        DFA.State s184 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_184 = input.LA(1);
                if ( LA16_184=='r' ) {return s270;}
                return s50;

            }
        };
        DFA.State s77 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_77 = input.LA(1);
                if ( LA16_77=='e' ) {return s184;}
                return s50;

            }
        };
        DFA.State s13 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_13 = input.LA(1);
                if ( LA16_13=='u' ) {return s77;}
                return s50;

            }
        };
        DFA.State s347 = new DFA.State() {{alt=16;}};
        DFA.State s273 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_273 = input.LA(1);
                if ( (LA16_273>='0' && LA16_273<='9')||(LA16_273>='A' && LA16_273<='Z')||LA16_273=='_'||(LA16_273>='a' && LA16_273<='z')||(LA16_273>='\u00C0' && LA16_273<='\u00FF') ) {return s50;}
                return s347;

            }
        };
        DFA.State s187 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_187 = input.LA(1);
                if ( LA16_187=='e' ) {return s273;}
                return s50;

            }
        };
        DFA.State s80 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_80 = input.LA(1);
                if ( LA16_80=='l' ) {return s187;}
                return s50;

            }
        };
        DFA.State s14 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_14 = input.LA(1);
                if ( LA16_14=='u' ) {return s80;}
                return s50;

            }
        };
        DFA.State s349 = new DFA.State() {{alt=17;}};
        DFA.State s276 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_276 = input.LA(1);
                if ( (LA16_276>='0' && LA16_276<='9')||(LA16_276>='A' && LA16_276<='Z')||LA16_276=='_'||(LA16_276>='a' && LA16_276<='z')||(LA16_276>='\u00C0' && LA16_276<='\u00FF') ) {return s50;}
                return s349;

            }
        };
        DFA.State s190 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_190 = input.LA(1);
                if ( LA16_190=='n' ) {return s276;}
                return s50;

            }
        };
        DFA.State s83 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_83 = input.LA(1);
                if ( LA16_83=='e' ) {return s190;}
                return s50;

            }
        };
        DFA.State s15 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_15 = input.LA(1);
                if ( LA16_15=='h' ) {return s83;}
                return s50;

            }
        };
        DFA.State s16 = new DFA.State() {{alt=18;}};
        DFA.State s351 = new DFA.State() {{alt=19;}};
        DFA.State s279 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_279 = input.LA(1);
                if ( (LA16_279>='0' && LA16_279<='9')||(LA16_279>='A' && LA16_279<='Z')||LA16_279=='_'||(LA16_279>='a' && LA16_279<='z')||(LA16_279>='\u00C0' && LA16_279<='\u00FF') ) {return s50;}
                return s351;

            }
        };
        DFA.State s193 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_193 = input.LA(1);
                if ( LA16_193=='n' ) {return s279;}
                return s50;

            }
        };
        DFA.State s86 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_86 = input.LA(1);
                if ( LA16_86=='e' ) {return s193;}
                return s50;

            }
        };
        DFA.State s282 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_282 = input.LA(1);
                if ( (LA16_282>='0' && LA16_282<='9')||(LA16_282>='A' && LA16_282<='Z')||LA16_282=='_'||(LA16_282>='a' && LA16_282<='z')||(LA16_282>='\u00C0' && LA16_282<='\u00FF') ) {return s50;}
                return s353;

            }
        };
        DFA.State s196 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_196 = input.LA(1);
                if ( LA16_196=='e' ) {return s282;}
                return s50;

            }
        };
        DFA.State s87 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_87 = input.LA(1);
                if ( LA16_87=='u' ) {return s196;}
                return s50;

            }
        };
        DFA.State s17 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 'h':
                    return s86;

                case 'r':
                    return s87;

                default:
                    return s50;
        	        }
            }
        };
        DFA.State s445 = new DFA.State() {{alt=25;}};
        DFA.State s406 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_406 = input.LA(1);
                if ( LA16_406=='-' ) {return s445;}
                return s50;

            }
        };
        DFA.State s355 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_355 = input.LA(1);
                if ( LA16_355=='a' ) {return s406;}
                return s50;

            }
        };
        DFA.State s285 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_285 = input.LA(1);
                if ( LA16_285=='d' ) {return s355;}
                return s50;

            }
        };
        DFA.State s199 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_199 = input.LA(1);
                if ( LA16_199=='n' ) {return s285;}
                return s50;

            }
        };
        DFA.State s90 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_90 = input.LA(1);
                if ( LA16_90=='e' ) {return s199;}
                return s50;

            }
        };
        DFA.State s518 = new DFA.State() {{alt=24;}};
        DFA.State s512 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_512 = input.LA(1);
                if ( LA16_512=='-' ) {return s518;}
                return s50;

            }
        };
        DFA.State s500 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_500 = input.LA(1);
                if ( LA16_500=='n' ) {return s512;}
                return s50;

            }
        };
        DFA.State s477 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_477 = input.LA(1);
                if ( LA16_477=='o' ) {return s500;}
                return s50;

            }
        };
        DFA.State s448 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_448 = input.LA(1);
                if ( LA16_448=='i' ) {return s477;}
                return s50;

            }
        };
        DFA.State s409 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_409 = input.LA(1);
                if ( LA16_409=='t' ) {return s448;}
                return s50;

            }
        };
        DFA.State s358 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_358 = input.LA(1);
                if ( LA16_358=='a' ) {return s409;}
                return s50;

            }
        };
        DFA.State s288 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_288 = input.LA(1);
                if ( LA16_288=='v' ) {return s358;}
                return s50;

            }
        };
        DFA.State s202 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_202 = input.LA(1);
                if ( LA16_202=='i' ) {return s288;}
                return s50;

            }
        };
        DFA.State s91 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_91 = input.LA(1);
                if ( LA16_91=='t' ) {return s202;}
                return s50;

            }
        };
        DFA.State s291 = new DFA.State() {{alt=40;}};
        DFA.State s205 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_205 = input.LA(1);
                if ( (LA16_205>='0' && LA16_205<='9')||(LA16_205>='A' && LA16_205<='Z')||LA16_205=='_'||(LA16_205>='a' && LA16_205<='z')||(LA16_205>='\u00C0' && LA16_205<='\u00FF') ) {return s50;}
                return s291;

            }
        };
        DFA.State s92 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_92 = input.LA(1);
                if ( LA16_92=='d' ) {return s205;}
                return s50;

            }
        };
        DFA.State s361 = new DFA.State() {{alt=23;}};
        DFA.State s293 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_293 = input.LA(1);
                if ( LA16_293=='-' ) {return s361;}
                return s50;

            }
        };
        DFA.State s208 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_208 = input.LA(1);
                if ( LA16_208=='o' ) {return s293;}
                return s50;

            }
        };
        DFA.State s93 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_93 = input.LA(1);
                if ( LA16_93=='t' ) {return s208;}
                return s50;

            }
        };
        DFA.State s521 = new DFA.State() {{alt=20;}};
        DFA.State s515 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_515 = input.LA(1);
                if ( (LA16_515>='0' && LA16_515<='9')||(LA16_515>='A' && LA16_515<='Z')||LA16_515=='_'||(LA16_515>='a' && LA16_515<='z')||(LA16_515>='\u00C0' && LA16_515<='\u00FF') ) {return s50;}
                return s521;

            }
        };
        DFA.State s503 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_503 = input.LA(1);
                if ( LA16_503=='s' ) {return s515;}
                return s50;

            }
        };
        DFA.State s480 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_480 = input.LA(1);
                if ( LA16_480=='e' ) {return s503;}
                return s50;

            }
        };
        DFA.State s451 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_451 = input.LA(1);
                if ( LA16_451=='t' ) {return s480;}
                return s50;

            }
        };
        DFA.State s412 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_412 = input.LA(1);
                if ( LA16_412=='u' ) {return s451;}
                return s50;

            }
        };
        DFA.State s364 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_364 = input.LA(1);
                if ( LA16_364=='b' ) {return s412;}
                return s50;

            }
        };
        DFA.State s296 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_296 = input.LA(1);
                if ( LA16_296=='i' ) {return s364;}
                return s50;

            }
        };
        DFA.State s211 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_211 = input.LA(1);
                if ( LA16_211=='r' ) {return s296;}
                return s50;

            }
        };
        DFA.State s94 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_94 = input.LA(1);
                if ( LA16_94=='t' ) {return s211;}
                return s50;

            }
        };
        DFA.State s18 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 'g':
                    return s90;

                case 'c':
                    return s91;

                case 'n':
                    return s92;

                case 'u':
                    return s93;

                case 't':
                    return s94;

                default:
                    return s50;
        	        }
            }
        };
        DFA.State s506 = new DFA.State() {{alt=21;}};
        DFA.State s483 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_483 = input.LA(1);
                if ( (LA16_483>='0' && LA16_483<='9')||(LA16_483>='A' && LA16_483<='Z')||LA16_483=='_'||(LA16_483>='a' && LA16_483<='z')||(LA16_483>='\u00C0' && LA16_483<='\u00FF') ) {return s50;}
                return s506;

            }
        };
        DFA.State s454 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_454 = input.LA(1);
                if ( LA16_454=='e' ) {return s483;}
                return s50;

            }
        };
        DFA.State s415 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_415 = input.LA(1);
                if ( LA16_415=='c' ) {return s454;}
                return s50;

            }
        };
        DFA.State s367 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_367 = input.LA(1);
                if ( LA16_367=='n' ) {return s415;}
                return s50;

            }
        };
        DFA.State s299 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_299 = input.LA(1);
                if ( LA16_299=='e' ) {return s367;}
                return s50;

            }
        };
        DFA.State s214 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_214 = input.LA(1);
                if ( LA16_214=='i' ) {return s299;}
                return s50;

            }
        };
        DFA.State s97 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_97 = input.LA(1);
                if ( LA16_97=='l' ) {return s214;}
                return s50;

            }
        };
        DFA.State s19 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_19 = input.LA(1);
                if ( LA16_19=='a' ) {return s97;}
                return s50;

            }
        };
        DFA.State s217 = new DFA.State() {{alt=22;}};
        DFA.State s302 = new DFA.State() {{alt=43;}};
        DFA.State s218 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_218 = input.LA(1);
                if ( (LA16_218>='0' && LA16_218<='9')||(LA16_218>='A' && LA16_218<='Z')||LA16_218=='_'||(LA16_218>='a' && LA16_218<='z')||(LA16_218>='\u00C0' && LA16_218<='\u00FF') ) {return s50;}
                return s302;

            }
        };
        DFA.State s100 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case '-':
                    return s217;

                case 't':
                    return s218;

                default:
                    return s50;
        	        }
            }
        };
        DFA.State s370 = new DFA.State() {{alt=38;}};
        DFA.State s304 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_304 = input.LA(1);
                if ( (LA16_304>='0' && LA16_304<='9')||(LA16_304>='A' && LA16_304<='Z')||LA16_304=='_'||(LA16_304>='a' && LA16_304<='z')||(LA16_304>='\u00C0' && LA16_304<='\u00FF') ) {return s50;}
                return s370;

            }
        };
        DFA.State s221 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_221 = input.LA(1);
                if ( LA16_221=='l' ) {return s304;}
                return s50;

            }
        };
        DFA.State s101 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_101 = input.LA(1);
                if ( LA16_101=='l' ) {return s221;}
                return s50;

            }
        };
        DFA.State s20 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 'o':
                    return s100;

                case 'u':
                    return s101;

                default:
                    return s50;
        	        }
            }
        };
        DFA.State s508 = new DFA.State() {{alt=26;}};
        DFA.State s486 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_486 = input.LA(1);
                if ( (LA16_486>='0' && LA16_486<='9')||(LA16_486>='A' && LA16_486<='Z')||LA16_486=='_'||(LA16_486>='a' && LA16_486<='z')||(LA16_486>='\u00C0' && LA16_486<='\u00FF') ) {return s50;}
                return s508;

            }
        };
        DFA.State s457 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_457 = input.LA(1);
                if ( LA16_457=='n' ) {return s486;}
                return s50;

            }
        };
        DFA.State s418 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_418 = input.LA(1);
                if ( LA16_418=='o' ) {return s457;}
                return s50;

            }
        };
        DFA.State s372 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_372 = input.LA(1);
                if ( LA16_372=='i' ) {return s418;}
                return s50;

            }
        };
        DFA.State s307 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_307 = input.LA(1);
                if ( LA16_307=='t' ) {return s372;}
                return s50;

            }
        };
        DFA.State s224 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_224 = input.LA(1);
                if ( LA16_224=='a' ) {return s307;}
                return s50;

            }
        };
        DFA.State s104 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_104 = input.LA(1);
                if ( LA16_104=='r' ) {return s224;}
                return s50;

            }
        };
        DFA.State s21 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_21 = input.LA(1);
                if ( LA16_21=='u' ) {return s104;}
                return s50;

            }
        };
        DFA.State s227 = new DFA.State() {{alt=27;}};
        DFA.State s107 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_107 = input.LA(1);
                if ( (LA16_107>='0' && LA16_107<='9')||(LA16_107>='A' && LA16_107<='Z')||LA16_107=='_'||(LA16_107>='a' && LA16_107<='z')||(LA16_107>='\u00C0' && LA16_107<='\u00FF') ) {return s50;}
                return s227;

            }
        };
        DFA.State s22 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_22 = input.LA(1);
                if ( LA16_22=='r' ) {return s107;}
                return s50;

            }
        };
        DFA.State s229 = new DFA.State() {{alt=28;}};
        DFA.State s110 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_110 = input.LA(1);
                return s229;

            }
        };
        DFA.State s35 = new DFA.State() {{alt=48;}};
        DFA.State s23 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_23 = input.LA(1);
                if ( LA16_23=='|' ) {return s110;}
                return s35;

            }
        };
        DFA.State s230 = new DFA.State() {{alt=29;}};
        DFA.State s112 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_112 = input.LA(1);
                return s230;

            }
        };
        DFA.State s24 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_24 = input.LA(1);
                if ( LA16_24=='=' ) {return s112;}
                return s35;

            }
        };
        DFA.State s118 = new DFA.State() {{alt=31;}};
        DFA.State s120 = new DFA.State() {{alt=30;}};
        DFA.State s25 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case '=':
                    return s118;

                case '>':
                    return s35;

                default:
                    return s120;
        	        }
            }
        };
        DFA.State s121 = new DFA.State() {{alt=33;}};
        DFA.State s123 = new DFA.State() {{alt=32;}};
        DFA.State s26 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case '=':
                    return s121;

                case '<':
                    return s35;

                default:
                    return s123;
        	        }
            }
        };
        DFA.State s124 = new DFA.State() {{alt=34;}};
        DFA.State s27 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_27 = input.LA(1);
                if ( LA16_27=='=' ) {return s124;}
                return s35;

            }
        };
        DFA.State s510 = new DFA.State() {{alt=35;}};
        DFA.State s489 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_489 = input.LA(1);
                if ( (LA16_489>='0' && LA16_489<='9')||(LA16_489>='A' && LA16_489<='Z')||LA16_489=='_'||(LA16_489>='a' && LA16_489<='z')||(LA16_489>='\u00C0' && LA16_489<='\u00FF') ) {return s50;}
                return s510;

            }
        };
        DFA.State s460 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_460 = input.LA(1);
                if ( LA16_460=='s' ) {return s489;}
                return s50;

            }
        };
        DFA.State s421 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_421 = input.LA(1);
                if ( LA16_421=='n' ) {return s460;}
                return s50;

            }
        };
        DFA.State s375 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_375 = input.LA(1);
                if ( LA16_375=='i' ) {return s421;}
                return s50;

            }
        };
        DFA.State s310 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_310 = input.LA(1);
                if ( LA16_310=='a' ) {return s375;}
                return s50;

            }
        };
        DFA.State s231 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_231 = input.LA(1);
                if ( LA16_231=='t' ) {return s310;}
                return s50;

            }
        };
        DFA.State s126 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_126 = input.LA(1);
                if ( LA16_126=='n' ) {return s231;}
                return s50;

            }
        };
        DFA.State s28 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_28 = input.LA(1);
                if ( LA16_28=='o' ) {return s126;}
                return s50;

            }
        };
        DFA.State s492 = new DFA.State() {{alt=36;}};
        DFA.State s463 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_463 = input.LA(1);
                if ( (LA16_463>='0' && LA16_463<='9')||(LA16_463>='A' && LA16_463<='Z')||LA16_463=='_'||(LA16_463>='a' && LA16_463<='z')||(LA16_463>='\u00C0' && LA16_463<='\u00FF') ) {return s50;}
                return s492;

            }
        };
        DFA.State s424 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_424 = input.LA(1);
                if ( LA16_424=='s' ) {return s463;}
                return s50;

            }
        };
        DFA.State s378 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_378 = input.LA(1);
                if ( LA16_378=='e' ) {return s424;}
                return s50;

            }
        };
        DFA.State s313 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_313 = input.LA(1);
                if ( LA16_313=='h' ) {return s378;}
                return s50;

            }
        };
        DFA.State s234 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_234 = input.LA(1);
                if ( LA16_234=='c' ) {return s313;}
                return s50;

            }
        };
        DFA.State s129 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_129 = input.LA(1);
                if ( LA16_129=='t' ) {return s234;}
                return s50;

            }
        };
        DFA.State s29 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_29 = input.LA(1);
                if ( LA16_29=='a' ) {return s129;}
                return s50;

            }
        };
        DFA.State s134 = new DFA.State() {{alt=39;}};
        DFA.State s155 = new DFA.State() {{alt=52;}};
        DFA.State s157 = new DFA.State() {{alt=51;}};
        DFA.State s48 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case '.':
                    return s155;

                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    return s48;

                default:
                    return s157;
        	        }
            }
        };
        DFA.State s30 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case '>':
                    return s134;

                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    return s48;

                default:
                    return s35;
        	        }
            }
        };
        DFA.State s237 = new DFA.State() {{alt=41;}};
        DFA.State s137 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_137 = input.LA(1);
                return s237;

            }
        };
        DFA.State s31 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_31 = input.LA(1);
                if ( LA16_31=='&' ) {return s137;}
                return s35;

            }
        };
        DFA.State s139 = new DFA.State() {{alt=45;}};
        DFA.State s32 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_32 = input.LA(1);
                return s139;

            }
        };
        DFA.State s140 = new DFA.State() {{alt=46;}};
        DFA.State s33 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_33 = input.LA(1);
                return s140;

            }
        };
        DFA.State s316 = new DFA.State() {{alt=47;}};
        DFA.State s238 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_238 = input.LA(1);
                if ( (LA16_238>='0' && LA16_238<='9')||(LA16_238>='A' && LA16_238<='Z')||LA16_238=='_'||(LA16_238>='a' && LA16_238<='z')||(LA16_238>='\u00C0' && LA16_238<='\u00FF') ) {return s50;}
                return s316;

            }
        };
        DFA.State s141 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_141 = input.LA(1);
                if ( LA16_141=='e' ) {return s238;}
                return s50;

            }
        };
        DFA.State s34 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_34 = input.LA(1);
                if ( LA16_34=='s' ) {return s141;}
                return s50;

            }
        };
        DFA.State s144 = new DFA.State() {{alt=48;}};
        DFA.State s36 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_36 = input.LA(1);
                if ( (LA16_36>='0' && LA16_36<='9')||(LA16_36>='A' && LA16_36<='Z')||LA16_36=='_'||(LA16_36>='a' && LA16_36<='z')||(LA16_36>='\u00C0' && LA16_36<='\u00FF') ) {return s50;}
                return s144;

            }
        };
        DFA.State s40 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_40 = input.LA(1);
                if ( (LA16_40>='0' && LA16_40<='9')||(LA16_40>='A' && LA16_40<='Z')||LA16_40=='_'||(LA16_40>='a' && LA16_40<='z')||(LA16_40>='\u00C0' && LA16_40<='\u00FF') ) {return s50;}
                return s144;

            }
        };
        DFA.State s148 = new DFA.State() {{alt=58;}};
        DFA.State s149 = new DFA.State() {{alt=57;}};
        DFA.State s42 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case '*':
                    return s148;

                case '/':
                    return s149;

                default:
                    return s144;
        	        }
            }
        };
        DFA.State s49 = new DFA.State() {{alt=53;}};
        DFA.State s43 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA16_43 = input.LA(1);
                if ( (LA16_43>='\u0000' && LA16_43<='\uFFFE') ) {return s49;}
                return s144;

            }
        };
        DFA.State s45 = new DFA.State() {{alt=49;}};
        DFA.State s46 = new DFA.State() {{alt=50;}};
        DFA.State s51 = new DFA.State() {{alt=56;}};
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ';':
                    return s1;

                case 'p':
                    return s2;

                case 'i':
                    return s3;

                case '.':
                    return s4;

                case 'e':
                    return s5;

                case 'g':
                    return s6;

                case 'f':
                    return s7;

                case '(':
                    return s8;

                case ',':
                    return s9;

                case ')':
                    return s10;

                case '{':
                    return s11;

                case '}':
                    return s12;

                case 'q':
                    return s13;

                case 'r':
                    return s14;

                case 'w':
                    return s15;

                case ':':
                    return s16;

                case 't':
                    return s17;

                case 'a':
                    return s18;

                case 's':
                    return s19;

                case 'n':
                    return s20;

                case 'd':
                    return s21;

                case 'o':
                    return s22;

                case '|':
                    return s23;

                case '=':
                    return s24;

                case '>':
                    return s25;

                case '<':
                    return s26;

                case '!':
                    return s27;

                case 'c':
                    return s28;

                case 'm':
                    return s29;

                case '-':
                    return s30;

                case '&':
                    return s31;

                case '[':
                    return s32;

                case ']':
                    return s33;

                case 'u':
                    return s34;

                case '%':
                case '*':
                case '+':
                case '@':
                case '\\':
                case '^':
                    return s35;

                case '$':
                    return s36;

                case '_':
                    return s40;

                case '/':
                    return s42;

                case '\'':
                    return s43;

                case '\t':
                case '\f':
                case ' ':
                    return s45;

                case '\n':
                case '\r':
                    return s46;

                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    return s48;

                case '"':
                    return s49;

                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case 'b':
                case 'h':
                case 'j':
                case 'k':
                case 'l':
                case 'v':
                case 'x':
                case 'y':
                case 'z':
                case '\u00C0':
                case '\u00C1':
                case '\u00C2':
                case '\u00C3':
                case '\u00C4':
                case '\u00C5':
                case '\u00C6':
                case '\u00C7':
                case '\u00C8':
                case '\u00C9':
                case '\u00CA':
                case '\u00CB':
                case '\u00CC':
                case '\u00CD':
                case '\u00CE':
                case '\u00CF':
                case '\u00D0':
                case '\u00D1':
                case '\u00D2':
                case '\u00D3':
                case '\u00D4':
                case '\u00D5':
                case '\u00D6':
                case '\u00D7':
                case '\u00D8':
                case '\u00D9':
                case '\u00DA':
                case '\u00DB':
                case '\u00DC':
                case '\u00DD':
                case '\u00DE':
                case '\u00DF':
                case '\u00E0':
                case '\u00E1':
                case '\u00E2':
                case '\u00E3':
                case '\u00E4':
                case '\u00E5':
                case '\u00E6':
                case '\u00E7':
                case '\u00E8':
                case '\u00E9':
                case '\u00EA':
                case '\u00EB':
                case '\u00EC':
                case '\u00ED':
                case '\u00EE':
                case '\u00EF':
                case '\u00F0':
                case '\u00F1':
                case '\u00F2':
                case '\u00F3':
                case '\u00F4':
                case '\u00F5':
                case '\u00F6':
                case '\u00F7':
                case '\u00F8':
                case '\u00F9':
                case '\u00FA':
                case '\u00FB':
                case '\u00FC':
                case '\u00FD':
                case '\u00FE':
                case '\u00FF':
                    return s50;

                case '#':
                    return s51;

                default:
                    if (backtracking>0) {failed=true; return null;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 0, input);

                    throw nvae;        }
            }
        };

    }
}