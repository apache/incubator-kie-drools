// $ANTLR 3.0ea7 C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g 2006-03-11 22:09:42

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
    public static final int T44=44;
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:6:7: ( 'package' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:6:7: 'package'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:7:7: ( '.' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:7:7: '.'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:8:7: ( ';' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:8:7: ';'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:9:7: ( 'import' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:9:7: 'import'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:10:7: ( 'expander' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:10:7: 'expander'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:11:7: ( 'rule' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:11:7: 'rule'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:12:7: ( 'when' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:12:7: 'when'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:13:7: ( ':' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:13:7: ':'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:14:7: ( '>' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:14:7: '>'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:15:7: ( 'then' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:15:7: 'then'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:16:7: ( 'end' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:16:7: 'end'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:17:7: ( 'options' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:17:7: 'options'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:18:7: ( 'salience' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:18:7: 'salience'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:19:7: ( 'no-loop' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:19:7: 'no-loop'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:20:7: ( '(' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:20:7: '('
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:21:7: ( ')' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:21:7: ')'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:22:7: ( ',' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:22:7: ','
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:23:7: ( '==' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:23:7: '=='
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:24:7: ( '>=' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:24:7: '>='
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:25:7: ( '<' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:25:7: '<'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:26:7: ( '<=' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:26:7: '<='
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:27:7: ( '!=' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:27:7: '!='
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:28:7: ( 'contains' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:28:7: 'contains'
        {

        match("contains");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT37() throws RecognitionException {
        int type = T37;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:29:7: ( 'or' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:29:7: 'or'
        {

        match("or");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT38() throws RecognitionException {
        int type = T38;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:30:7: ( '||' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:30:7: '||'
        {

        match("||");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT39() throws RecognitionException {
        int type = T39;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:31:7: ( 'and' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:31:7: 'and'
        {

        match("and");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT40() throws RecognitionException {
        int type = T40;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:32:7: ( '&&' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:32:7: '&&'
        {

        match("&&");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT41() throws RecognitionException {
        int type = T41;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:33:7: ( 'exists' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:33:7: 'exists'
        {

        match("exists");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT42() throws RecognitionException {
        int type = T42;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:34:7: ( 'not' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:34:7: 'not'
        {

        match("not");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT43() throws RecognitionException {
        int type = T43;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:35:7: ( 'eval' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:35:7: 'eval'
        {

        match("eval");


        }

        if ( token==null ) {emit(type,line,charPosition,channel,start,getCharIndex()-1);}
    }

    public void mT44() throws RecognitionException {
        int type = T44;
        int start = getCharIndex();
        int line = getLine();
        int charPosition = getCharPositionInLine();
        int channel = Token.DEFAULT_CHANNEL;
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:36:7: ( 'use' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:36:7: 'use'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:38:8: ( ('!'|'@'|'$'|'%'|'^'|'&'|'*'|'_'|'-'|'+'|'|'|','|'{'|'}'|'['|']'))
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:38:8: ('!'|'@'|'$'|'%'|'^'|'&'|'*'|'_'|'-'|'+'|'|'|','|'{'|'}'|'['|']')
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:40:6: ( (' '|'\t'|'\f'))
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:40:6: (' '|'\t'|'\f')
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:42:7: ( ( '\r\n' | '\r' | '\n' ) )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:42:7: ( '\r\n' | '\r' | '\n' )
        {

        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:42:7: ( '\r\n' | '\r' | '\n' )
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
                new NoViableAltException("42:7: ( \'\\r\\n\' | \'\\r\' | \'\\n\' )", 1, 0, input);

            throw nvae;
        }
        switch (alt1) {
            case 1 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:42:8: '\r\n'
                {

                match("\r\n");


                }
                break;
            case 2 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:42:15: '\r'
                {

                match('\r');

                }
                break;
            case 3 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:42:20: '\n'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:44:7: ( ( '0' .. '9' )+ )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:44:7: ( '0' .. '9' )+
        {

        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:44:7: ( '0' .. '9' )+
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
        	    // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:44:9: '0' .. '9'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:46:9: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:46:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )+
        {

        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:46:9: ( '0' .. '9' )+
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
        	    // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:46:11: '0' .. '9'
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

        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:46:29: ( '0' .. '9' )+
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
        	    // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:46:31: '0' .. '9'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:48:10: ( '"' ( options {greedy=false; } : . )* '"' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:48:10: '"' ( options {greedy=false; } : . )* '"'
        {

        match('"');

        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:48:14: ( options {greedy=false; } : . )*
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
        	    // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:48:43: .
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:50:6: ( ('a'..'z'|'A'..'Z'|'_'|'$') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))* )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:50:6: ('a'..'z'|'A'..'Z'|'_'|'$') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))*
        {

        if ( input.LA(1)=='$'||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            input.consume();
            errorRecovery=false;
        }
        else {
            MismatchedSetException mse =
                new MismatchedSetException(null,input);
            recover(mse);    throw mse;
        }


        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:50:34: ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))*
        loop6:
        do {
            int alt6=2;
            int LA6_0 = input.LA(1);
            if ( (LA6_0>='0' && LA6_0<='9')||(LA6_0>='A' && LA6_0<='Z')||LA6_0=='_'||(LA6_0>='a' && LA6_0<='z') ) {
                alt6=1;
            }


            switch (alt6) {
        	case 1 :
        	    // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:50:36: ('a'..'z'|'A'..'Z'|'_'|'0'..'9')
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:52:32: ( '#' ( options {greedy=false; } : . )* ( '\r' )? '\n' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:52:32: '#' ( options {greedy=false; } : . )* ( '\r' )? '\n'
        {

        match('#');

        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:52:36: ( options {greedy=false; } : . )*
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
        	    // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:52:65: .
        	    {

        	    matchAny();

        	    }
        	    break;

        	default :
        	    break loop7;
            }
        } while (true);


        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:52:70: ( '\r' )?
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
                new NoViableAltException("52:70: ( \'\\r\' )?", 8, 0, input);

            throw nvae;
        }
        switch (alt8) {
            case 1 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:52:72: '\r'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:54:31: ( '//' ( options {greedy=false; } : . )* ( '\r' )? '\n' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:54:31: '//' ( options {greedy=false; } : . )* ( '\r' )? '\n'
        {

        match("//");


        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:54:36: ( options {greedy=false; } : . )*
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
        	    // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:54:65: .
        	    {

        	    matchAny();

        	    }
        	    break;

        	default :
        	    break loop9;
            }
        } while (true);


        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:54:70: ( '\r' )?
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
                new NoViableAltException("54:70: ( \'\\r\' )?", 10, 0, input);

            throw nvae;
        }
        switch (alt10) {
            case 1 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:54:72: '\r'
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:56:22: ( '/*' ( options {greedy=false; } : . )* '*/' )
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:56:22: '/*' ( options {greedy=false; } : . )* '*/'
        {

        match("/*");


        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:56:27: ( options {greedy=false; } : . )*
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
        	    // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:56:56: .
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
        // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:10: ( T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | T27 | T28 | T29 | T30 | T31 | T32 | T33 | T34 | T35 | T36 | T37 | T38 | T39 | T40 | T41 | T42 | T43 | T44 | MISC | WS | EOL | INT | FLOAT | STRING | ID | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT )
        int alt12=41;
        alt12 = dfa12.predict(input);
        switch (alt12) {
            case 1 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:10: T14
                {

                mT14();

                }
                break;
            case 2 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:14: T15
                {

                mT15();

                }
                break;
            case 3 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:18: T16
                {

                mT16();

                }
                break;
            case 4 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:22: T17
                {

                mT17();

                }
                break;
            case 5 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:26: T18
                {

                mT18();

                }
                break;
            case 6 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:30: T19
                {

                mT19();

                }
                break;
            case 7 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:34: T20
                {

                mT20();

                }
                break;
            case 8 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:38: T21
                {

                mT21();

                }
                break;
            case 9 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:42: T22
                {

                mT22();

                }
                break;
            case 10 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:46: T23
                {

                mT23();

                }
                break;
            case 11 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:50: T24
                {

                mT24();

                }
                break;
            case 12 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:54: T25
                {

                mT25();

                }
                break;
            case 13 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:58: T26
                {

                mT26();

                }
                break;
            case 14 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:62: T27
                {

                mT27();

                }
                break;
            case 15 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:66: T28
                {

                mT28();

                }
                break;
            case 16 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:70: T29
                {

                mT29();

                }
                break;
            case 17 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:74: T30
                {

                mT30();

                }
                break;
            case 18 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:78: T31
                {

                mT31();

                }
                break;
            case 19 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:82: T32
                {

                mT32();

                }
                break;
            case 20 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:86: T33
                {

                mT33();

                }
                break;
            case 21 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:90: T34
                {

                mT34();

                }
                break;
            case 22 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:94: T35
                {

                mT35();

                }
                break;
            case 23 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:98: T36
                {

                mT36();

                }
                break;
            case 24 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:102: T37
                {

                mT37();

                }
                break;
            case 25 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:106: T38
                {

                mT38();

                }
                break;
            case 26 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:110: T39
                {

                mT39();

                }
                break;
            case 27 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:114: T40
                {

                mT40();

                }
                break;
            case 28 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:118: T41
                {

                mT41();

                }
                break;
            case 29 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:122: T42
                {

                mT42();

                }
                break;
            case 30 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:126: T43
                {

                mT43();

                }
                break;
            case 31 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:130: T44
                {

                mT44();

                }
                break;
            case 32 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:134: MISC
                {

                mMISC();

                }
                break;
            case 33 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:139: WS
                {

                mWS();

                }
                break;
            case 34 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:142: EOL
                {

                mEOL();

                }
                break;
            case 35 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:146: INT
                {

                mINT();

                }
                break;
            case 36 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:150: FLOAT
                {

                mFLOAT();

                }
                break;
            case 37 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:156: STRING
                {

                mSTRING();

                }
                break;
            case 38 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:163: ID
                {

                mID();

                }
                break;
            case 39 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:166: SH_STYLE_SINGLE_LINE_COMMENT
                {

                mSH_STYLE_SINGLE_LINE_COMMENT();

                }
                break;
            case 40 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:195: C_STYLE_SINGLE_LINE_COMMENT
                {

                mC_STYLE_SINGLE_LINE_COMMENT();

                }
                break;
            case 41 :
                // C:\Projects\jboss-rules\drools-compiler\src\main\java\org\drools\lang\RuleParser.lexer.g:1:223: MULTI_LINE_COMMENT
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
        DFA.State s248 = new DFA.State() {{alt=1;}};
        DFA.State s32 = new DFA.State() {{alt=38;}};
        DFA.State s229 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_229 = input.LA(1);
                if ( (LA12_229>='0' && LA12_229<='9')||(LA12_229>='A' && LA12_229<='Z')||LA12_229=='_'||(LA12_229>='a' && LA12_229<='z') ) {return s32;}
                return s248;

            }
        };
        DFA.State s208 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_208 = input.LA(1);
                if ( LA12_208=='e' ) {return s229;}
                return s32;

            }
        };
        DFA.State s179 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_179 = input.LA(1);
                if ( LA12_179=='g' ) {return s208;}
                return s32;

            }
        };
        DFA.State s138 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_138 = input.LA(1);
                if ( LA12_138=='a' ) {return s179;}
                return s32;

            }
        };
        DFA.State s92 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_92 = input.LA(1);
                if ( LA12_92=='k' ) {return s138;}
                return s32;

            }
        };
        DFA.State s35 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_35 = input.LA(1);
                if ( LA12_35=='c' ) {return s92;}
                return s32;

            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_1 = input.LA(1);
                if ( LA12_1=='a' ) {return s35;}
                return s32;

            }
        };
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {{alt=3;}};
        DFA.State s232 = new DFA.State() {{alt=4;}};
        DFA.State s211 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_211 = input.LA(1);
                if ( (LA12_211>='0' && LA12_211<='9')||(LA12_211>='A' && LA12_211<='Z')||LA12_211=='_'||(LA12_211>='a' && LA12_211<='z') ) {return s32;}
                return s232;

            }
        };
        DFA.State s182 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_182 = input.LA(1);
                if ( LA12_182=='t' ) {return s211;}
                return s32;

            }
        };
        DFA.State s141 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_141 = input.LA(1);
                if ( LA12_141=='r' ) {return s182;}
                return s32;

            }
        };
        DFA.State s95 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_95 = input.LA(1);
                if ( LA12_95=='o' ) {return s141;}
                return s32;

            }
        };
        DFA.State s38 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_38 = input.LA(1);
                if ( LA12_38=='p' ) {return s95;}
                return s32;

            }
        };
        DFA.State s4 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_4 = input.LA(1);
                if ( LA12_4=='m' ) {return s38;}
                return s32;

            }
        };
        DFA.State s234 = new DFA.State() {{alt=28;}};
        DFA.State s214 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_214 = input.LA(1);
                if ( (LA12_214>='0' && LA12_214<='9')||(LA12_214>='A' && LA12_214<='Z')||LA12_214=='_'||(LA12_214>='a' && LA12_214<='z') ) {return s32;}
                return s234;

            }
        };
        DFA.State s185 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_185 = input.LA(1);
                if ( LA12_185=='s' ) {return s214;}
                return s32;

            }
        };
        DFA.State s144 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_144 = input.LA(1);
                if ( LA12_144=='t' ) {return s185;}
                return s32;

            }
        };
        DFA.State s98 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_98 = input.LA(1);
                if ( LA12_98=='s' ) {return s144;}
                return s32;

            }
        };
        DFA.State s261 = new DFA.State() {{alt=5;}};
        DFA.State s250 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_250 = input.LA(1);
                if ( (LA12_250>='0' && LA12_250<='9')||(LA12_250>='A' && LA12_250<='Z')||LA12_250=='_'||(LA12_250>='a' && LA12_250<='z') ) {return s32;}
                return s261;

            }
        };
        DFA.State s236 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_236 = input.LA(1);
                if ( LA12_236=='r' ) {return s250;}
                return s32;

            }
        };
        DFA.State s217 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_217 = input.LA(1);
                if ( LA12_217=='e' ) {return s236;}
                return s32;

            }
        };
        DFA.State s188 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_188 = input.LA(1);
                if ( LA12_188=='d' ) {return s217;}
                return s32;

            }
        };
        DFA.State s147 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_147 = input.LA(1);
                if ( LA12_147=='n' ) {return s188;}
                return s32;

            }
        };
        DFA.State s99 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_99 = input.LA(1);
                if ( LA12_99=='a' ) {return s147;}
                return s32;

            }
        };
        DFA.State s41 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 'i':
                    return s98;

                case 'p':
                    return s99;

                default:
                    return s32;
        	        }
            }
        };
        DFA.State s191 = new DFA.State() {{alt=30;}};
        DFA.State s150 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_150 = input.LA(1);
                if ( (LA12_150>='0' && LA12_150<='9')||(LA12_150>='A' && LA12_150<='Z')||LA12_150=='_'||(LA12_150>='a' && LA12_150<='z') ) {return s32;}
                return s191;

            }
        };
        DFA.State s102 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_102 = input.LA(1);
                if ( LA12_102=='l' ) {return s150;}
                return s32;

            }
        };
        DFA.State s42 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_42 = input.LA(1);
                if ( LA12_42=='a' ) {return s102;}
                return s32;

            }
        };
        DFA.State s153 = new DFA.State() {{alt=11;}};
        DFA.State s105 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_105 = input.LA(1);
                if ( (LA12_105>='0' && LA12_105<='9')||(LA12_105>='A' && LA12_105<='Z')||LA12_105=='_'||(LA12_105>='a' && LA12_105<='z') ) {return s32;}
                return s153;

            }
        };
        DFA.State s43 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_43 = input.LA(1);
                if ( LA12_43=='d' ) {return s105;}
                return s32;

            }
        };
        DFA.State s5 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 'x':
                    return s41;

                case 'v':
                    return s42;

                case 'n':
                    return s43;

                default:
                    return s32;
        	        }
            }
        };
        DFA.State s193 = new DFA.State() {{alt=6;}};
        DFA.State s155 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_155 = input.LA(1);
                if ( (LA12_155>='0' && LA12_155<='9')||(LA12_155>='A' && LA12_155<='Z')||LA12_155=='_'||(LA12_155>='a' && LA12_155<='z') ) {return s32;}
                return s193;

            }
        };
        DFA.State s108 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_108 = input.LA(1);
                if ( LA12_108=='e' ) {return s155;}
                return s32;

            }
        };
        DFA.State s46 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_46 = input.LA(1);
                if ( LA12_46=='l' ) {return s108;}
                return s32;

            }
        };
        DFA.State s6 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_6 = input.LA(1);
                if ( LA12_6=='u' ) {return s46;}
                return s32;

            }
        };
        DFA.State s195 = new DFA.State() {{alt=7;}};
        DFA.State s158 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_158 = input.LA(1);
                if ( (LA12_158>='0' && LA12_158<='9')||(LA12_158>='A' && LA12_158<='Z')||LA12_158=='_'||(LA12_158>='a' && LA12_158<='z') ) {return s32;}
                return s195;

            }
        };
        DFA.State s111 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_111 = input.LA(1);
                if ( LA12_111=='n' ) {return s158;}
                return s32;

            }
        };
        DFA.State s49 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_49 = input.LA(1);
                if ( LA12_49=='e' ) {return s111;}
                return s32;

            }
        };
        DFA.State s7 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_7 = input.LA(1);
                if ( LA12_7=='h' ) {return s49;}
                return s32;

            }
        };
        DFA.State s8 = new DFA.State() {{alt=8;}};
        DFA.State s52 = new DFA.State() {{alt=19;}};
        DFA.State s53 = new DFA.State() {{alt=9;}};
        DFA.State s9 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_9 = input.LA(1);
                if ( LA12_9=='=' ) {return s52;}
                return s53;

            }
        };
        DFA.State s197 = new DFA.State() {{alt=10;}};
        DFA.State s161 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_161 = input.LA(1);
                if ( (LA12_161>='0' && LA12_161<='9')||(LA12_161>='A' && LA12_161<='Z')||LA12_161=='_'||(LA12_161>='a' && LA12_161<='z') ) {return s32;}
                return s197;

            }
        };
        DFA.State s114 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_114 = input.LA(1);
                if ( LA12_114=='n' ) {return s161;}
                return s32;

            }
        };
        DFA.State s54 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_54 = input.LA(1);
                if ( LA12_54=='e' ) {return s114;}
                return s32;

            }
        };
        DFA.State s10 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_10 = input.LA(1);
                if ( LA12_10=='h' ) {return s54;}
                return s32;

            }
        };
        DFA.State s117 = new DFA.State() {{alt=24;}};
        DFA.State s57 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_57 = input.LA(1);
                if ( (LA12_57>='0' && LA12_57<='9')||(LA12_57>='A' && LA12_57<='Z')||LA12_57=='_'||(LA12_57>='a' && LA12_57<='z') ) {return s32;}
                return s117;

            }
        };
        DFA.State s253 = new DFA.State() {{alt=12;}};
        DFA.State s239 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_239 = input.LA(1);
                if ( (LA12_239>='0' && LA12_239<='9')||(LA12_239>='A' && LA12_239<='Z')||LA12_239=='_'||(LA12_239>='a' && LA12_239<='z') ) {return s32;}
                return s253;

            }
        };
        DFA.State s220 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_220 = input.LA(1);
                if ( LA12_220=='s' ) {return s239;}
                return s32;

            }
        };
        DFA.State s199 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_199 = input.LA(1);
                if ( LA12_199=='n' ) {return s220;}
                return s32;

            }
        };
        DFA.State s164 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_164 = input.LA(1);
                if ( LA12_164=='o' ) {return s199;}
                return s32;

            }
        };
        DFA.State s119 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_119 = input.LA(1);
                if ( LA12_119=='i' ) {return s164;}
                return s32;

            }
        };
        DFA.State s58 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_58 = input.LA(1);
                if ( LA12_58=='t' ) {return s119;}
                return s32;

            }
        };
        DFA.State s11 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 'r':
                    return s57;

                case 'p':
                    return s58;

                default:
                    return s32;
        	        }
            }
        };
        DFA.State s263 = new DFA.State() {{alt=13;}};
        DFA.State s255 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_255 = input.LA(1);
                if ( (LA12_255>='0' && LA12_255<='9')||(LA12_255>='A' && LA12_255<='Z')||LA12_255=='_'||(LA12_255>='a' && LA12_255<='z') ) {return s32;}
                return s263;

            }
        };
        DFA.State s242 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_242 = input.LA(1);
                if ( LA12_242=='e' ) {return s255;}
                return s32;

            }
        };
        DFA.State s223 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_223 = input.LA(1);
                if ( LA12_223=='c' ) {return s242;}
                return s32;

            }
        };
        DFA.State s202 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_202 = input.LA(1);
                if ( LA12_202=='n' ) {return s223;}
                return s32;

            }
        };
        DFA.State s167 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_167 = input.LA(1);
                if ( LA12_167=='e' ) {return s202;}
                return s32;

            }
        };
        DFA.State s122 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_122 = input.LA(1);
                if ( LA12_122=='i' ) {return s167;}
                return s32;

            }
        };
        DFA.State s61 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_61 = input.LA(1);
                if ( LA12_61=='l' ) {return s122;}
                return s32;

            }
        };
        DFA.State s12 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_12 = input.LA(1);
                if ( LA12_12=='a' ) {return s61;}
                return s32;

            }
        };
        DFA.State s125 = new DFA.State() {{alt=14;}};
        DFA.State s170 = new DFA.State() {{alt=29;}};
        DFA.State s126 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_126 = input.LA(1);
                if ( (LA12_126>='0' && LA12_126<='9')||(LA12_126>='A' && LA12_126<='Z')||LA12_126=='_'||(LA12_126>='a' && LA12_126<='z') ) {return s32;}
                return s170;

            }
        };
        DFA.State s64 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case '-':
                    return s125;

                case 't':
                    return s126;

                default:
                    return s32;
        	        }
            }
        };
        DFA.State s13 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_13 = input.LA(1);
                if ( LA12_13=='o' ) {return s64;}
                return s32;

            }
        };
        DFA.State s14 = new DFA.State() {{alt=15;}};
        DFA.State s15 = new DFA.State() {{alt=16;}};
        DFA.State s67 = new DFA.State() {{alt=17;}};
        DFA.State s16 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_16 = input.LA(1);
                return s67;

            }
        };
        DFA.State s17 = new DFA.State() {{alt=18;}};
        DFA.State s68 = new DFA.State() {{alt=21;}};
        DFA.State s69 = new DFA.State() {{alt=20;}};
        DFA.State s18 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_18 = input.LA(1);
                if ( LA12_18=='=' ) {return s68;}
                return s69;

            }
        };
        DFA.State s70 = new DFA.State() {{alt=22;}};
        DFA.State s31 = new DFA.State() {{alt=32;}};
        DFA.State s19 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_19 = input.LA(1);
                if ( LA12_19=='=' ) {return s70;}
                return s31;

            }
        };
        DFA.State s265 = new DFA.State() {{alt=23;}};
        DFA.State s258 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_258 = input.LA(1);
                if ( (LA12_258>='0' && LA12_258<='9')||(LA12_258>='A' && LA12_258<='Z')||LA12_258=='_'||(LA12_258>='a' && LA12_258<='z') ) {return s32;}
                return s265;

            }
        };
        DFA.State s245 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_245 = input.LA(1);
                if ( LA12_245=='s' ) {return s258;}
                return s32;

            }
        };
        DFA.State s226 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_226 = input.LA(1);
                if ( LA12_226=='n' ) {return s245;}
                return s32;

            }
        };
        DFA.State s205 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_205 = input.LA(1);
                if ( LA12_205=='i' ) {return s226;}
                return s32;

            }
        };
        DFA.State s172 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_172 = input.LA(1);
                if ( LA12_172=='a' ) {return s205;}
                return s32;

            }
        };
        DFA.State s129 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_129 = input.LA(1);
                if ( LA12_129=='t' ) {return s172;}
                return s32;

            }
        };
        DFA.State s72 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_72 = input.LA(1);
                if ( LA12_72=='n' ) {return s129;}
                return s32;

            }
        };
        DFA.State s20 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_20 = input.LA(1);
                if ( LA12_20=='o' ) {return s72;}
                return s32;

            }
        };
        DFA.State s75 = new DFA.State() {{alt=25;}};
        DFA.State s21 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_21 = input.LA(1);
                if ( LA12_21=='|' ) {return s75;}
                return s31;

            }
        };
        DFA.State s175 = new DFA.State() {{alt=26;}};
        DFA.State s132 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_132 = input.LA(1);
                if ( (LA12_132>='0' && LA12_132<='9')||(LA12_132>='A' && LA12_132<='Z')||LA12_132=='_'||(LA12_132>='a' && LA12_132<='z') ) {return s32;}
                return s175;

            }
        };
        DFA.State s77 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_77 = input.LA(1);
                if ( LA12_77=='d' ) {return s132;}
                return s32;

            }
        };
        DFA.State s22 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_22 = input.LA(1);
                if ( LA12_22=='n' ) {return s77;}
                return s32;

            }
        };
        DFA.State s80 = new DFA.State() {{alt=27;}};
        DFA.State s23 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_23 = input.LA(1);
                if ( LA12_23=='&' ) {return s80;}
                return s31;

            }
        };
        DFA.State s177 = new DFA.State() {{alt=31;}};
        DFA.State s135 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_135 = input.LA(1);
                if ( (LA12_135>='0' && LA12_135<='9')||(LA12_135>='A' && LA12_135<='Z')||LA12_135=='_'||(LA12_135>='a' && LA12_135<='z') ) {return s32;}
                return s177;

            }
        };
        DFA.State s82 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_82 = input.LA(1);
                if ( LA12_82=='e' ) {return s135;}
                return s32;

            }
        };
        DFA.State s24 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_24 = input.LA(1);
                if ( LA12_24=='s' ) {return s82;}
                return s32;

            }
        };
        DFA.State s25 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_25 = input.LA(1);
                if ( (LA12_25>='0' && LA12_25<='9')||(LA12_25>='A' && LA12_25<='Z')||LA12_25=='_'||(LA12_25>='a' && LA12_25<='z') ) {return s32;}
                return s31;

            }
        };
        DFA.State s26 = new DFA.State() {{alt=33;}};
        DFA.State s27 = new DFA.State() {{alt=34;}};
        DFA.State s87 = new DFA.State() {{alt=35;}};
        DFA.State s89 = new DFA.State() {{alt=36;}};
        DFA.State s29 = new DFA.State() {
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
                    return s29;

                case '.':
                    return s89;

                default:
                    return s87;
        	        }
            }
        };
        DFA.State s30 = new DFA.State() {{alt=37;}};
        DFA.State s33 = new DFA.State() {{alt=39;}};
        DFA.State s90 = new DFA.State() {{alt=41;}};
        DFA.State s91 = new DFA.State() {{alt=40;}};
        DFA.State s34 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_34 = input.LA(1);
                if ( LA12_34=='*' ) {return s90;}
                if ( LA12_34=='/' ) {return s91;}


                NoViableAltException nvae =
        	    new NoViableAltException("", 12, 34, input);

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

                case 'c':
                    return s20;

                case '|':
                    return s21;

                case 'a':
                    return s22;

                case '&':
                    return s23;

                case 'u':
                    return s24;

                case '$':
                case '_':
                    return s25;

                case '\t':
                case '\f':
                case ' ':
                    return s26;

                case '\n':
                case '\r':
                    return s27;

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
                    return s29;

                case '"':
                    return s30;

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
                    return s31;

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
                    return s32;

                case '#':
                    return s33;

                case '/':
                    return s34;

                default:

                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 0, input);

                    throw nvae;        }
            }
        };

    }
}