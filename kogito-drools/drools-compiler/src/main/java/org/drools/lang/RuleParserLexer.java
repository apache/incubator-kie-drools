// $ANTLR 3.0ea7 /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g 2006-03-09 00:28:52

	package org.drools.lang;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class RuleParserLexer extends Lexer {
    public static final int T14=14;
    public static final int T29=29;
    public static final int T36=36;
    public static final int MISC=9;
    public static final int FLOAT=8;
    public static final int T35=35;
    public static final int T20=20;
    public static final int T34=34;
    public static final int T25=25;
    public static final int T18=18;
    public static final int T37=37;
    public static final int INT=6;
    public static final int T26=26;
    public static final int T32=32;
    public static final int T17=17;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=11;
    public static final int T16=16;
    public static final int T38=38;
    public static final int T41=41;
    public static final int T24=24;
    public static final int T19=19;
    public static final int T39=39;
    public static final int ID=5;
    public static final int T21=21;
    public static final int T33=33;
    public static final int T22=22;
    public static final int WS=10;
    public static final int STRING=7;
    public static final int T43=43;
    public static final int T23=23;
    public static final int T28=28;
    public static final int T42=42;
    public static final int T40=40;
    public static final int T15=15;
    public static final int EOL=4;
    public static final int T31=31;
    public static final int MULTI_LINE_COMMENT=13;
    public static final int T27=27;
    public static final int T30=30;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=12;
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

    public void mT14() throws RecognitionException {
        int type = T14;
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

    public void mT15() throws RecognitionException {
        int type = T15;
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

    public void mT16() throws RecognitionException {
        int type = T16;
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

    public void mT17() throws RecognitionException {
        int type = T17;
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

    public void mT18() throws RecognitionException {
        int type = T18;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:10:7: ( 'expander' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:10:7: 'expander'
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:11:7: ( 'rule' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:11:7: 'rule'
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:12:7: ( 'when' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:12:7: 'when'
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:13:7: ( ':' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:13:7: ':'
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:14:7: ( '>' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:14:7: '>'
        {

        match('>');

        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT23() throws RecognitionException {
        int type = T23;
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

    public void mT24() throws RecognitionException {
        int type = T24;
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

    public void mT25() throws RecognitionException {
        int type = T25;
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

    public void mT26() throws RecognitionException {
        int type = T26;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:18:7: ( 'salience' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:18:7: 'salience'
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:19:7: ( 'no-loop' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:19:7: 'no-loop'
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:20:7: ( '(' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:20:7: '('
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:21:7: ( ')' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:21:7: ')'
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:22:7: ( ',' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:22:7: ','
        {

        match(',');

        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT31() throws RecognitionException {
        int type = T31;
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

    public void mT32() throws RecognitionException {
        int type = T32;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:24:7: ( '>=' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:24:7: '>='
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:25:7: ( '<' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:25:7: '<'
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:26:7: ( '<=' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:26:7: '<='
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:27:7: ( '!=' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:27:7: '!='
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:28:7: ( 'or' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:28:7: 'or'
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:29:7: ( '||' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:29:7: '||'
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:30:7: ( 'and' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:30:7: 'and'
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:31:7: ( '&&' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:31:7: '&&'
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:32:7: ( 'exists' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:32:7: 'exists'
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:33:7: ( 'not' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:33:7: 'not'
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:34:7: ( 'eval' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:34:7: 'eval'
        {

        match("eval");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT43() throws RecognitionException {
        int type = T43;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:35:7: ( 'use' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:35:7: 'use'
        {

        match("use");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mMISC() throws RecognitionException {
        int type = MISC;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:37:8: ( ('!'|'@'|'$'|'%'|'^'|'&'|'*'|'_'|'-'|'+'|'|'|','|'{'|'}'|'['|']'))
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:37:8: ('!'|'@'|'$'|'%'|'^'|'&'|'*'|'_'|'-'|'+'|'|'|','|'{'|'}'|'['|']')
        {

        if ( input.LA(1)=='!'||(input.LA(1)>='$' && input.LA(1)<='&')||(input.LA(1)>='*' && input.LA(1)<='-')||input.LA(1)=='@'||input.LA(1)=='['||(input.LA(1)>=']' && input.LA(1)<='_')||(input.LA(1)>='{' && input.LA(1)<='}') ) {
            input.consume();
            errorRecovery=false;
        }
        else {
            MismatchedSetException mse =
                new MismatchedSetException(null,input);
            recover(mse);    throw mse;
        }


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mWS() throws RecognitionException {
        int type = WS;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:39:6: ( (' '|'\t'|'\f'))
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:39:6: (' '|'\t'|'\f')
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:41:7: ( ( '\r\n' | '\r' | '\n' ) )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:41:7: ( '\r\n' | '\r' | '\n' )
        {

        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:41:7: ( '\r\n' | '\r' | '\n' )
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
                new NoViableAltException("41:7: ( \'\\r\\n\' | \'\\r\' | \'\\n\' )", 1, 0, input);

            throw nvae;
        }
        switch (alt1) {
            case 1 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:41:8: '\r\n'
                {

                match("\r\n");


                }
                break;
            case 2 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:41:15: '\r'
                {

                match('\r');

                }
                break;
            case 3 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:41:20: '\n'
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:43:7: ( ( '0' .. '9' )+ )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:43:7: ( '0' .. '9' )+
        {

        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:43:7: ( '0' .. '9' )+
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
        	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:43:9: '0' .. '9'
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:45:9: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:45:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )+
        {

        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:45:9: ( '0' .. '9' )+
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
        	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:45:11: '0' .. '9'
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

        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:45:29: ( '0' .. '9' )+
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
        	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:45:31: '0' .. '9'
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:47:10: ( '"' ( options {greedy=false; } : . )* '"' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:47:10: '"' ( options {greedy=false; } : . )* '"'
        {

        match('"');

        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:47:14: ( options {greedy=false; } : . )*
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
        	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:47:43: .
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:49:6: ( ('a'..'z'|'A'..'Z'|'_') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))* )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:49:6: ('a'..'z'|'A'..'Z'|'_') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))*
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


        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:49:30: ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))*
        loop6:
        do {
            int alt6=2;
            int LA6_0 = input.LA(1);
            if ( (LA6_0>='0' && LA6_0<='9')||(LA6_0>='A' && LA6_0<='Z')||LA6_0=='_'||(LA6_0>='a' && LA6_0<='z') ) {
                alt6=1;
            }


            switch (alt6) {
        	case 1 :
        	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:49:32: ('a'..'z'|'A'..'Z'|'_'|'0'..'9')
        	    {

        	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
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
        	    break loop6;
            }
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:51:32: ( '#' ( options {greedy=false; } : . )* ( '\r' )? '\n' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:51:32: '#' ( options {greedy=false; } : . )* ( '\r' )? '\n'
        {

        match('#');

        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:51:36: ( options {greedy=false; } : . )*
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
        	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:51:65: .
        	    {

        	    matchAny();

        	    }
        	    break;

        	default :
        	    break loop7;
            }
        } while (true);


        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:51:70: ( '\r' )?
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
                new NoViableAltException("51:70: ( \'\\r\' )?", 8, 0, input);

            throw nvae;
        }
        switch (alt8) {
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

    public void mC_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        int type = C_STYLE_SINGLE_LINE_COMMENT;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:53:31: ( '//' ( options {greedy=false; } : . )* ( '\r' )? '\n' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:53:31: '//' ( options {greedy=false; } : . )* ( '\r' )? '\n'
        {

        match("//");


        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:53:36: ( options {greedy=false; } : . )*
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
        	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:53:65: .
        	    {

        	    matchAny();

        	    }
        	    break;

        	default :
        	    break loop9;
            }
        } while (true);


        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:53:70: ( '\r' )?
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
                new NoViableAltException("53:70: ( \'\\r\' )?", 10, 0, input);

            throw nvae;
        }
        switch (alt10) {
            case 1 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:53:72: '\r'
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:55:22: ( '/*' ( options {greedy=false; } : . )* '*/' )
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:55:22: '/*' ( options {greedy=false; } : . )* '*/'
        {

        match("/*");


        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:55:27: ( options {greedy=false; } : . )*
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
        	    // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:55:56: .
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
        // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:10: ( T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | T33 | T34 | T35 | T36 | T37 | T38 | T39 | T40 | T41 | T42 | T43 | MISC | WS | EOL | INT | FLOAT | STRING | ID | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT )
        int alt12=40;
        alt12 = dfa12.predict(input);
        switch (alt12) {
            case 1 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:10: T14
                {

                mT14();

                }
                break;
            case 2 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:14: T15
                {

                mT15();

                }
                break;
            case 3 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:18: T16
                {

                mT16();

                }
                break;
            case 4 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:22: T17
                {

                mT17();

                }
                break;
            case 5 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:26: T18
                {

                mT18();

                }
                break;
            case 6 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:30: T19
                {

                mT19();

                }
                break;
            case 7 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:34: T20
                {

                mT20();

                }
                break;
            case 8 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:38: T21
                {

                mT21();

                }
                break;
            case 9 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:42: T22
                {

                mT22();

                }
                break;
            case 10 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:46: T23
                {

                mT23();

                }
                break;
            case 11 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:50: T24
                {

                mT24();

                }
                break;
            case 12 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:54: T25
                {

                mT25();

                }
                break;
            case 13 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:58: T26
                {

                mT26();

                }
                break;
            case 14 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:62: T27
                {

                mT27();

                }
                break;
            case 15 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:66: T28
                {

                mT28();

                }
                break;
            case 16 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:70: T29
                {

                mT29();

                }
                break;
            case 17 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:74: T30
                {

                mT30();

                }
                break;
            case 18 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:78: T31
                {

                mT31();

                }
                break;
            case 19 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:82: T32
                {

                mT32();

                }
                break;
            case 20 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:86: T33
                {

                mT33();

                }
                break;
            case 21 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:90: T34
                {

                mT34();

                }
                break;
            case 22 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:94: T35
                {

                mT35();

                }
                break;
            case 23 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:98: T36
                {

                mT36();

                }
                break;
            case 24 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:102: T37
                {

                mT37();

                }
                break;
            case 25 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:106: T38
                {

                mT38();

                }
                break;
            case 26 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:110: T39
                {

                mT39();

                }
                break;
            case 27 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:114: T40
                {

                mT40();

                }
                break;
            case 28 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:118: T41
                {

                mT41();

                }
                break;
            case 29 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:122: T42
                {

                mT42();

                }
                break;
            case 30 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:126: T43
                {

                mT43();

                }
                break;
            case 31 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:130: MISC
                {

                mMISC();

                }
                break;
            case 32 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:135: WS
                {

                mWS();

                }
                break;
            case 33 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:138: EOL
                {

                mEOL();

                }
                break;
            case 34 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:142: INT
                {

                mINT();

                }
                break;
            case 35 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:146: FLOAT
                {

                mFLOAT();

                }
                break;
            case 36 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:152: STRING
                {

                mSTRING();

                }
                break;
            case 37 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:159: ID
                {

                mID();

                }
                break;
            case 38 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:162: SH_STYLE_SINGLE_LINE_COMMENT
                {

                mSH_STYLE_SINGLE_LINE_COMMENT();

                }
                break;
            case 39 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:191: C_STYLE_SINGLE_LINE_COMMENT
                {

                mC_STYLE_SINGLE_LINE_COMMENT();

                }
                break;
            case 40 :
                // /Users/bob/Documents/workspace/jbossrules/drools-compiler/src/main/java/org/drools/lang/RuleParser.lexer.g:1:219: MULTI_LINE_COMMENT
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
        DFA.State s229 = new DFA.State() {{alt=1;}};
        DFA.State s31 = new DFA.State() {{alt=37;}};
        DFA.State s213 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_213 = input.LA(1);
                if ( (LA12_213>='0' && LA12_213<='9')||(LA12_213>='A' && LA12_213<='Z')||LA12_213=='_'||(LA12_213>='a' && LA12_213<='z') ) {return s31;}
                return s229;

            }
        };
        DFA.State s195 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_195 = input.LA(1);
                if ( LA12_195=='e' ) {return s213;}
                return s31;

            }
        };
        DFA.State s169 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_169 = input.LA(1);
                if ( LA12_169=='g' ) {return s195;}
                return s31;

            }
        };
        DFA.State s131 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_131 = input.LA(1);
                if ( LA12_131=='a' ) {return s169;}
                return s31;

            }
        };
        DFA.State s88 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_88 = input.LA(1);
                if ( LA12_88=='k' ) {return s131;}
                return s31;

            }
        };
        DFA.State s34 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_34 = input.LA(1);
                if ( LA12_34=='c' ) {return s88;}
                return s31;

            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_1 = input.LA(1);
                if ( LA12_1=='a' ) {return s34;}
                return s31;

            }
        };
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {{alt=3;}};
        DFA.State s216 = new DFA.State() {{alt=4;}};
        DFA.State s198 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_198 = input.LA(1);
                if ( (LA12_198>='0' && LA12_198<='9')||(LA12_198>='A' && LA12_198<='Z')||LA12_198=='_'||(LA12_198>='a' && LA12_198<='z') ) {return s31;}
                return s216;

            }
        };
        DFA.State s172 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_172 = input.LA(1);
                if ( LA12_172=='t' ) {return s198;}
                return s31;

            }
        };
        DFA.State s134 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_134 = input.LA(1);
                if ( LA12_134=='r' ) {return s172;}
                return s31;

            }
        };
        DFA.State s91 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_91 = input.LA(1);
                if ( LA12_91=='o' ) {return s134;}
                return s31;

            }
        };
        DFA.State s37 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_37 = input.LA(1);
                if ( LA12_37=='p' ) {return s91;}
                return s31;

            }
        };
        DFA.State s4 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_4 = input.LA(1);
                if ( LA12_4=='m' ) {return s37;}
                return s31;

            }
        };
        DFA.State s137 = new DFA.State() {{alt=11;}};
        DFA.State s94 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_94 = input.LA(1);
                if ( (LA12_94>='0' && LA12_94<='9')||(LA12_94>='A' && LA12_94<='Z')||LA12_94=='_'||(LA12_94>='a' && LA12_94<='z') ) {return s31;}
                return s137;

            }
        };
        DFA.State s40 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_40 = input.LA(1);
                if ( LA12_40=='d' ) {return s94;}
                return s31;

            }
        };
        DFA.State s175 = new DFA.State() {{alt=29;}};
        DFA.State s139 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_139 = input.LA(1);
                if ( (LA12_139>='0' && LA12_139<='9')||(LA12_139>='A' && LA12_139<='Z')||LA12_139=='_'||(LA12_139>='a' && LA12_139<='z') ) {return s31;}
                return s175;

            }
        };
        DFA.State s97 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_97 = input.LA(1);
                if ( LA12_97=='l' ) {return s139;}
                return s31;

            }
        };
        DFA.State s41 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_41 = input.LA(1);
                if ( LA12_41=='a' ) {return s97;}
                return s31;

            }
        };
        DFA.State s239 = new DFA.State() {{alt=5;}};
        DFA.State s231 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_231 = input.LA(1);
                if ( (LA12_231>='0' && LA12_231<='9')||(LA12_231>='A' && LA12_231<='Z')||LA12_231=='_'||(LA12_231>='a' && LA12_231<='z') ) {return s31;}
                return s239;

            }
        };
        DFA.State s218 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_218 = input.LA(1);
                if ( LA12_218=='r' ) {return s231;}
                return s31;

            }
        };
        DFA.State s201 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_201 = input.LA(1);
                if ( LA12_201=='e' ) {return s218;}
                return s31;

            }
        };
        DFA.State s177 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_177 = input.LA(1);
                if ( LA12_177=='d' ) {return s201;}
                return s31;

            }
        };
        DFA.State s142 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_142 = input.LA(1);
                if ( LA12_142=='n' ) {return s177;}
                return s31;

            }
        };
        DFA.State s100 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_100 = input.LA(1);
                if ( LA12_100=='a' ) {return s142;}
                return s31;

            }
        };
        DFA.State s221 = new DFA.State() {{alt=27;}};
        DFA.State s204 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_204 = input.LA(1);
                if ( (LA12_204>='0' && LA12_204<='9')||(LA12_204>='A' && LA12_204<='Z')||LA12_204=='_'||(LA12_204>='a' && LA12_204<='z') ) {return s31;}
                return s221;

            }
        };
        DFA.State s180 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_180 = input.LA(1);
                if ( LA12_180=='s' ) {return s204;}
                return s31;

            }
        };
        DFA.State s145 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_145 = input.LA(1);
                if ( LA12_145=='t' ) {return s180;}
                return s31;

            }
        };
        DFA.State s101 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_101 = input.LA(1);
                if ( LA12_101=='s' ) {return s145;}
                return s31;

            }
        };
        DFA.State s42 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 'p':
                    return s100;

                case 'i':
                    return s101;

                default:
                    return s31;
        	        }
            }
        };
        DFA.State s5 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 'n':
                    return s40;

                case 'v':
                    return s41;

                case 'x':
                    return s42;

                default:
                    return s31;
        	        }
            }
        };
        DFA.State s183 = new DFA.State() {{alt=6;}};
        DFA.State s148 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_148 = input.LA(1);
                if ( (LA12_148>='0' && LA12_148<='9')||(LA12_148>='A' && LA12_148<='Z')||LA12_148=='_'||(LA12_148>='a' && LA12_148<='z') ) {return s31;}
                return s183;

            }
        };
        DFA.State s104 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_104 = input.LA(1);
                if ( LA12_104=='e' ) {return s148;}
                return s31;

            }
        };
        DFA.State s45 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_45 = input.LA(1);
                if ( LA12_45=='l' ) {return s104;}
                return s31;

            }
        };
        DFA.State s6 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_6 = input.LA(1);
                if ( LA12_6=='u' ) {return s45;}
                return s31;

            }
        };
        DFA.State s185 = new DFA.State() {{alt=7;}};
        DFA.State s151 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_151 = input.LA(1);
                if ( (LA12_151>='0' && LA12_151<='9')||(LA12_151>='A' && LA12_151<='Z')||LA12_151=='_'||(LA12_151>='a' && LA12_151<='z') ) {return s31;}
                return s185;

            }
        };
        DFA.State s107 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_107 = input.LA(1);
                if ( LA12_107=='n' ) {return s151;}
                return s31;

            }
        };
        DFA.State s48 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_48 = input.LA(1);
                if ( LA12_48=='e' ) {return s107;}
                return s31;

            }
        };
        DFA.State s7 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_7 = input.LA(1);
                if ( LA12_7=='h' ) {return s48;}
                return s31;

            }
        };
        DFA.State s8 = new DFA.State() {{alt=8;}};
        DFA.State s51 = new DFA.State() {{alt=19;}};
        DFA.State s52 = new DFA.State() {{alt=9;}};
        DFA.State s9 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_9 = input.LA(1);
                if ( LA12_9=='=' ) {return s51;}
                return s52;

            }
        };
        DFA.State s187 = new DFA.State() {{alt=10;}};
        DFA.State s154 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_154 = input.LA(1);
                if ( (LA12_154>='0' && LA12_154<='9')||(LA12_154>='A' && LA12_154<='Z')||LA12_154=='_'||(LA12_154>='a' && LA12_154<='z') ) {return s31;}
                return s187;

            }
        };
        DFA.State s110 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_110 = input.LA(1);
                if ( LA12_110=='n' ) {return s154;}
                return s31;

            }
        };
        DFA.State s53 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_53 = input.LA(1);
                if ( LA12_53=='e' ) {return s110;}
                return s31;

            }
        };
        DFA.State s10 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_10 = input.LA(1);
                if ( LA12_10=='h' ) {return s53;}
                return s31;

            }
        };
        DFA.State s234 = new DFA.State() {{alt=12;}};
        DFA.State s223 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_223 = input.LA(1);
                if ( (LA12_223>='0' && LA12_223<='9')||(LA12_223>='A' && LA12_223<='Z')||LA12_223=='_'||(LA12_223>='a' && LA12_223<='z') ) {return s31;}
                return s234;

            }
        };
        DFA.State s207 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_207 = input.LA(1);
                if ( LA12_207=='s' ) {return s223;}
                return s31;

            }
        };
        DFA.State s189 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_189 = input.LA(1);
                if ( LA12_189=='n' ) {return s207;}
                return s31;

            }
        };
        DFA.State s157 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_157 = input.LA(1);
                if ( LA12_157=='o' ) {return s189;}
                return s31;

            }
        };
        DFA.State s113 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_113 = input.LA(1);
                if ( LA12_113=='i' ) {return s157;}
                return s31;

            }
        };
        DFA.State s56 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_56 = input.LA(1);
                if ( LA12_56=='t' ) {return s113;}
                return s31;

            }
        };
        DFA.State s116 = new DFA.State() {{alt=23;}};
        DFA.State s57 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_57 = input.LA(1);
                if ( (LA12_57>='0' && LA12_57<='9')||(LA12_57>='A' && LA12_57<='Z')||LA12_57=='_'||(LA12_57>='a' && LA12_57<='z') ) {return s31;}
                return s116;

            }
        };
        DFA.State s11 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 'p':
                    return s56;

                case 'r':
                    return s57;

                default:
                    return s31;
        	        }
            }
        };
        DFA.State s241 = new DFA.State() {{alt=13;}};
        DFA.State s236 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_236 = input.LA(1);
                if ( (LA12_236>='0' && LA12_236<='9')||(LA12_236>='A' && LA12_236<='Z')||LA12_236=='_'||(LA12_236>='a' && LA12_236<='z') ) {return s31;}
                return s241;

            }
        };
        DFA.State s226 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_226 = input.LA(1);
                if ( LA12_226=='e' ) {return s236;}
                return s31;

            }
        };
        DFA.State s210 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_210 = input.LA(1);
                if ( LA12_210=='c' ) {return s226;}
                return s31;

            }
        };
        DFA.State s192 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_192 = input.LA(1);
                if ( LA12_192=='n' ) {return s210;}
                return s31;

            }
        };
        DFA.State s160 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_160 = input.LA(1);
                if ( LA12_160=='e' ) {return s192;}
                return s31;

            }
        };
        DFA.State s118 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_118 = input.LA(1);
                if ( LA12_118=='i' ) {return s160;}
                return s31;

            }
        };
        DFA.State s60 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_60 = input.LA(1);
                if ( LA12_60=='l' ) {return s118;}
                return s31;

            }
        };
        DFA.State s12 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_12 = input.LA(1);
                if ( LA12_12=='a' ) {return s60;}
                return s31;

            }
        };
        DFA.State s163 = new DFA.State() {{alt=28;}};
        DFA.State s121 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_121 = input.LA(1);
                if ( (LA12_121>='0' && LA12_121<='9')||(LA12_121>='A' && LA12_121<='Z')||LA12_121=='_'||(LA12_121>='a' && LA12_121<='z') ) {return s31;}
                return s163;

            }
        };
        DFA.State s122 = new DFA.State() {{alt=14;}};
        DFA.State s63 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 't':
                    return s121;

                case '-':
                    return s122;

                default:
                    return s31;
        	        }
            }
        };
        DFA.State s13 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_13 = input.LA(1);
                if ( LA12_13=='o' ) {return s63;}
                return s31;

            }
        };
        DFA.State s14 = new DFA.State() {{alt=15;}};
        DFA.State s15 = new DFA.State() {{alt=16;}};
        DFA.State s66 = new DFA.State() {{alt=17;}};
        DFA.State s16 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_16 = input.LA(1);
                return s66;

            }
        };
        DFA.State s17 = new DFA.State() {{alt=18;}};
        DFA.State s67 = new DFA.State() {{alt=21;}};
        DFA.State s68 = new DFA.State() {{alt=20;}};
        DFA.State s18 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_18 = input.LA(1);
                if ( LA12_18=='=' ) {return s67;}
                return s68;

            }
        };
        DFA.State s69 = new DFA.State() {{alt=22;}};
        DFA.State s30 = new DFA.State() {{alt=31;}};
        DFA.State s19 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_19 = input.LA(1);
                if ( LA12_19=='=' ) {return s69;}
                return s30;

            }
        };
        DFA.State s71 = new DFA.State() {{alt=24;}};
        DFA.State s20 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_20 = input.LA(1);
                if ( LA12_20=='|' ) {return s71;}
                return s30;

            }
        };
        DFA.State s165 = new DFA.State() {{alt=25;}};
        DFA.State s125 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_125 = input.LA(1);
                if ( (LA12_125>='0' && LA12_125<='9')||(LA12_125>='A' && LA12_125<='Z')||LA12_125=='_'||(LA12_125>='a' && LA12_125<='z') ) {return s31;}
                return s165;

            }
        };
        DFA.State s73 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_73 = input.LA(1);
                if ( LA12_73=='d' ) {return s125;}
                return s31;

            }
        };
        DFA.State s21 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_21 = input.LA(1);
                if ( LA12_21=='n' ) {return s73;}
                return s31;

            }
        };
        DFA.State s76 = new DFA.State() {{alt=26;}};
        DFA.State s22 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_22 = input.LA(1);
                if ( LA12_22=='&' ) {return s76;}
                return s30;

            }
        };
        DFA.State s167 = new DFA.State() {{alt=30;}};
        DFA.State s128 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_128 = input.LA(1);
                if ( (LA12_128>='0' && LA12_128<='9')||(LA12_128>='A' && LA12_128<='Z')||LA12_128=='_'||(LA12_128>='a' && LA12_128<='z') ) {return s31;}
                return s167;

            }
        };
        DFA.State s78 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_78 = input.LA(1);
                if ( LA12_78=='e' ) {return s128;}
                return s31;

            }
        };
        DFA.State s23 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_23 = input.LA(1);
                if ( LA12_23=='s' ) {return s78;}
                return s31;

            }
        };
        DFA.State s24 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_24 = input.LA(1);
                if ( (LA12_24>='0' && LA12_24<='9')||(LA12_24>='A' && LA12_24<='Z')||LA12_24=='_'||(LA12_24>='a' && LA12_24<='z') ) {return s31;}
                return s30;

            }
        };
        DFA.State s25 = new DFA.State() {{alt=32;}};
        DFA.State s26 = new DFA.State() {{alt=33;}};
        DFA.State s83 = new DFA.State() {{alt=34;}};
        DFA.State s85 = new DFA.State() {{alt=35;}};
        DFA.State s28 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
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
                    return s28;

                case '.':
                    return s85;

                default:
                    return s83;
        	        }
            }
        };
        DFA.State s29 = new DFA.State() {{alt=36;}};
        DFA.State s32 = new DFA.State() {{alt=38;}};
        DFA.State s86 = new DFA.State() {{alt=40;}};
        DFA.State s87 = new DFA.State() {{alt=39;}};
        DFA.State s33 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_33 = input.LA(1);
                if ( LA12_33=='*' ) {return s86;}
                if ( LA12_33=='/' ) {return s87;}


                NoViableAltException nvae =
        	    new NoViableAltException("", 12, 33, input);

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

                case 'e':
                    return s5;

                case 'r':
                    return s6;

                case 'w':
                    return s7;

                case ':':
                    return s8;

                case '>':
                    return s9;

                case 't':
                    return s10;

                case 'o':
                    return s11;

                case 's':
                    return s12;

                case 'n':
                    return s13;

                case '(':
                    return s14;

                case ')':
                    return s15;

                case ',':
                    return s16;

                case '=':
                    return s17;

                case '<':
                    return s18;

                case '!':
                    return s19;

                case '|':
                    return s20;

                case 'a':
                    return s21;

                case '&':
                    return s22;

                case 'u':
                    return s23;

                case '_':
                    return s24;

                case '\t':
                case '\f':
                case ' ':
                    return s25;

                case '\n':
                case '\r':
                    return s26;

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
                    return s28;

                case '"':
                    return s29;

                case '$':
                case '%':
                case '*':
                case '+':
                case '-':
                case '@':
                case '[':
                case ']':
                case '^':
                case '{':
                case '}':
                    return s30;

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
                    return s31;

                case '#':
                    return s32;

                case '/':
                    return s33;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 0, input);

                    throw nvae;        }
            }
        };

    }
}