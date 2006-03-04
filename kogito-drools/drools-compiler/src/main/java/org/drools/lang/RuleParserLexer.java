// $ANTLR 3.0ea7 /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g 2006-03-04 17:06:04

	package org.drools.lang;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class RuleParserLexer extends Lexer {
    public static final int T21=21;
    public static final int T14=14;
    public static final int T29=29;
    public static final int T33=33;
    public static final int T22=22;
    public static final int T36=36;
    public static final int WS=9;
    public static final int STRING=7;
    public static final int FLOAT=8;
    public static final int T28=28;
    public static final int T23=23;
    public static final int T42=42;
    public static final int T40=40;
    public static final int T35=35;
    public static final int T13=13;
    public static final int T34=34;
    public static final int T20=20;
    public static final int T25=25;
    public static final int T37=37;
    public static final int T18=18;
    public static final int T26=26;
    public static final int INT=6;
    public static final int T15=15;
    public static final int T32=32;
    public static final int EOL=4;
    public static final int T17=17;
    public static final int T31=31;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=10;
    public static final int MULTI_LINE_COMMENT=12;
    public static final int T16=16;
    public static final int T38=38;
    public static final int T27=27;
    public static final int T41=41;
    public static final int T30=30;
    public static final int T24=24;
    public static final int T19=19;
    public static final int T39=39;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=11;
    public static final int ID=5;
    public RuleParserLexer(CharStream input) {
        super(input);
    }
    public Token nextToken() {
        token=null;
retry:
        while (true) {
            if ( input.LA(1)==CharStream.EOF ) {
                return Token.EOF_TOKEN;
            }	
            try {
                mTokens();
                break retry;
            }
            catch (RecognitionException re) {
                reportError(re);
                recover(re);
            }
        }
        return token;
    }

    public void mT13() throws RecognitionException {
        int type = T13;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:6:7: ( 'package' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:6:7: 'package'
        {

        match("package");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT14() throws RecognitionException {
        int type = T14;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:7:7: ( '.' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:7:7: '.'
        {

        match('.');

        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT15() throws RecognitionException {
        int type = T15;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:8:7: ( ';' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:8:7: ';'
        {

        match(';');

        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT16() throws RecognitionException {
        int type = T16;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:9:7: ( 'import' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:9:7: 'import'
        {

        match("import");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT17() throws RecognitionException {
        int type = T17;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:10:7: ( 'use' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:10:7: 'use'
        {

        match("use");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT18() throws RecognitionException {
        int type = T18;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:11:7: ( 'expander' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:11:7: 'expander'
        {

        match("expander");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT19() throws RecognitionException {
        int type = T19;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:12:7: ( 'rule' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:12:7: 'rule'
        {

        match("rule");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT20() throws RecognitionException {
        int type = T20;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:13:7: ( 'when' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:13:7: 'when'
        {

        match("when");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT21() throws RecognitionException {
        int type = T21;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:14:7: ( ':' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:14:7: ':'
        {

        match(':');

        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT22() throws RecognitionException {
        int type = T22;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:15:7: ( 'then' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:15:7: 'then'
        {

        match("then");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT23() throws RecognitionException {
        int type = T23;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:16:7: ( 'end' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:16:7: 'end'
        {

        match("end");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT24() throws RecognitionException {
        int type = T24;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:17:7: ( 'options' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:17:7: 'options'
        {

        match("options");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT25() throws RecognitionException {
        int type = T25;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:18:7: ( ',' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:18:7: ','
        {

        match(',');

        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT26() throws RecognitionException {
        int type = T26;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:19:7: ( 'salience' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:19:7: 'salience'
        {

        match("salience");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT27() throws RecognitionException {
        int type = T27;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:20:7: ( 'no-loop' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:20:7: 'no-loop'
        {

        match("no-loop");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT28() throws RecognitionException {
        int type = T28;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:21:7: ( '(' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:21:7: '('
        {

        match('(');

        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT29() throws RecognitionException {
        int type = T29;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:22:7: ( ')' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:22:7: ')'
        {

        match(')');

        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT30() throws RecognitionException {
        int type = T30;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:23:7: ( '==' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:23:7: '=='
        {

        match("==");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT31() throws RecognitionException {
        int type = T31;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:24:7: ( '>' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:24:7: '>'
        {

        match('>');

        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT32() throws RecognitionException {
        int type = T32;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:25:7: ( '>=' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:25:7: '>='
        {

        match(">=");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT33() throws RecognitionException {
        int type = T33;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:26:7: ( '<' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:26:7: '<'
        {

        match('<');

        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT34() throws RecognitionException {
        int type = T34;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:27:7: ( '<=' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:27:7: '<='
        {

        match("<=");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT35() throws RecognitionException {
        int type = T35;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:28:7: ( '!=' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:28:7: '!='
        {

        match("!=");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT36() throws RecognitionException {
        int type = T36;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:29:7: ( 'or' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:29:7: 'or'
        {

        match("or");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT37() throws RecognitionException {
        int type = T37;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:30:7: ( '||' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:30:7: '||'
        {

        match("||");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT38() throws RecognitionException {
        int type = T38;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:31:7: ( 'and' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:31:7: 'and'
        {

        match("and");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT39() throws RecognitionException {
        int type = T39;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:32:7: ( '&&' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:32:7: '&&'
        {

        match("&&");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT40() throws RecognitionException {
        int type = T40;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:33:7: ( 'exists' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:33:7: 'exists'
        {

        match("exists");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT41() throws RecognitionException {
        int type = T41;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:34:7: ( 'not' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:34:7: 'not'
        {

        match("not");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT42() throws RecognitionException {
        int type = T42;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:35:7: ( 'eval' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:35:7: 'eval'
        {

        match("eval");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mWS() throws RecognitionException {
        int type = WS;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:37:6: ( (' '|'\t'|'\f'))
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:37:6: (' '|'\t'|'\f')
        {

        if ( input.LA(1)=='\t'||input.LA(1)=='\f'||input.LA(1)==' ' ) {
            input.consume();
            errorRecovery=false;
        }
        else {
            MismatchedSetException mse =
                new MismatchedSetException(null,input);
            recover(mse);    throw mse;
        }


         channel=99; 

        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mEOL() throws RecognitionException {
        int type = EOL;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:39:7: ( ( '\r\n' | '\r' | '\n' ) )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:39:7: ( '\r\n' | '\r' | '\n' )
        {

        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:39:7: ( '\r\n' | '\r' | '\n' )
        int alt1=3;
        int LA1_0 = input.LA(1);
        if ( LA1_0=='\r' ) {
            int LA1_1 = input.LA(2);
            if ( LA1_1=='n' ) {
                alt1=1;
            }
            else {
                alt1=2;}
        }
        else if ( LA1_0=='\n' ) {
            alt1=3;
        }
        else {

            NoViableAltException nvae =
                new NoViableAltException("39:7: ( \'\\r\\n\' | \'\\r\' | \'\\n\' )", 1, 0, input);

            throw nvae;
        }
        switch (alt1) {
            case 1 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:39:8: '\r\n'
                {

                match("\r\n");


                }
                break;
            case 2 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:39:15: '\r'
                {

                match('\r');

                }
                break;
            case 3 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:39:20: '\n'
                {

                match('\n');

                }
                break;

        }


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mINT() throws RecognitionException {
        int type = INT;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:41:7: ( ( '0' .. '9' )+ )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:41:7: ( '0' .. '9' )+
        {

        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:41:7: ( '0' .. '9' )+
        int cnt2=0;
        loop2:
        do {
            int alt2=2;
            int LA2_0 = input.LA(1);
            if ( (LA2_0>='0' && LA2_0<='9') ) {
                alt2=1;
            }


            switch (alt2) {
        	case 1 :
        	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:41:9: '0' .. '9'
        	    {

        	    matchRange('0','9');

        	    }
        	    break;

        	default :
        	    if ( cnt2 >= 1 ) break loop2;
                    EarlyExitException eee =
                        new EarlyExitException(2, input);
                    throw eee;
            }
            cnt2++;
        } while (true);


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mFLOAT() throws RecognitionException {
        int type = FLOAT;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:43:9: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:43:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )+
        {

        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:43:9: ( '0' .. '9' )+
        int cnt3=0;
        loop3:
        do {
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0>='0' && LA3_0<='9') ) {
                alt3=1;
            }


            switch (alt3) {
        	case 1 :
        	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:43:11: '0' .. '9'
        	    {

        	    matchRange('0','9');

        	    }
        	    break;

        	default :
        	    if ( cnt3 >= 1 ) break loop3;
                    EarlyExitException eee =
                        new EarlyExitException(3, input);
                    throw eee;
            }
            cnt3++;
        } while (true);


        match('.');

        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:43:29: ( '0' .. '9' )+
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
        	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:43:31: '0' .. '9'
        	    {

        	    matchRange('0','9');

        	    }
        	    break;

        	default :
        	    if ( cnt4 >= 1 ) break loop4;
                    EarlyExitException eee =
                        new EarlyExitException(4, input);
                    throw eee;
            }
            cnt4++;
        } while (true);


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mSTRING() throws RecognitionException {
        int type = STRING;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:45:10: ( '"' ( options {greedy=false; } : . )* '"' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:45:10: '"' ( options {greedy=false; } : . )* '"'
        {

        match('"');

        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:45:14: ( options {greedy=false; } : . )*
        loop5:
        do {
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( LA5_0=='"' ) {
                alt5=2;
            }
            else if ( (LA5_0>='\u0000' && LA5_0<='!')||(LA5_0>='#' && LA5_0<='\uFFFE') ) {
                alt5=1;
            }


            switch (alt5) {
        	case 1 :
        	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:45:43: .
        	    {

        	    matchAny();

        	    }
        	    break;

        	default :
        	    break loop5;
            }
        } while (true);


        match('"');

        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mID() throws RecognitionException {
        int type = ID;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:47:6: ( ( ('a'..'z'|'A'..'Z'|'_'))+ )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:47:6: ( ('a'..'z'|'A'..'Z'|'_'))+
        {

        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:47:6: ( ('a'..'z'|'A'..'Z'|'_'))+
        int cnt6=0;
        loop6:
        do {
            int alt6=2;
            int LA6_0 = input.LA(1);
            if ( (LA6_0>='A' && LA6_0<='Z')||LA6_0=='_'||(LA6_0>='a' && LA6_0<='z') ) {
                alt6=1;
            }


            switch (alt6) {
        	case 1 :
        	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:47:8: ('a'..'z'|'A'..'Z'|'_')
        	    {

        	    if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
        	        input.consume();
        	        errorRecovery=false;
        	    }
        	    else {
        	        MismatchedSetException mse =
        	            new MismatchedSetException(null,input);
        	        recover(mse);    throw mse;
        	    }


        	    }
        	    break;

        	default :
        	    if ( cnt6 >= 1 ) break loop6;
                    EarlyExitException eee =
                        new EarlyExitException(6, input);
                    throw eee;
            }
            cnt6++;
        } while (true);


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mSH_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        int type = SH_STYLE_SINGLE_LINE_COMMENT;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:49:32: ( '#' ( options {greedy=false; } : . )* ( '\r' )? '\n' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:49:32: '#' ( options {greedy=false; } : . )* ( '\r' )? '\n'
        {

        match('#');

        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:49:36: ( options {greedy=false; } : . )*
        loop7:
        do {
            int alt7=2;
            int LA7_0 = input.LA(1);
            if ( LA7_0=='\r' ) {
                alt7=2;
            }
            else if ( LA7_0=='\n' ) {
                alt7=2;
            }
            else if ( (LA7_0>='\u0000' && LA7_0<='\t')||(LA7_0>='\u000B' && LA7_0<='\f')||(LA7_0>='\u000E' && LA7_0<='\uFFFE') ) {
                alt7=1;
            }


            switch (alt7) {
        	case 1 :
        	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:49:65: .
        	    {

        	    matchAny();

        	    }
        	    break;

        	default :
        	    break loop7;
            }
        } while (true);


        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:49:70: ( '\r' )?
        int alt8=2;
        int LA8_0 = input.LA(1);
        if ( LA8_0=='\r' ) {
            alt8=1;
        }
        else if ( LA8_0=='\n' ) {
            alt8=2;
        }
        else {

            NoViableAltException nvae =
                new NoViableAltException("49:70: ( \'\\r\' )?", 8, 0, input);

            throw nvae;
        }
        switch (alt8) {
            case 1 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:49:72: '\r'
                {

                match('\r');

                }
                break;

        }


        match('\n');

        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mC_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        int type = C_STYLE_SINGLE_LINE_COMMENT;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:51:31: ( '//' ( options {greedy=false; } : . )* ( '\r' )? '\n' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:51:31: '//' ( options {greedy=false; } : . )* ( '\r' )? '\n'
        {

        match("//");


        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:51:36: ( options {greedy=false; } : . )*
        loop9:
        do {
            int alt9=2;
            int LA9_0 = input.LA(1);
            if ( LA9_0=='\r' ) {
                alt9=2;
            }
            else if ( LA9_0=='\n' ) {
                alt9=2;
            }
            else if ( (LA9_0>='\u0000' && LA9_0<='\t')||(LA9_0>='\u000B' && LA9_0<='\f')||(LA9_0>='\u000E' && LA9_0<='\uFFFE') ) {
                alt9=1;
            }


            switch (alt9) {
        	case 1 :
        	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:51:65: .
        	    {

        	    matchAny();

        	    }
        	    break;

        	default :
        	    break loop9;
            }
        } while (true);


        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:51:70: ( '\r' )?
        int alt10=2;
        int LA10_0 = input.LA(1);
        if ( LA10_0=='\r' ) {
            alt10=1;
        }
        else if ( LA10_0=='\n' ) {
            alt10=2;
        }
        else {

            NoViableAltException nvae =
                new NoViableAltException("51:70: ( \'\\r\' )?", 10, 0, input);

            throw nvae;
        }
        switch (alt10) {
            case 1 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:51:72: '\r'
                {

                match('\r');

                }
                break;

        }


        match('\n');

        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mMULTI_LINE_COMMENT() throws RecognitionException {
        int type = MULTI_LINE_COMMENT;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:53:22: ( '/*' ( options {greedy=false; } : . )* '*/' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:53:22: '/*' ( options {greedy=false; } : . )* '*/'
        {

        match("/*");


        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:53:27: ( options {greedy=false; } : . )*
        loop11:
        do {
            int alt11=2;
            int LA11_0 = input.LA(1);
            if ( LA11_0=='*' ) {
                int LA11_1 = input.LA(2);
                if ( LA11_1=='/' ) {
                    alt11=2;
                }
                else if ( (LA11_1>='\u0000' && LA11_1<='.')||(LA11_1>='0' && LA11_1<='\uFFFE') ) {
                    alt11=1;
                }


            }
            else if ( (LA11_0>='\u0000' && LA11_0<=')')||(LA11_0>='+' && LA11_0<='\uFFFE') ) {
                alt11=1;
            }


            switch (alt11) {
        	case 1 :
        	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:53:56: .
        	    {

        	    matchAny();

        	    }
        	    break;

        	default :
        	    break loop11;
            }
        } while (true);


        match("*/");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mTokens() throws RecognitionException {
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:10: ( T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | T33 | T34 | T35 | T36 | T37 | T38 | T39 | T40 | T41 | T42 | WS | EOL | INT | FLOAT | STRING | ID | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT )
        int alt12=39;
        alt12 = dfa12.predict(input);
        switch (alt12) {
            case 1 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:10: T13
                {

                mT13();

                }
                break;
            case 2 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:14: T14
                {

                mT14();

                }
                break;
            case 3 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:18: T15
                {

                mT15();

                }
                break;
            case 4 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:22: T16
                {

                mT16();

                }
                break;
            case 5 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:26: T17
                {

                mT17();

                }
                break;
            case 6 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:30: T18
                {

                mT18();

                }
                break;
            case 7 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:34: T19
                {

                mT19();

                }
                break;
            case 8 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:38: T20
                {

                mT20();

                }
                break;
            case 9 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:42: T21
                {

                mT21();

                }
                break;
            case 10 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:46: T22
                {

                mT22();

                }
                break;
            case 11 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:50: T23
                {

                mT23();

                }
                break;
            case 12 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:54: T24
                {

                mT24();

                }
                break;
            case 13 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:58: T25
                {

                mT25();

                }
                break;
            case 14 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:62: T26
                {

                mT26();

                }
                break;
            case 15 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:66: T27
                {

                mT27();

                }
                break;
            case 16 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:70: T28
                {

                mT28();

                }
                break;
            case 17 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:74: T29
                {

                mT29();

                }
                break;
            case 18 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:78: T30
                {

                mT30();

                }
                break;
            case 19 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:82: T31
                {

                mT31();

                }
                break;
            case 20 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:86: T32
                {

                mT32();

                }
                break;
            case 21 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:90: T33
                {

                mT33();

                }
                break;
            case 22 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:94: T34
                {

                mT34();

                }
                break;
            case 23 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:98: T35
                {

                mT35();

                }
                break;
            case 24 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:102: T36
                {

                mT36();

                }
                break;
            case 25 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:106: T37
                {

                mT37();

                }
                break;
            case 26 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:110: T38
                {

                mT38();

                }
                break;
            case 27 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:114: T39
                {

                mT39();

                }
                break;
            case 28 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:118: T40
                {

                mT40();

                }
                break;
            case 29 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:122: T41
                {

                mT41();

                }
                break;
            case 30 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:126: T42
                {

                mT42();

                }
                break;
            case 31 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:130: WS
                {

                mWS();

                }
                break;
            case 32 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:133: EOL
                {

                mEOL();

                }
                break;
            case 33 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:137: INT
                {

                mINT();

                }
                break;
            case 34 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:141: FLOAT
                {

                mFLOAT();

                }
                break;
            case 35 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:147: STRING
                {

                mSTRING();

                }
                break;
            case 36 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:154: ID
                {

                mID();

                }
                break;
            case 37 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:157: SH_STYLE_SINGLE_LINE_COMMENT
                {

                mSH_STYLE_SINGLE_LINE_COMMENT();

                }
                break;
            case 38 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:186: C_STYLE_SINGLE_LINE_COMMENT
                {

                mC_STYLE_SINGLE_LINE_COMMENT();

                }
                break;
            case 39 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:214: MULTI_LINE_COMMENT
                {

                mMULTI_LINE_COMMENT();

                }
                break;

        }

    }


    protected DFA12 dfa12 = new DFA12();
    class DFA12 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s218 = new DFA.State() {{alt=1;}};
        DFA.State s29 = new DFA.State() {{alt=36;}};
        DFA.State s202 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_202 = input.LA(1);
                if ( (LA12_202>='A' && LA12_202<='Z')||LA12_202=='_'||(LA12_202>='a' && LA12_202<='z') ) {return s29;}
                return s218;

            }
        };
        DFA.State s184 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_184 = input.LA(1);
                if ( LA12_184=='e' ) {return s202;}
                return s29;

            }
        };
        DFA.State s158 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_158 = input.LA(1);
                if ( LA12_158=='g' ) {return s184;}
                return s29;

            }
        };
        DFA.State s120 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_120 = input.LA(1);
                if ( LA12_120=='a' ) {return s158;}
                return s29;

            }
        };
        DFA.State s77 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_77 = input.LA(1);
                if ( LA12_77=='k' ) {return s120;}
                return s29;

            }
        };
        DFA.State s32 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_32 = input.LA(1);
                if ( LA12_32=='c' ) {return s77;}
                return s29;

            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_1 = input.LA(1);
                if ( LA12_1=='a' ) {return s32;}
                return s29;

            }
        };
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {{alt=3;}};
        DFA.State s205 = new DFA.State() {{alt=4;}};
        DFA.State s187 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_187 = input.LA(1);
                if ( (LA12_187>='A' && LA12_187<='Z')||LA12_187=='_'||(LA12_187>='a' && LA12_187<='z') ) {return s29;}
                return s205;

            }
        };
        DFA.State s161 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_161 = input.LA(1);
                if ( LA12_161=='t' ) {return s187;}
                return s29;

            }
        };
        DFA.State s123 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_123 = input.LA(1);
                if ( LA12_123=='r' ) {return s161;}
                return s29;

            }
        };
        DFA.State s80 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_80 = input.LA(1);
                if ( LA12_80=='o' ) {return s123;}
                return s29;

            }
        };
        DFA.State s35 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_35 = input.LA(1);
                if ( LA12_35=='p' ) {return s80;}
                return s29;

            }
        };
        DFA.State s4 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_4 = input.LA(1);
                if ( LA12_4=='m' ) {return s35;}
                return s29;

            }
        };
        DFA.State s126 = new DFA.State() {{alt=5;}};
        DFA.State s83 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_83 = input.LA(1);
                if ( (LA12_83>='A' && LA12_83<='Z')||LA12_83=='_'||(LA12_83>='a' && LA12_83<='z') ) {return s29;}
                return s126;

            }
        };
        DFA.State s38 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_38 = input.LA(1);
                if ( LA12_38=='e' ) {return s83;}
                return s29;

            }
        };
        DFA.State s5 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_5 = input.LA(1);
                if ( LA12_5=='s' ) {return s38;}
                return s29;

            }
        };
        DFA.State s228 = new DFA.State() {{alt=6;}};
        DFA.State s220 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_220 = input.LA(1);
                if ( (LA12_220>='A' && LA12_220<='Z')||LA12_220=='_'||(LA12_220>='a' && LA12_220<='z') ) {return s29;}
                return s228;

            }
        };
        DFA.State s207 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_207 = input.LA(1);
                if ( LA12_207=='r' ) {return s220;}
                return s29;

            }
        };
        DFA.State s190 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_190 = input.LA(1);
                if ( LA12_190=='e' ) {return s207;}
                return s29;

            }
        };
        DFA.State s164 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_164 = input.LA(1);
                if ( LA12_164=='d' ) {return s190;}
                return s29;

            }
        };
        DFA.State s128 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_128 = input.LA(1);
                if ( LA12_128=='n' ) {return s164;}
                return s29;

            }
        };
        DFA.State s86 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_86 = input.LA(1);
                if ( LA12_86=='a' ) {return s128;}
                return s29;

            }
        };
        DFA.State s210 = new DFA.State() {{alt=28;}};
        DFA.State s193 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_193 = input.LA(1);
                if ( (LA12_193>='A' && LA12_193<='Z')||LA12_193=='_'||(LA12_193>='a' && LA12_193<='z') ) {return s29;}
                return s210;

            }
        };
        DFA.State s167 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_167 = input.LA(1);
                if ( LA12_167=='s' ) {return s193;}
                return s29;

            }
        };
        DFA.State s131 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_131 = input.LA(1);
                if ( LA12_131=='t' ) {return s167;}
                return s29;

            }
        };
        DFA.State s87 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_87 = input.LA(1);
                if ( LA12_87=='s' ) {return s131;}
                return s29;

            }
        };
        DFA.State s41 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 'p':
                    return s86;

                case 'i':
                    return s87;

                default:
                    return s29;
        	        }
            }
        };
        DFA.State s170 = new DFA.State() {{alt=30;}};
        DFA.State s134 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_134 = input.LA(1);
                if ( (LA12_134>='A' && LA12_134<='Z')||LA12_134=='_'||(LA12_134>='a' && LA12_134<='z') ) {return s29;}
                return s170;

            }
        };
        DFA.State s90 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_90 = input.LA(1);
                if ( LA12_90=='l' ) {return s134;}
                return s29;

            }
        };
        DFA.State s42 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_42 = input.LA(1);
                if ( LA12_42=='a' ) {return s90;}
                return s29;

            }
        };
        DFA.State s137 = new DFA.State() {{alt=11;}};
        DFA.State s93 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_93 = input.LA(1);
                if ( (LA12_93>='A' && LA12_93<='Z')||LA12_93=='_'||(LA12_93>='a' && LA12_93<='z') ) {return s29;}
                return s137;

            }
        };
        DFA.State s43 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_43 = input.LA(1);
                if ( LA12_43=='d' ) {return s93;}
                return s29;

            }
        };
        DFA.State s6 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 'x':
                    return s41;

                case 'v':
                    return s42;

                case 'n':
                    return s43;

                default:
                    return s29;
        	        }
            }
        };
        DFA.State s172 = new DFA.State() {{alt=7;}};
        DFA.State s139 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_139 = input.LA(1);
                if ( (LA12_139>='A' && LA12_139<='Z')||LA12_139=='_'||(LA12_139>='a' && LA12_139<='z') ) {return s29;}
                return s172;

            }
        };
        DFA.State s96 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_96 = input.LA(1);
                if ( LA12_96=='e' ) {return s139;}
                return s29;

            }
        };
        DFA.State s46 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_46 = input.LA(1);
                if ( LA12_46=='l' ) {return s96;}
                return s29;

            }
        };
        DFA.State s7 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_7 = input.LA(1);
                if ( LA12_7=='u' ) {return s46;}
                return s29;

            }
        };
        DFA.State s174 = new DFA.State() {{alt=8;}};
        DFA.State s142 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_142 = input.LA(1);
                if ( (LA12_142>='A' && LA12_142<='Z')||LA12_142=='_'||(LA12_142>='a' && LA12_142<='z') ) {return s29;}
                return s174;

            }
        };
        DFA.State s99 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_99 = input.LA(1);
                if ( LA12_99=='n' ) {return s142;}
                return s29;

            }
        };
        DFA.State s49 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_49 = input.LA(1);
                if ( LA12_49=='e' ) {return s99;}
                return s29;

            }
        };
        DFA.State s8 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_8 = input.LA(1);
                if ( LA12_8=='h' ) {return s49;}
                return s29;

            }
        };
        DFA.State s9 = new DFA.State() {{alt=9;}};
        DFA.State s176 = new DFA.State() {{alt=10;}};
        DFA.State s145 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_145 = input.LA(1);
                if ( (LA12_145>='A' && LA12_145<='Z')||LA12_145=='_'||(LA12_145>='a' && LA12_145<='z') ) {return s29;}
                return s176;

            }
        };
        DFA.State s102 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_102 = input.LA(1);
                if ( LA12_102=='n' ) {return s145;}
                return s29;

            }
        };
        DFA.State s52 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_52 = input.LA(1);
                if ( LA12_52=='e' ) {return s102;}
                return s29;

            }
        };
        DFA.State s10 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_10 = input.LA(1);
                if ( LA12_10=='h' ) {return s52;}
                return s29;

            }
        };
        DFA.State s223 = new DFA.State() {{alt=12;}};
        DFA.State s212 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_212 = input.LA(1);
                if ( (LA12_212>='A' && LA12_212<='Z')||LA12_212=='_'||(LA12_212>='a' && LA12_212<='z') ) {return s29;}
                return s223;

            }
        };
        DFA.State s196 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_196 = input.LA(1);
                if ( LA12_196=='s' ) {return s212;}
                return s29;

            }
        };
        DFA.State s178 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_178 = input.LA(1);
                if ( LA12_178=='n' ) {return s196;}
                return s29;

            }
        };
        DFA.State s148 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_148 = input.LA(1);
                if ( LA12_148=='o' ) {return s178;}
                return s29;

            }
        };
        DFA.State s105 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_105 = input.LA(1);
                if ( LA12_105=='i' ) {return s148;}
                return s29;

            }
        };
        DFA.State s55 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_55 = input.LA(1);
                if ( LA12_55=='t' ) {return s105;}
                return s29;

            }
        };
        DFA.State s108 = new DFA.State() {{alt=24;}};
        DFA.State s56 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_56 = input.LA(1);
                if ( (LA12_56>='A' && LA12_56<='Z')||LA12_56=='_'||(LA12_56>='a' && LA12_56<='z') ) {return s29;}
                return s108;

            }
        };
        DFA.State s11 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 'p':
                    return s55;

                case 'r':
                    return s56;

                default:
                    return s29;
        	        }
            }
        };
        DFA.State s12 = new DFA.State() {{alt=13;}};
        DFA.State s230 = new DFA.State() {{alt=14;}};
        DFA.State s225 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_225 = input.LA(1);
                if ( (LA12_225>='A' && LA12_225<='Z')||LA12_225=='_'||(LA12_225>='a' && LA12_225<='z') ) {return s29;}
                return s230;

            }
        };
        DFA.State s215 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_215 = input.LA(1);
                if ( LA12_215=='e' ) {return s225;}
                return s29;

            }
        };
        DFA.State s199 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_199 = input.LA(1);
                if ( LA12_199=='c' ) {return s215;}
                return s29;

            }
        };
        DFA.State s181 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_181 = input.LA(1);
                if ( LA12_181=='n' ) {return s199;}
                return s29;

            }
        };
        DFA.State s151 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_151 = input.LA(1);
                if ( LA12_151=='e' ) {return s181;}
                return s29;

            }
        };
        DFA.State s110 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_110 = input.LA(1);
                if ( LA12_110=='i' ) {return s151;}
                return s29;

            }
        };
        DFA.State s59 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_59 = input.LA(1);
                if ( LA12_59=='l' ) {return s110;}
                return s29;

            }
        };
        DFA.State s13 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_13 = input.LA(1);
                if ( LA12_13=='a' ) {return s59;}
                return s29;

            }
        };
        DFA.State s154 = new DFA.State() {{alt=29;}};
        DFA.State s113 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_113 = input.LA(1);
                if ( (LA12_113>='A' && LA12_113<='Z')||LA12_113=='_'||(LA12_113>='a' && LA12_113<='z') ) {return s29;}
                return s154;

            }
        };
        DFA.State s114 = new DFA.State() {{alt=15;}};
        DFA.State s62 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 't':
                    return s113;

                case '-':
                    return s114;

                default:
                    return s29;
        	        }
            }
        };
        DFA.State s14 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_14 = input.LA(1);
                if ( LA12_14=='o' ) {return s62;}
                return s29;

            }
        };
        DFA.State s15 = new DFA.State() {{alt=16;}};
        DFA.State s16 = new DFA.State() {{alt=17;}};
        DFA.State s17 = new DFA.State() {{alt=18;}};
        DFA.State s65 = new DFA.State() {{alt=20;}};
        DFA.State s66 = new DFA.State() {{alt=19;}};
        DFA.State s18 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_18 = input.LA(1);
                if ( LA12_18=='=' ) {return s65;}
                return s66;

            }
        };
        DFA.State s67 = new DFA.State() {{alt=22;}};
        DFA.State s68 = new DFA.State() {{alt=21;}};
        DFA.State s19 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_19 = input.LA(1);
                if ( LA12_19=='=' ) {return s67;}
                return s68;

            }
        };
        DFA.State s20 = new DFA.State() {{alt=23;}};
        DFA.State s21 = new DFA.State() {{alt=25;}};
        DFA.State s156 = new DFA.State() {{alt=26;}};
        DFA.State s117 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_117 = input.LA(1);
                if ( (LA12_117>='A' && LA12_117<='Z')||LA12_117=='_'||(LA12_117>='a' && LA12_117<='z') ) {return s29;}
                return s156;

            }
        };
        DFA.State s69 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_69 = input.LA(1);
                if ( LA12_69=='d' ) {return s117;}
                return s29;

            }
        };
        DFA.State s22 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_22 = input.LA(1);
                if ( LA12_22=='n' ) {return s69;}
                return s29;

            }
        };
        DFA.State s23 = new DFA.State() {{alt=27;}};
        DFA.State s24 = new DFA.State() {{alt=31;}};
        DFA.State s25 = new DFA.State() {{alt=32;}};
        DFA.State s72 = new DFA.State() {{alt=34;}};
        DFA.State s74 = new DFA.State() {{alt=33;}};
        DFA.State s27 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case '.':
                    return s72;

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
                    return s27;

                default:
                    return s74;
        	        }
            }
        };
        DFA.State s28 = new DFA.State() {{alt=35;}};
        DFA.State s30 = new DFA.State() {{alt=37;}};
        DFA.State s75 = new DFA.State() {{alt=38;}};
        DFA.State s76 = new DFA.State() {{alt=39;}};
        DFA.State s31 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_31 = input.LA(1);
                if ( LA12_31=='/' ) {return s75;}
                if ( LA12_31=='*' ) {return s76;}


                NoViableAltException nvae =
        	    new NoViableAltException("", 12, 31, input);

                throw nvae;
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 'p':
                    return s1;

                case '.':
                    return s2;

                case ';':
                    return s3;

                case 'i':
                    return s4;

                case 'u':
                    return s5;

                case 'e':
                    return s6;

                case 'r':
                    return s7;

                case 'w':
                    return s8;

                case ':':
                    return s9;

                case 't':
                    return s10;

                case 'o':
                    return s11;

                case ',':
                    return s12;

                case 's':
                    return s13;

                case 'n':
                    return s14;

                case '(':
                    return s15;

                case ')':
                    return s16;

                case '=':
                    return s17;

                case '>':
                    return s18;

                case '<':
                    return s19;

                case '!':
                    return s20;

                case '|':
                    return s21;

                case 'a':
                    return s22;

                case '&':
                    return s23;

                case '\t':
                case '\f':
                case ' ':
                    return s24;

                case '\n':
                case '\r':
                    return s25;

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
                    return s27;

                case '"':
                    return s28;

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
                case '_':
                case 'b':
                case 'c':
                case 'd':
                case 'f':
                case 'g':
                case 'h':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'q':
                case 'v':
                case 'x':
                case 'y':
                case 'z':
                    return s29;

                case '#':
                    return s30;

                case '/':
                    return s31;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 0, input);

                    throw nvae;        }
            }
        };

    }
}