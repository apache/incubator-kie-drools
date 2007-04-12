// $ANTLR 3.0b5 D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g 2006-11-22 14:58:23

package org.drools.rule.builder.dialect.java.parser;

import org.antlr.runtime.*;

public class JavaParserLexer extends Lexer {
    public static final int         SR_ASSIGN         = 22;
    public static final int         COMMA             = 12;
    public static final int         MINUS             = 44;
    public static final int         T70               = 70;
    public static final int         T74               = 74;
    public static final int         T85               = 85;
    public static final int         BOR               = 31;
    public static final int         DOT               = 7;
    public static final int         SR                = 41;
    public static final int         T102              = 102;
    public static final int         LCURLY            = 9;
    public static final int         T114              = 114;
    public static final int         T103              = 103;
    public static final int         STRING_LITERAL    = 53;
    public static final int         LE                = 38;
    public static final int         T81               = 81;
    public static final int         RPAREN            = 14;
    public static final int         STAR_ASSIGN       = 19;
    public static final int         NUM_INT           = 51;
    public static final int         PLUS              = 43;
    public static final int         MINUS_ASSIGN      = 18;
    public static final int         T113              = 113;
    public static final int         T109              = 109;
    public static final int         IDENT             = 6;
    public static final int         DECIMAL_LITERAL   = 58;
    public static final int         T68               = 68;
    public static final int         T73               = 73;
    public static final int         T84               = 84;
    public static final int         MOD_ASSIGN        = 21;
    public static final int         T78               = 78;
    public static final int         T115              = 115;
    public static final int         WS                = 55;
    public static final int         LT                = 36;
    public static final int         BSR               = 42;
    public static final int         SL_ASSIGN         = 24;
    public static final int         T96               = 96;
    public static final int         T71               = 71;
    public static final int         T72               = 72;
    public static final int         T94               = 94;
    public static final int         LAND              = 30;
    public static final int         LBRACK            = 4;
    public static final int         T76               = 76;
    public static final int         NUM_FLOAT         = 54;
    public static final int         SEMI              = 10;
    public static final int         GE                = 39;
    public static final int         LNOT              = 50;
    public static final int         DIV_ASSIGN        = 20;
    public static final int         T75               = 75;
    public static final int         UNICODE_CHAR      = 66;
    public static final int         EQUAL             = 35;
    public static final int         T89               = 89;
    public static final int         OCTAL_DIGIT       = 65;
    public static final int         COLON             = 16;
    public static final int         SL                = 40;
    public static final int         T82               = 82;
    public static final int         DIV               = 45;
    public static final int         T100              = 100;
    public static final int         EXPONENT_PART     = 62;
    public static final int         T79               = 79;
    public static final int         LOR               = 29;
    public static final int         BNOT              = 49;
    public static final int         INC               = 47;
    public static final int         T93               = 93;
    public static final int         T107              = 107;
    public static final int         MOD               = 46;
    public static final int         OCTAL_LITERAL     = 60;
    public static final int         PLUS_ASSIGN       = 17;
    public static final int         T83               = 83;
    public static final int         QUESTION          = 28;
    public static final int         HEX_LITERAL       = 59;
    public static final int         T101              = 101;
    public static final int         FLOAT_TYPE_SUFFIX = 63;
    public static final int         RCURLY            = 11;
    public static final int         T91               = 91;
    public static final int         T105              = 105;
    public static final int         T86               = 86;
    public static final int         CHAR_LITERAL      = 52;
    public static final int         BOR_ASSIGN        = 27;
    public static final int         ASSIGN            = 15;
    public static final int         LPAREN            = 13;
    public static final int         T111              = 111;
    public static final int         HEX_DIGIT         = 67;
    public static final int         T77               = 77;
    public static final int         ML_COMMENT        = 57;
    public static final int         SL_COMMENT        = 56;
    public static final int         BAND              = 33;
    public static final int         T106              = 106;
    public static final int         T112              = 112;
    public static final int         T69               = 69;
    public static final int         NOT_EQUAL         = 34;
    public static final int         BAND_ASSIGN       = 25;
    public static final int         T95               = 95;
    public static final int         DIGITS            = 61;
    public static final int         T110              = 110;
    public static final int         T108              = 108;
    public static final int         T92               = 92;
    public static final int         BXOR_ASSIGN       = 26;
    public static final int         GT                = 37;
    public static final int         BSR_ASSIGN        = 23;
    public static final int         T88               = 88;
    public static final int         T98               = 98;
    public static final int         T87               = 87;
    public static final int         T80               = 80;
    public static final int         DEC               = 48;
    public static final int         T97               = 97;
    public static final int         ESCAPE_SEQUENCE   = 64;
    public static final int         EOF               = -1;
    public static final int         T104              = 104;
    public static final int         Tokens            = 116;
    public static final int         RBRACK            = 5;
    public static final int         T99               = 99;
    public static final int         STAR              = 8;
    public static final int         BXOR              = 32;
    public static final int         T90               = 90;

    public static final CommonToken IGNORE_TOKEN      = new CommonToken( null,
                                                                         0,
                                                                         99,
                                                                         0,
                                                                         0 );

    public JavaParserLexer() {
        ;
    }

    public JavaParserLexer(final CharStream input) {
        super( input );
    }

    public String getGrammarFileName() {
        return "D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g";
    }

    // $ANTLR start T68
    public void mT68() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T68;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:9:7: ( 'void' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:9:7: 'void'
            {
                match( "void" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T68

    // $ANTLR start T69
    public void mT69() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T69;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:10:7: ( 'boolean' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:10:7: 'boolean'
            {
                match( "boolean" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T69

    // $ANTLR start T70
    public void mT70() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T70;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:11:7: ( 'byte' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:11:7: 'byte'
            {
                match( "byte" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T70

    // $ANTLR start T71
    public void mT71() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T71;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:12:7: ( 'char' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:12:7: 'char'
            {
                match( "char" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T71

    // $ANTLR start T72
    public void mT72() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T72;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:13:7: ( 'short' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:13:7: 'short'
            {
                match( "short" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T72

    // $ANTLR start T73
    public void mT73() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T73;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:14:7: ( 'int' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:14:7: 'int'
            {
                match( "int" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T73

    // $ANTLR start T74
    public void mT74() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T74;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:15:7: ( 'float' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:15:7: 'float'
            {
                match( "float" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T74

    // $ANTLR start T75
    public void mT75() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T75;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:16:7: ( 'long' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:16:7: 'long'
            {
                match( "long" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T75

    // $ANTLR start T76
    public void mT76() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T76;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:17:7: ( 'double' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:17:7: 'double'
            {
                match( "double" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T76

    // $ANTLR start T77
    public void mT77() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T77;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:18:7: ( 'private' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:18:7: 'private'
            {
                match( "private" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T77

    // $ANTLR start T78
    public void mT78() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T78;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:19:7: ( 'public' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:19:7: 'public'
            {
                match( "public" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T78

    // $ANTLR start T79
    public void mT79() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T79;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:20:7: ( 'protected' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:20:7: 'protected'
            {
                match( "protected" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T79

    // $ANTLR start T80
    public void mT80() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T80;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:21:7: ( 'static' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:21:7: 'static'
            {
                match( "static" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T80

    // $ANTLR start T81
    public void mT81() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T81;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:22:7: ( 'transient' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:22:7: 'transient'
            {
                match( "transient" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T81

    // $ANTLR start T82
    public void mT82() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T82;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:23:7: ( 'final' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:23:7: 'final'
            {
                match( "final" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T82

    // $ANTLR start T83
    public void mT83() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T83;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:24:7: ( 'abstract' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:24:7: 'abstract'
            {
                match( "abstract" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T83

    // $ANTLR start T84
    public void mT84() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T84;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:25:7: ( 'native' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:25:7: 'native'
            {
                match( "native" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T84

    // $ANTLR start T85
    public void mT85() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T85;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:26:7: ( 'threadsafe' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:26:7: 'threadsafe'
            {
                match( "threadsafe" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T85

    // $ANTLR start T86
    public void mT86() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T86;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:27:7: ( 'synchronized' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:27:7: 'synchronized'
            {
                match( "synchronized" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T86

    // $ANTLR start T87
    public void mT87() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T87;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:28:7: ( 'volatile' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:28:7: 'volatile'
            {
                match( "volatile" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T87

    // $ANTLR start T88
    public void mT88() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T88;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:29:7: ( 'strictfp' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:29:7: 'strictfp'
            {
                match( "strictfp" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T88

    // $ANTLR start T89
    public void mT89() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T89;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:30:7: ( 'class' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:30:7: 'class'
            {
                match( "class" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T89

    // $ANTLR start T90
    public void mT90() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T90;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:31:7: ( 'extends' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:31:7: 'extends'
            {
                match( "extends" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T90

    // $ANTLR start T91
    public void mT91() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T91;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:32:7: ( 'interface' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:32:7: 'interface'
            {
                match( "interface" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T91

    // $ANTLR start T92
    public void mT92() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T92;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:33:7: ( 'implements' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:33:7: 'implements'
            {
                match( "implements" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T92

    // $ANTLR start T93
    public void mT93() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T93;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:34:7: ( 'this' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:34:7: 'this'
            {
                match( "this" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T93

    // $ANTLR start T94
    public void mT94() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T94;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:35:7: ( 'super' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:35:7: 'super'
            {
                match( "super" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T94

    // $ANTLR start T95
    public void mT95() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T95;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:36:7: ( 'throws' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:36:7: 'throws'
            {
                match( "throws" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T95

    // $ANTLR start T96
    public void mT96() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T96;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:37:7: ( 'if' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:37:7: 'if'
            {
                match( "if" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T96

    // $ANTLR start T97
    public void mT97() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T97;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:38:7: ( 'else' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:38:7: 'else'
            {
                match( "else" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T97

    // $ANTLR start T98
    public void mT98() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T98;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:39:7: ( 'for' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:39:7: 'for'
            {
                match( "for" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T98

    // $ANTLR start T99
    public void mT99() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T99;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:40:7: ( 'while' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:40:7: 'while'
            {
                match( "while" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T99

    // $ANTLR start T100
    public void mT100() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T100;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:41:8: ( 'do' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:41:8: 'do'
            {
                match( "do" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T100

    // $ANTLR start T101
    public void mT101() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T101;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:42:8: ( 'break' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:42:8: 'break'
            {
                match( "break" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T101

    // $ANTLR start T102
    public void mT102() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T102;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:43:8: ( 'continue' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:43:8: 'continue'
            {
                match( "continue" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T102

    // $ANTLR start T103
    public void mT103() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T103;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:44:8: ( 'return' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:44:8: 'return'
            {
                match( "return" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T103

    // $ANTLR start T104
    public void mT104() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T104;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:45:8: ( 'switch' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:45:8: 'switch'
            {
                match( "switch" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T104

    // $ANTLR start T105
    public void mT105() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T105;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:46:8: ( 'throw' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:46:8: 'throw'
            {
                match( "throw" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T105

    // $ANTLR start T106
    public void mT106() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T106;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:47:8: ( 'case' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:47:8: 'case'
            {
                match( "case" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T106

    // $ANTLR start T107
    public void mT107() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T107;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:48:8: ( 'default' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:48:8: 'default'
            {
                match( "default" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T107

    // $ANTLR start T108
    public void mT108() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T108;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:49:8: ( 'try' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:49:8: 'try'
            {
                match( "try" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T108

    // $ANTLR start T109
    public void mT109() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T109;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:50:8: ( 'finally' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:50:8: 'finally'
            {
                match( "finally" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T109

    // $ANTLR start T110
    public void mT110() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T110;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:51:8: ( 'catch' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:51:8: 'catch'
            {
                match( "catch" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T110

    // $ANTLR start T111
    public void mT111() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T111;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:52:8: ( 'instanceof' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:52:8: 'instanceof'
            {
                match( "instanceof" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T111

    // $ANTLR start T112
    public void mT112() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T112;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:53:8: ( 'true' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:53:8: 'true'
            {
                match( "true" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T112

    // $ANTLR start T113
    public void mT113() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T113;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:54:8: ( 'false' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:54:8: 'false'
            {
                match( "false" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T113

    // $ANTLR start T114
    public void mT114() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T114;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:55:8: ( 'null' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:55:8: 'null'
            {
                match( "null" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T114

    // $ANTLR start T115
    public void mT115() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = T115;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:56:8: ( 'new' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:56:8: 'new'
            {
                match( "new" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end T115

    // $ANTLR start QUESTION
    public void mQUESTION() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = QUESTION;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:914:13: ( '?' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:914:13: '?'
            {
                match( '?' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end QUESTION

    // $ANTLR start LPAREN
    public void mLPAREN() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = LPAREN;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:917:12: ( '(' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:917:12: '('
            {
                match( '(' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end LPAREN

    // $ANTLR start RPAREN
    public void mRPAREN() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = RPAREN;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:920:12: ( ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:920:12: ')'
            {
                match( ')' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end RPAREN

    // $ANTLR start LBRACK
    public void mLBRACK() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = LBRACK;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:923:12: ( '[' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:923:12: '['
            {
                match( '[' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end LBRACK

    // $ANTLR start RBRACK
    public void mRBRACK() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = RBRACK;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:926:12: ( ']' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:926:12: ']'
            {
                match( ']' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end RBRACK

    // $ANTLR start LCURLY
    public void mLCURLY() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = LCURLY;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:929:12: ( '{' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:929:12: '{'
            {
                match( '{' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end LCURLY

    // $ANTLR start RCURLY
    public void mRCURLY() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = RCURLY;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:932:12: ( '}' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:932:12: '}'
            {
                match( '}' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end RCURLY

    // $ANTLR start COLON
    public void mCOLON() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = COLON;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:935:11: ( ':' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:935:11: ':'
            {
                match( ':' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end COLON

    // $ANTLR start COMMA
    public void mCOMMA() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = COMMA;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:938:11: ( ',' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:938:11: ','
            {
                match( ',' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end COMMA

    // $ANTLR start DOT
    public void mDOT() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = DOT;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:940:10: ( '.' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:940:10: '.'
            {
                match( '.' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end DOT

    // $ANTLR start ASSIGN
    public void mASSIGN() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = ASSIGN;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:942:12: ( '=' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:942:12: '='
            {
                match( '=' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end ASSIGN

    // $ANTLR start EQUAL
    public void mEQUAL() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = EQUAL;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:945:11: ( '==' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:945:11: '=='
            {
                match( "==" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end EQUAL

    // $ANTLR start LNOT
    public void mLNOT() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = LNOT;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:948:10: ( '!' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:948:10: '!'
            {
                match( '!' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end LNOT

    // $ANTLR start BNOT
    public void mBNOT() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = BNOT;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:951:10: ( '~' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:951:10: '~'
            {
                match( '~' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end BNOT

    // $ANTLR start NOT_EQUAL
    public void mNOT_EQUAL() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = NOT_EQUAL;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:954:14: ( '!=' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:954:14: '!='
            {
                match( "!=" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end NOT_EQUAL

    // $ANTLR start DIV
    public void mDIV() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = DIV;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:957:10: ( '/' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:957:10: '/'
            {
                match( '/' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end DIV

    // $ANTLR start DIV_ASSIGN
    public void mDIV_ASSIGN() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = DIV_ASSIGN;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:960:15: ( '/=' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:960:15: '/='
            {
                match( "/=" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end DIV_ASSIGN

    // $ANTLR start PLUS
    public void mPLUS() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = PLUS;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:963:10: ( '+' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:963:10: '+'
            {
                match( '+' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end PLUS

    // $ANTLR start PLUS_ASSIGN
    public void mPLUS_ASSIGN() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = PLUS_ASSIGN;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:966:16: ( '+=' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:966:16: '+='
            {
                match( "+=" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end PLUS_ASSIGN

    // $ANTLR start INC
    public void mINC() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = INC;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:969:10: ( '++' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:969:10: '++'
            {
                match( "++" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end INC

    // $ANTLR start MINUS
    public void mMINUS() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = MINUS;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:972:11: ( '-' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:972:11: '-'
            {
                match( '-' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end MINUS

    // $ANTLR start MINUS_ASSIGN
    public void mMINUS_ASSIGN() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = MINUS_ASSIGN;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:975:16: ( '-=' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:975:16: '-='
            {
                match( "-=" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end MINUS_ASSIGN

    // $ANTLR start DEC
    public void mDEC() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = DEC;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:978:10: ( '--' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:978:10: '--'
            {
                match( "--" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end DEC

    // $ANTLR start STAR
    public void mSTAR() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = STAR;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:981:10: ( '*' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:981:10: '*'
            {
                match( '*' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end STAR

    // $ANTLR start STAR_ASSIGN
    public void mSTAR_ASSIGN() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = STAR_ASSIGN;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:984:16: ( '*=' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:984:16: '*='
            {
                match( "*=" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end STAR_ASSIGN

    // $ANTLR start MOD
    public void mMOD() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = MOD;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:987:10: ( '%' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:987:10: '%'
            {
                match( '%' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end MOD

    // $ANTLR start MOD_ASSIGN
    public void mMOD_ASSIGN() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = MOD_ASSIGN;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:990:15: ( '%=' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:990:15: '%='
            {
                match( "%=" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end MOD_ASSIGN

    // $ANTLR start SR
    public void mSR() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = SR;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:993:9: ( '>>' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:993:9: '>>'
            {
                match( ">>" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end SR

    // $ANTLR start SR_ASSIGN
    public void mSR_ASSIGN() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = SR_ASSIGN;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:996:14: ( '>>=' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:996:14: '>>='
            {
                match( ">>=" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end SR_ASSIGN

    // $ANTLR start BSR
    public void mBSR() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = BSR;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:999:10: ( '>>>' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:999:10: '>>>'
            {
                match( ">>>" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end BSR

    // $ANTLR start BSR_ASSIGN
    public void mBSR_ASSIGN() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = BSR_ASSIGN;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1002:15: ( '>>>=' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1002:15: '>>>='
            {
                match( ">>>=" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end BSR_ASSIGN

    // $ANTLR start GE
    public void mGE() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = GE;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1005:9: ( '>=' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1005:9: '>='
            {
                match( ">=" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end GE

    // $ANTLR start GT
    public void mGT() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = GT;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1008:9: ( '>' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1008:9: '>'
            {
                match( '>' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end GT

    // $ANTLR start SL
    public void mSL() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = SL;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1011:9: ( '<<' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1011:9: '<<'
            {
                match( "<<" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end SL

    // $ANTLR start SL_ASSIGN
    public void mSL_ASSIGN() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = SL_ASSIGN;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1014:14: ( '<<=' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1014:14: '<<='
            {
                match( "<<=" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end SL_ASSIGN

    // $ANTLR start LE
    public void mLE() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = LE;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1017:9: ( '<=' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1017:9: '<='
            {
                match( "<=" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end LE

    // $ANTLR start LT
    public void mLT() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = LT;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1020:9: ( '<' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1020:9: '<'
            {
                match( '<' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end LT

    // $ANTLR start BXOR
    public void mBXOR() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = BXOR;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1023:10: ( '^' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1023:10: '^'
            {
                match( '^' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end BXOR

    // $ANTLR start BXOR_ASSIGN
    public void mBXOR_ASSIGN() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = BXOR_ASSIGN;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1026:16: ( '^=' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1026:16: '^='
            {
                match( "^=" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end BXOR_ASSIGN

    // $ANTLR start BOR
    public void mBOR() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = BOR;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1029:10: ( '|' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1029:10: '|'
            {
                match( '|' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end BOR

    // $ANTLR start BOR_ASSIGN
    public void mBOR_ASSIGN() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = BOR_ASSIGN;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1032:15: ( '|=' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1032:15: '|='
            {
                match( "|=" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end BOR_ASSIGN

    // $ANTLR start LOR
    public void mLOR() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = LOR;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1035:10: ( '||' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1035:10: '||'
            {
                match( "||" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end LOR

    // $ANTLR start BAND
    public void mBAND() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = BAND;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1038:10: ( '&' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1038:10: '&'
            {
                match( '&' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end BAND

    // $ANTLR start BAND_ASSIGN
    public void mBAND_ASSIGN() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = BAND_ASSIGN;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1041:16: ( '&=' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1041:16: '&='
            {
                match( "&=" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end BAND_ASSIGN

    // $ANTLR start LAND
    public void mLAND() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = LAND;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1044:10: ( '&&' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1044:10: '&&'
            {
                match( "&&" );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end LAND

    // $ANTLR start SEMI
    public void mSEMI() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = SEMI;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1047:10: ( ';' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1047:10: ';'
            {
                match( ';' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end SEMI

    // $ANTLR start WS
    public void mWS() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = WS;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1053:6: ( ( ' ' | '\\t' | '\\f' | ( '\\r\\n' | '\\r' | '\\n' ) )+ )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1053:6: ( ' ' | '\\t' | '\\f' | ( '\\r\\n' | '\\r' | '\\n' ) )+
            {
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1053:6: ( ' ' | '\\t' | '\\f' | ( '\\r\\n' | '\\r' | '\\n' ) )+
                int cnt2 = 0;
                loop2 : do {
                    int alt2 = 5;
                    switch ( this.input.LA( 1 ) ) {
                        case ' ' :
                            alt2 = 1;
                            break;
                        case '\t' :
                            alt2 = 2;
                            break;
                        case '\f' :
                            alt2 = 3;
                            break;
                        case '\n' :
                        case '\r' :
                            alt2 = 4;
                            break;

                    }

                    switch ( alt2 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1053:8: ' '
                        {
                            match( ' ' );

                        }
                            break;
                        case 2 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1054:5: '\\t'
                        {
                            match( '\t' );

                        }
                            break;
                        case 3 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1055:5: '\\f'
                        {
                            match( '\f' );

                        }
                            break;
                        case 4 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1057:5: ( '\\r\\n' | '\\r' | '\\n' )
                        {
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1057:5: ( '\\r\\n' | '\\r' | '\\n' )
                            int alt1 = 3;
                            final int LA1_0 = this.input.LA( 1 );
                            if ( (LA1_0 == '\r') ) {
                                final int LA1_1 = this.input.LA( 2 );
                                if ( (LA1_1 == '\n') ) {
                                    alt1 = 1;
                                } else {
                                    alt1 = 2;
                                }
                            } else if ( (LA1_0 == '\n') ) {
                                alt1 = 3;
                            } else {
                                final NoViableAltException nvae = new NoViableAltException( "1057:5: ( '\\r\\n' | '\\r' | '\\n' )",
                                                                                      1,
                                                                                      0,
                                                                                      this.input );

                                throw nvae;
                            }
                            switch ( alt1 ) {
                                case 1 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1057:7: '\\r\\n'
                                {
                                    match( "\r\n" );

                                }
                                    break;
                                case 2 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1058:6: '\\r'
                                {
                                    match( '\r' );

                                }
                                    break;
                                case 3 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1059:6: '\\n'
                                {
                                    match( '\n' );

                                }
                                    break;

                            }

                        }
                            break;

                        default :
                            if ( cnt2 >= 1 ) {
                                break loop2;
                            }
                            final EarlyExitException eee = new EarlyExitException( 2,
                                                                             this.input );
                            throw eee;
                    }
                    cnt2++;
                } while ( true );

                _channel = HIDDEN; /*token = JavaParser.IGNORE_TOKEN;*/

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end WS

    // $ANTLR start SL_COMMENT
    public void mSL_COMMENT() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = SL_COMMENT;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1069:4: ( '//' ( options {greedy=false; } : . )* ( '\\r' )? '\\n' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1069:4: '//' ( options {greedy=false; } : . )* ( '\\r' )? '\\n'
            {
                match( "//" );

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1069:9: ( options {greedy=false; } : . )*
                loop3 : do {
                    int alt3 = 2;
                    final int LA3_0 = this.input.LA( 1 );
                    if ( (LA3_0 == '\r') ) {
                        alt3 = 2;
                    } else if ( (LA3_0 == '\n') ) {
                        alt3 = 2;
                    } else if ( ((LA3_0 >= '\u0000' && LA3_0 <= '\t') || (LA3_0 >= '\u000B' && LA3_0 <= '\f') || (LA3_0 >= '\u000E' && LA3_0 <= '\uFFFE')) ) {
                        alt3 = 1;
                    }

                    switch ( alt3 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1069:36: .
                        {
                            matchAny();

                        }
                            break;

                        default :
                            break loop3;
                    }
                } while ( true );

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1069:40: ( '\\r' )?
                int alt4 = 2;
                final int LA4_0 = this.input.LA( 1 );
                if ( (LA4_0 == '\r') ) {
                    alt4 = 1;
                }
                switch ( alt4 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1069:41: '\\r'
                    {
                        match( '\r' );

                    }
                        break;

                }

                match( '\n' );
                _channel = HIDDEN; /*token = JavaParser.IGNORE_TOKEN;*/

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end SL_COMMENT

    // $ANTLR start ML_COMMENT
    public void mML_COMMENT() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = ML_COMMENT;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1077:4: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1077:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
                match( "/*" );

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1078:3: ( options {greedy=false; } : . )*
                loop5 : do {
                    int alt5 = 2;
                    final int LA5_0 = this.input.LA( 1 );
                    if ( (LA5_0 == '*') ) {
                        final int LA5_1 = this.input.LA( 2 );
                        if ( (LA5_1 == '/') ) {
                            alt5 = 2;
                        } else if ( ((LA5_1 >= '\u0000' && LA5_1 <= '.') || (LA5_1 >= '0' && LA5_1 <= '\uFFFE')) ) {
                            alt5 = 1;
                        }

                    } else if ( ((LA5_0 >= '\u0000' && LA5_0 <= ')') || (LA5_0 >= '+' && LA5_0 <= '\uFFFE')) ) {
                        alt5 = 1;
                    }

                    switch ( alt5 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1078:31: .
                        {
                            matchAny();

                        }
                            break;

                        default :
                            break loop5;
                    }
                } while ( true );

                match( "*/" );

                _channel = HIDDEN;/*token = JavaParser.IGNORE_TOKEN;*/

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end ML_COMMENT

    // $ANTLR start IDENT
    public void mIDENT() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = IDENT;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1084:4: ( ('a'..'z'|'A'..'Z'|'_'|'$') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$'))* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1084:4: ('a'..'z'|'A'..'Z'|'_'|'$') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$'))*
            {
                if ( this.input.LA( 1 ) == '$' || (this.input.LA( 1 ) >= 'A' && this.input.LA( 1 ) <= 'Z') || this.input.LA( 1 ) == '_' || (this.input.LA( 1 ) >= 'a' && this.input.LA( 1 ) <= 'z') ) {
                    this.input.consume();

                } else {
                    final MismatchedSetException mse = new MismatchedSetException( null,
                                                                             this.input );
                    recover( mse );
                    throw mse;
                }

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1084:32: ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$'))*
                loop6 : do {
                    int alt6 = 2;
                    final int LA6_0 = this.input.LA( 1 );
                    if ( (LA6_0 == '$' || (LA6_0 >= '0' && LA6_0 <= '9') || (LA6_0 >= 'A' && LA6_0 <= 'Z') || LA6_0 == '_' || (LA6_0 >= 'a' && LA6_0 <= 'z')) ) {
                        alt6 = 1;
                    }

                    switch ( alt6 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1084:33: ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$')
                        {
                            if ( this.input.LA( 1 ) == '$' || (this.input.LA( 1 ) >= '0' && this.input.LA( 1 ) <= '9') || (this.input.LA( 1 ) >= 'A' && this.input.LA( 1 ) <= 'Z') || this.input.LA( 1 ) == '_' || (this.input.LA( 1 ) >= 'a' && this.input.LA( 1 ) <= 'z') ) {
                                this.input.consume();

                            } else {
                                final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                         this.input );
                                recover( mse );
                                throw mse;
                            }

                        }
                            break;

                        default :
                            break loop6;
                    }
                } while ( true );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end IDENT

    // $ANTLR start NUM_INT
    public void mNUM_INT() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = NUM_INT;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1090:7: ( DECIMAL_LITERAL | HEX_LITERAL | OCTAL_LITERAL )
            int alt7 = 3;
            final int LA7_0 = this.input.LA( 1 );
            if ( ((LA7_0 >= '1' && LA7_0 <= '9')) ) {
                alt7 = 1;
            } else if ( (LA7_0 == '0') ) {
                final int LA7_2 = this.input.LA( 2 );
                if ( (LA7_2 == 'X' || LA7_2 == 'x') ) {
                    alt7 = 2;
                } else {
                    alt7 = 3;
                }
            } else {
                final NoViableAltException nvae = new NoViableAltException( "1089:1: NUM_INT : ( DECIMAL_LITERAL | HEX_LITERAL | OCTAL_LITERAL );",
                                                                      7,
                                                                      0,
                                                                      this.input );

                throw nvae;
            }
            switch ( alt7 ) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1090:7: DECIMAL_LITERAL
                {
                    mDECIMAL_LITERAL();

                }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1091:7: HEX_LITERAL
                {
                    mHEX_LITERAL();

                }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1092:7: OCTAL_LITERAL
                {
                    mOCTAL_LITERAL();

                }
                    break;

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end NUM_INT

    // $ANTLR start DECIMAL_LITERAL
    public void mDECIMAL_LITERAL() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1096:18: ( '1' .. '9' ( '0' .. '9' )* ( ('l'|'L'))? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1096:18: '1' .. '9' ( '0' .. '9' )* ( ('l'|'L'))?
            {
                matchRange( '1',
                            '9' );
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1096:27: ( '0' .. '9' )*
                loop8 : do {
                    int alt8 = 2;
                    final int LA8_0 = this.input.LA( 1 );
                    if ( ((LA8_0 >= '0' && LA8_0 <= '9')) ) {
                        alt8 = 1;
                    }

                    switch ( alt8 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1096:28: '0' .. '9'
                        {
                            matchRange( '0',
                                        '9' );

                        }
                            break;

                        default :
                            break loop8;
                    }
                } while ( true );

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1096:39: ( ('l'|'L'))?
                int alt9 = 2;
                final int LA9_0 = this.input.LA( 1 );
                if ( (LA9_0 == 'L' || LA9_0 == 'l') ) {
                    alt9 = 1;
                }
                switch ( alt9 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1096:40: ('l'|'L')
                    {
                        if ( this.input.LA( 1 ) == 'L' || this.input.LA( 1 ) == 'l' ) {
                            this.input.consume();

                        } else {
                            final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                     this.input );
                            recover( mse );
                            throw mse;
                        }

                    }
                        break;

                }

            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end DECIMAL_LITERAL

    // $ANTLR start HEX_LITERAL
    public void mHEX_LITERAL() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1099:14: ( '0' ('x'|'X') ( ('0'..'9'|'a'..'f'|'A'..'F'))+ ( ('l'|'L'))? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1099:14: '0' ('x'|'X') ( ('0'..'9'|'a'..'f'|'A'..'F'))+ ( ('l'|'L'))?
            {
                match( '0' );
                if ( this.input.LA( 1 ) == 'X' || this.input.LA( 1 ) == 'x' ) {
                    this.input.consume();

                } else {
                    final MismatchedSetException mse = new MismatchedSetException( null,
                                                                             this.input );
                    recover( mse );
                    throw mse;
                }

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1099:28: ( ('0'..'9'|'a'..'f'|'A'..'F'))+
                int cnt10 = 0;
                loop10 : do {
                    int alt10 = 2;
                    final int LA10_0 = this.input.LA( 1 );
                    if ( ((LA10_0 >= '0' && LA10_0 <= '9') || (LA10_0 >= 'A' && LA10_0 <= 'F') || (LA10_0 >= 'a' && LA10_0 <= 'f')) ) {
                        alt10 = 1;
                    }

                    switch ( alt10 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1099:29: ('0'..'9'|'a'..'f'|'A'..'F')
                        {
                            if ( (this.input.LA( 1 ) >= '0' && this.input.LA( 1 ) <= '9') || (this.input.LA( 1 ) >= 'A' && this.input.LA( 1 ) <= 'F') || (this.input.LA( 1 ) >= 'a' && this.input.LA( 1 ) <= 'f') ) {
                                this.input.consume();

                            } else {
                                final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                         this.input );
                                recover( mse );
                                throw mse;
                            }

                        }
                            break;

                        default :
                            if ( cnt10 >= 1 ) {
                                break loop10;
                            }
                            final EarlyExitException eee = new EarlyExitException( 10,
                                                                             this.input );
                            throw eee;
                    }
                    cnt10++;
                } while ( true );

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1099:58: ( ('l'|'L'))?
                int alt11 = 2;
                final int LA11_0 = this.input.LA( 1 );
                if ( (LA11_0 == 'L' || LA11_0 == 'l') ) {
                    alt11 = 1;
                }
                switch ( alt11 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1099:59: ('l'|'L')
                    {
                        if ( this.input.LA( 1 ) == 'L' || this.input.LA( 1 ) == 'l' ) {
                            this.input.consume();

                        } else {
                            final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                     this.input );
                            recover( mse );
                            throw mse;
                        }

                    }
                        break;

                }

            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end HEX_LITERAL

    // $ANTLR start OCTAL_LITERAL
    public void mOCTAL_LITERAL() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1102:16: ( '0' ( '0' .. '7' )* ( ('l'|'L'))? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1102:16: '0' ( '0' .. '7' )* ( ('l'|'L'))?
            {
                match( '0' );
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1102:20: ( '0' .. '7' )*
                loop12 : do {
                    int alt12 = 2;
                    final int LA12_0 = this.input.LA( 1 );
                    if ( ((LA12_0 >= '0' && LA12_0 <= '7')) ) {
                        alt12 = 1;
                    }

                    switch ( alt12 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1102:21: '0' .. '7'
                        {
                            matchRange( '0',
                                        '7' );

                        }
                            break;

                        default :
                            break loop12;
                    }
                } while ( true );

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1102:32: ( ('l'|'L'))?
                int alt13 = 2;
                final int LA13_0 = this.input.LA( 1 );
                if ( (LA13_0 == 'L' || LA13_0 == 'l') ) {
                    alt13 = 1;
                }
                switch ( alt13 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1102:33: ('l'|'L')
                    {
                        if ( this.input.LA( 1 ) == 'L' || this.input.LA( 1 ) == 'l' ) {
                            this.input.consume();

                        } else {
                            final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                     this.input );
                            recover( mse );
                            throw mse;
                        }

                    }
                        break;

                }

            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end OCTAL_LITERAL

    // $ANTLR start NUM_FLOAT
    public void mNUM_FLOAT() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = NUM_FLOAT;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1105:11: ( DIGITS '.' ( DIGITS )? ( EXPONENT_PART )? ( FLOAT_TYPE_SUFFIX )? | '.' DIGITS ( EXPONENT_PART )? ( FLOAT_TYPE_SUFFIX )? | DIGITS EXPONENT_PART FLOAT_TYPE_SUFFIX | DIGITS EXPONENT_PART | DIGITS FLOAT_TYPE_SUFFIX )
            int alt19 = 5;
            alt19 = this.dfa19.predict( this.input );
            switch ( alt19 ) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1105:11: DIGITS '.' ( DIGITS )? ( EXPONENT_PART )? ( FLOAT_TYPE_SUFFIX )?
                {
                    mDIGITS();
                    match( '.' );
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1105:22: ( DIGITS )?
                    int alt14 = 2;
                    final int LA14_0 = this.input.LA( 1 );
                    if ( ((LA14_0 >= '0' && LA14_0 <= '9')) ) {
                        alt14 = 1;
                    }
                    switch ( alt14 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1105:23: DIGITS
                        {
                            mDIGITS();

                        }
                            break;

                    }

                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1105:32: ( EXPONENT_PART )?
                    int alt15 = 2;
                    final int LA15_0 = this.input.LA( 1 );
                    if ( (LA15_0 == 'E' || LA15_0 == 'e') ) {
                        alt15 = 1;
                    }
                    switch ( alt15 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1105:33: EXPONENT_PART
                        {
                            mEXPONENT_PART();

                        }
                            break;

                    }

                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1105:49: ( FLOAT_TYPE_SUFFIX )?
                    int alt16 = 2;
                    final int LA16_0 = this.input.LA( 1 );
                    if ( (LA16_0 == 'D' || LA16_0 == 'F' || LA16_0 == 'd' || LA16_0 == 'f') ) {
                        alt16 = 1;
                    }
                    switch ( alt16 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1105:50: FLOAT_TYPE_SUFFIX
                        {
                            mFLOAT_TYPE_SUFFIX();

                        }
                            break;

                    }

                }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1106:7: '.' DIGITS ( EXPONENT_PART )? ( FLOAT_TYPE_SUFFIX )?
                {
                    match( '.' );
                    mDIGITS();
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1106:18: ( EXPONENT_PART )?
                    int alt17 = 2;
                    final int LA17_0 = this.input.LA( 1 );
                    if ( (LA17_0 == 'E' || LA17_0 == 'e') ) {
                        alt17 = 1;
                    }
                    switch ( alt17 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1106:19: EXPONENT_PART
                        {
                            mEXPONENT_PART();

                        }
                            break;

                    }

                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1106:35: ( FLOAT_TYPE_SUFFIX )?
                    int alt18 = 2;
                    final int LA18_0 = this.input.LA( 1 );
                    if ( (LA18_0 == 'D' || LA18_0 == 'F' || LA18_0 == 'd' || LA18_0 == 'f') ) {
                        alt18 = 1;
                    }
                    switch ( alt18 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1106:36: FLOAT_TYPE_SUFFIX
                        {
                            mFLOAT_TYPE_SUFFIX();

                        }
                            break;

                    }

                }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1107:11: DIGITS EXPONENT_PART FLOAT_TYPE_SUFFIX
                {
                    mDIGITS();
                    mEXPONENT_PART();
                    mFLOAT_TYPE_SUFFIX();

                }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1108:11: DIGITS EXPONENT_PART
                {
                    mDIGITS();
                    mEXPONENT_PART();

                }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1109:11: DIGITS FLOAT_TYPE_SUFFIX
                {
                    mDIGITS();
                    mFLOAT_TYPE_SUFFIX();

                }
                    break;

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end NUM_FLOAT

    // $ANTLR start DIGITS
    public void mDIGITS() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1114:10: ( ( '0' .. '9' )+ )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1114:10: ( '0' .. '9' )+
            {
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1114:10: ( '0' .. '9' )+
                int cnt20 = 0;
                loop20 : do {
                    int alt20 = 2;
                    final int LA20_0 = this.input.LA( 1 );
                    if ( ((LA20_0 >= '0' && LA20_0 <= '9')) ) {
                        alt20 = 1;
                    }

                    switch ( alt20 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1114:11: '0' .. '9'
                        {
                            matchRange( '0',
                                        '9' );

                        }
                            break;

                        default :
                            if ( cnt20 >= 1 ) {
                                break loop20;
                            }
                            final EarlyExitException eee = new EarlyExitException( 20,
                                                                             this.input );
                            throw eee;
                    }
                    cnt20++;
                } while ( true );

            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end DIGITS

    // $ANTLR start EXPONENT_PART
    public void mEXPONENT_PART() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1122:16: ( ('e'|'E') ( ('+'|'-'))? DIGITS )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1122:16: ('e'|'E') ( ('+'|'-'))? DIGITS
            {
                if ( this.input.LA( 1 ) == 'E' || this.input.LA( 1 ) == 'e' ) {
                    this.input.consume();

                } else {
                    final MismatchedSetException mse = new MismatchedSetException( null,
                                                                             this.input );
                    recover( mse );
                    throw mse;
                }

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1122:26: ( ('+'|'-'))?
                int alt21 = 2;
                final int LA21_0 = this.input.LA( 1 );
                if ( (LA21_0 == '+' || LA21_0 == '-') ) {
                    alt21 = 1;
                }
                switch ( alt21 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1122:27: ('+'|'-')
                    {
                        if ( this.input.LA( 1 ) == '+' || this.input.LA( 1 ) == '-' ) {
                            this.input.consume();

                        } else {
                            final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                     this.input );
                            recover( mse );
                            throw mse;
                        }

                    }
                        break;

                }

                mDIGITS();

            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end EXPONENT_PART

    // $ANTLR start FLOAT_TYPE_SUFFIX
    public void mFLOAT_TYPE_SUFFIX() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1125:23: ( ('f'|'F'|'d'|'D'))
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1125:23: ('f'|'F'|'d'|'D')
            {
                if ( this.input.LA( 1 ) == 'D' || this.input.LA( 1 ) == 'F' || this.input.LA( 1 ) == 'd' || this.input.LA( 1 ) == 'f' ) {
                    this.input.consume();

                } else {
                    final MismatchedSetException mse = new MismatchedSetException( null,
                                                                             this.input );
                    recover( mse );
                    throw mse;
                }

            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end FLOAT_TYPE_SUFFIX

    // $ANTLR start CHAR_LITERAL
    public void mCHAR_LITERAL() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = CHAR_LITERAL;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1129:7: ( '\\'' (~ ('\\''|'\\\\') | ESCAPE_SEQUENCE ) '\\'' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1129:7: '\\'' (~ ('\\''|'\\\\') | ESCAPE_SEQUENCE ) '\\''
            {
                match( '\'' );
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1130:7: (~ ('\\''|'\\\\') | ESCAPE_SEQUENCE )
                int alt22 = 2;
                final int LA22_0 = this.input.LA( 1 );
                if ( ((LA22_0 >= '\u0000' && LA22_0 <= '&') || (LA22_0 >= '(' && LA22_0 <= '[') || (LA22_0 >= ']' && LA22_0 <= '\uFFFE')) ) {
                    alt22 = 1;
                } else if ( (LA22_0 == '\\') ) {
                    alt22 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "1130:7: (~ ('\\''|'\\\\') | ESCAPE_SEQUENCE )",
                                                                          22,
                                                                          0,
                                                                          this.input );

                    throw nvae;
                }
                switch ( alt22 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1130:9: ~ ('\\''|'\\\\')
                    {
                        if ( (this.input.LA( 1 ) >= '\u0000' && this.input.LA( 1 ) <= '&') || (this.input.LA( 1 ) >= '(' && this.input.LA( 1 ) <= '[') || (this.input.LA( 1 ) >= ']' && this.input.LA( 1 ) <= '\uFFFE') ) {
                            this.input.consume();

                        } else {
                            final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                     this.input );
                            recover( mse );
                            throw mse;
                        }

                    }
                        break;
                    case 2 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1131:9: ESCAPE_SEQUENCE
                    {
                        mESCAPE_SEQUENCE();

                    }
                        break;

                }

                match( '\'' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end CHAR_LITERAL

    // $ANTLR start STRING_LITERAL
    public void mSTRING_LITERAL() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            final int _type = STRING_LITERAL;
            final int _start = getCharIndex();
            final int _line = getLine();
            final int _charPosition = getCharPositionInLine();
            final int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1138:7: ( '\\\"' (~ ('\\\"'|'\\\\') | ESCAPE_SEQUENCE )* '\\\"' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1138:7: '\\\"' (~ ('\\\"'|'\\\\') | ESCAPE_SEQUENCE )* '\\\"'
            {
                match( '\"' );
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1139:7: (~ ('\\\"'|'\\\\') | ESCAPE_SEQUENCE )*
                loop23 : do {
                    int alt23 = 3;
                    final int LA23_0 = this.input.LA( 1 );
                    if ( ((LA23_0 >= '\u0000' && LA23_0 <= '!') || (LA23_0 >= '#' && LA23_0 <= '[') || (LA23_0 >= ']' && LA23_0 <= '\uFFFE')) ) {
                        alt23 = 1;
                    } else if ( (LA23_0 == '\\') ) {
                        alt23 = 2;
                    }

                    switch ( alt23 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1139:9: ~ ('\\\"'|'\\\\')
                        {
                            if ( (this.input.LA( 1 ) >= '\u0000' && this.input.LA( 1 ) <= '!') || (this.input.LA( 1 ) >= '#' && this.input.LA( 1 ) <= '[') || (this.input.LA( 1 ) >= ']' && this.input.LA( 1 ) <= '\uFFFE') ) {
                                this.input.consume();

                            } else {
                                final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                         this.input );
                                recover( mse );
                                throw mse;
                            }

                        }
                            break;
                        case 2 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1140:9: ESCAPE_SEQUENCE
                        {
                            mESCAPE_SEQUENCE();

                        }
                            break;

                        default :
                            break loop23;
                    }
                } while ( true );

                match( '\"' );

            }

            if ( this.token == null && this.ruleNestingLevel == 1 ) {
                emit( _type,
                      _line,
                      _charPosition,
                      _channel,
                      _start,
                      getCharIndex() - 1 );
            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end STRING_LITERAL

    // $ANTLR start ESCAPE_SEQUENCE
    public void mESCAPE_SEQUENCE() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1147:7: ( '\\\\' 'b' | '\\\\' 't' | '\\\\' 'n' | '\\\\' 'f' | '\\\\' 'r' | '\\\\' '\\\"' | '\\\\' '\\'' | '\\\\' '\\\\' | '\\\\' '0' .. '3' OCTAL_DIGIT OCTAL_DIGIT | '\\\\' OCTAL_DIGIT OCTAL_DIGIT | '\\\\' OCTAL_DIGIT | UNICODE_CHAR )
            int alt24 = 12;
            final int LA24_0 = this.input.LA( 1 );
            if ( (LA24_0 == '\\') ) {
                switch ( this.input.LA( 2 ) ) {
                    case 'r' :
                        alt24 = 5;
                        break;
                    case '\\' :
                        alt24 = 8;
                        break;
                    case 't' :
                        alt24 = 2;
                        break;
                    case 'f' :
                        alt24 = 4;
                        break;
                    case '\'' :
                        alt24 = 7;
                        break;
                    case 'u' :
                        alt24 = 12;
                        break;
                    case '0' :
                    case '1' :
                    case '2' :
                    case '3' :
                        final int LA24_8 = this.input.LA( 3 );
                        if ( ((LA24_8 >= '0' && LA24_8 <= '7')) ) {
                            final int LA24_14 = this.input.LA( 4 );
                            if ( ((LA24_14 >= '0' && LA24_14 <= '7')) ) {
                                alt24 = 9;
                            } else {
                                alt24 = 10;
                            }
                        } else {
                            alt24 = 11;
                        }
                        break;
                    case 'b' :
                        alt24 = 1;
                        break;
                    case 'n' :
                        alt24 = 3;
                        break;
                    case '\"' :
                        alt24 = 6;
                        break;
                    case '4' :
                    case '5' :
                    case '6' :
                    case '7' :
                        final int LA24_12 = this.input.LA( 3 );
                        if ( ((LA24_12 >= '0' && LA24_12 <= '7')) ) {
                            alt24 = 10;
                        } else {
                            alt24 = 11;
                        }
                        break;
                    default :
                        final NoViableAltException nvae = new NoViableAltException( "1145:1: fragment ESCAPE_SEQUENCE : ( '\\\\' 'b' | '\\\\' 't' | '\\\\' 'n' | '\\\\' 'f' | '\\\\' 'r' | '\\\\' '\\\"' | '\\\\' '\\'' | '\\\\' '\\\\' | '\\\\' '0' .. '3' OCTAL_DIGIT OCTAL_DIGIT | '\\\\' OCTAL_DIGIT OCTAL_DIGIT | '\\\\' OCTAL_DIGIT | UNICODE_CHAR );",
                                                                              24,
                                                                              1,
                                                                              this.input );

                        throw nvae;
                }

            } else {
                final NoViableAltException nvae = new NoViableAltException( "1145:1: fragment ESCAPE_SEQUENCE : ( '\\\\' 'b' | '\\\\' 't' | '\\\\' 'n' | '\\\\' 'f' | '\\\\' 'r' | '\\\\' '\\\"' | '\\\\' '\\'' | '\\\\' '\\\\' | '\\\\' '0' .. '3' OCTAL_DIGIT OCTAL_DIGIT | '\\\\' OCTAL_DIGIT OCTAL_DIGIT | '\\\\' OCTAL_DIGIT | UNICODE_CHAR );",
                                                                      24,
                                                                      0,
                                                                      this.input );

                throw nvae;
            }
            switch ( alt24 ) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1147:7: '\\\\' 'b'
                {
                    match( '\\' );
                    match( 'b' );

                }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1148:9: '\\\\' 't'
                {
                    match( '\\' );
                    match( 't' );

                }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1149:9: '\\\\' 'n'
                {
                    match( '\\' );
                    match( 'n' );

                }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1150:9: '\\\\' 'f'
                {
                    match( '\\' );
                    match( 'f' );

                }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1151:9: '\\\\' 'r'
                {
                    match( '\\' );
                    match( 'r' );

                }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1152:9: '\\\\' '\\\"'
                {
                    match( '\\' );
                    match( '\"' );

                }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1153:9: '\\\\' '\\''
                {
                    match( '\\' );
                    match( '\'' );

                }
                    break;
                case 8 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1154:9: '\\\\' '\\\\'
                {
                    match( '\\' );
                    match( '\\' );

                }
                    break;
                case 9 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1155:7: '\\\\' '0' .. '3' OCTAL_DIGIT OCTAL_DIGIT
                {
                    match( '\\' );
                    matchRange( '0',
                                '3' );
                    mOCTAL_DIGIT();
                    mOCTAL_DIGIT();

                }
                    break;
                case 10 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1156:9: '\\\\' OCTAL_DIGIT OCTAL_DIGIT
                {
                    match( '\\' );
                    mOCTAL_DIGIT();
                    mOCTAL_DIGIT();

                }
                    break;
                case 11 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1157:9: '\\\\' OCTAL_DIGIT
                {
                    match( '\\' );
                    mOCTAL_DIGIT();

                }
                    break;
                case 12 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1158:4: UNICODE_CHAR
                {
                    mUNICODE_CHAR();

                }
                    break;

            }
        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end ESCAPE_SEQUENCE

    // $ANTLR start UNICODE_CHAR
    public void mUNICODE_CHAR() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1163:4: ( '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1163:4: '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
            {
                match( '\\' );
                match( 'u' );
                mHEX_DIGIT();
                mHEX_DIGIT();
                mHEX_DIGIT();
                mHEX_DIGIT();

            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end UNICODE_CHAR

    // $ANTLR start HEX_DIGIT
    public void mHEX_DIGIT() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1168:2: ( ('0'..'9'|'a'..'f'|'A'..'F'))
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1168:4: ('0'..'9'|'a'..'f'|'A'..'F')
            {
                if ( (this.input.LA( 1 ) >= '0' && this.input.LA( 1 ) <= '9') || (this.input.LA( 1 ) >= 'A' && this.input.LA( 1 ) <= 'F') || (this.input.LA( 1 ) >= 'a' && this.input.LA( 1 ) <= 'f') ) {
                    this.input.consume();

                } else {
                    final MismatchedSetException mse = new MismatchedSetException( null,
                                                                             this.input );
                    recover( mse );
                    throw mse;
                }

            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end HEX_DIGIT

    // $ANTLR start OCTAL_DIGIT
    public void mOCTAL_DIGIT() throws RecognitionException {
        try {
            this.ruleNestingLevel++;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1173:4: ( '0' .. '7' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1173:4: '0' .. '7'
            {
                matchRange( '0',
                            '7' );

            }

        } finally {
            this.ruleNestingLevel--;
        }
    }

    // $ANTLR end OCTAL_DIGIT

    public void mTokens() throws RecognitionException {
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:10: ( T68 | T69 | T70 | T71 | T72 | T73 | T74 | T75 | T76 | T77 | T78 | T79 | T80 | T81 | T82 | T83 | T84 | T85 | T86 | T87 | T88 | T89 | T90 | T91 | T92 | T93 | T94 | T95 | T96 | T97 | T98 | T99 | T100 | T101 | T102 | T103 | T104 | T105 | T106 | T107 | T108 | T109 | T110 | T111 | T112 | T113 | T114 | T115 | QUESTION | LPAREN | RPAREN | LBRACK | RBRACK | LCURLY | RCURLY | COLON | COMMA | DOT | ASSIGN | EQUAL | LNOT | BNOT | NOT_EQUAL | DIV | DIV_ASSIGN | PLUS | PLUS_ASSIGN | INC | MINUS | MINUS_ASSIGN | DEC | STAR | STAR_ASSIGN | MOD | MOD_ASSIGN | SR | SR_ASSIGN | BSR | BSR_ASSIGN | GE | GT | SL | SL_ASSIGN | LE | LT | BXOR | BXOR_ASSIGN | BOR | BOR_ASSIGN | LOR | BAND | BAND_ASSIGN | LAND | SEMI | WS | SL_COMMENT | ML_COMMENT | IDENT | NUM_INT | NUM_FLOAT | CHAR_LITERAL | STRING_LITERAL )
        int alt25 = 102;
        alt25 = this.dfa25.predict( this.input );
        switch ( alt25 ) {
            case 1 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:10: T68
            {
                mT68();

            }
                break;
            case 2 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:14: T69
            {
                mT69();

            }
                break;
            case 3 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:18: T70
            {
                mT70();

            }
                break;
            case 4 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:22: T71
            {
                mT71();

            }
                break;
            case 5 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:26: T72
            {
                mT72();

            }
                break;
            case 6 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:30: T73
            {
                mT73();

            }
                break;
            case 7 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:34: T74
            {
                mT74();

            }
                break;
            case 8 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:38: T75
            {
                mT75();

            }
                break;
            case 9 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:42: T76
            {
                mT76();

            }
                break;
            case 10 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:46: T77
            {
                mT77();

            }
                break;
            case 11 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:50: T78
            {
                mT78();

            }
                break;
            case 12 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:54: T79
            {
                mT79();

            }
                break;
            case 13 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:58: T80
            {
                mT80();

            }
                break;
            case 14 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:62: T81
            {
                mT81();

            }
                break;
            case 15 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:66: T82
            {
                mT82();

            }
                break;
            case 16 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:70: T83
            {
                mT83();

            }
                break;
            case 17 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:74: T84
            {
                mT84();

            }
                break;
            case 18 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:78: T85
            {
                mT85();

            }
                break;
            case 19 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:82: T86
            {
                mT86();

            }
                break;
            case 20 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:86: T87
            {
                mT87();

            }
                break;
            case 21 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:90: T88
            {
                mT88();

            }
                break;
            case 22 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:94: T89
            {
                mT89();

            }
                break;
            case 23 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:98: T90
            {
                mT90();

            }
                break;
            case 24 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:102: T91
            {
                mT91();

            }
                break;
            case 25 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:106: T92
            {
                mT92();

            }
                break;
            case 26 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:110: T93
            {
                mT93();

            }
                break;
            case 27 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:114: T94
            {
                mT94();

            }
                break;
            case 28 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:118: T95
            {
                mT95();

            }
                break;
            case 29 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:122: T96
            {
                mT96();

            }
                break;
            case 30 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:126: T97
            {
                mT97();

            }
                break;
            case 31 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:130: T98
            {
                mT98();

            }
                break;
            case 32 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:134: T99
            {
                mT99();

            }
                break;
            case 33 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:138: T100
            {
                mT100();

            }
                break;
            case 34 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:143: T101
            {
                mT101();

            }
                break;
            case 35 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:148: T102
            {
                mT102();

            }
                break;
            case 36 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:153: T103
            {
                mT103();

            }
                break;
            case 37 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:158: T104
            {
                mT104();

            }
                break;
            case 38 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:163: T105
            {
                mT105();

            }
                break;
            case 39 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:168: T106
            {
                mT106();

            }
                break;
            case 40 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:173: T107
            {
                mT107();

            }
                break;
            case 41 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:178: T108
            {
                mT108();

            }
                break;
            case 42 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:183: T109
            {
                mT109();

            }
                break;
            case 43 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:188: T110
            {
                mT110();

            }
                break;
            case 44 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:193: T111
            {
                mT111();

            }
                break;
            case 45 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:198: T112
            {
                mT112();

            }
                break;
            case 46 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:203: T113
            {
                mT113();

            }
                break;
            case 47 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:208: T114
            {
                mT114();

            }
                break;
            case 48 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:213: T115
            {
                mT115();

            }
                break;
            case 49 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:218: QUESTION
            {
                mQUESTION();

            }
                break;
            case 50 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:227: LPAREN
            {
                mLPAREN();

            }
                break;
            case 51 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:234: RPAREN
            {
                mRPAREN();

            }
                break;
            case 52 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:241: LBRACK
            {
                mLBRACK();

            }
                break;
            case 53 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:248: RBRACK
            {
                mRBRACK();

            }
                break;
            case 54 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:255: LCURLY
            {
                mLCURLY();

            }
                break;
            case 55 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:262: RCURLY
            {
                mRCURLY();

            }
                break;
            case 56 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:269: COLON
            {
                mCOLON();

            }
                break;
            case 57 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:275: COMMA
            {
                mCOMMA();

            }
                break;
            case 58 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:281: DOT
            {
                mDOT();

            }
                break;
            case 59 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:285: ASSIGN
            {
                mASSIGN();

            }
                break;
            case 60 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:292: EQUAL
            {
                mEQUAL();

            }
                break;
            case 61 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:298: LNOT
            {
                mLNOT();

            }
                break;
            case 62 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:303: BNOT
            {
                mBNOT();

            }
                break;
            case 63 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:308: NOT_EQUAL
            {
                mNOT_EQUAL();

            }
                break;
            case 64 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:318: DIV
            {
                mDIV();

            }
                break;
            case 65 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:322: DIV_ASSIGN
            {
                mDIV_ASSIGN();

            }
                break;
            case 66 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:333: PLUS
            {
                mPLUS();

            }
                break;
            case 67 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:338: PLUS_ASSIGN
            {
                mPLUS_ASSIGN();

            }
                break;
            case 68 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:350: INC
            {
                mINC();

            }
                break;
            case 69 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:354: MINUS
            {
                mMINUS();

            }
                break;
            case 70 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:360: MINUS_ASSIGN
            {
                mMINUS_ASSIGN();

            }
                break;
            case 71 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:373: DEC
            {
                mDEC();

            }
                break;
            case 72 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:377: STAR
            {
                mSTAR();

            }
                break;
            case 73 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:382: STAR_ASSIGN
            {
                mSTAR_ASSIGN();

            }
                break;
            case 74 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:394: MOD
            {
                mMOD();

            }
                break;
            case 75 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:398: MOD_ASSIGN
            {
                mMOD_ASSIGN();

            }
                break;
            case 76 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:409: SR
            {
                mSR();

            }
                break;
            case 77 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:412: SR_ASSIGN
            {
                mSR_ASSIGN();

            }
                break;
            case 78 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:422: BSR
            {
                mBSR();

            }
                break;
            case 79 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:426: BSR_ASSIGN
            {
                mBSR_ASSIGN();

            }
                break;
            case 80 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:437: GE
            {
                mGE();

            }
                break;
            case 81 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:440: GT
            {
                mGT();

            }
                break;
            case 82 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:443: SL
            {
                mSL();

            }
                break;
            case 83 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:446: SL_ASSIGN
            {
                mSL_ASSIGN();

            }
                break;
            case 84 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:456: LE
            {
                mLE();

            }
                break;
            case 85 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:459: LT
            {
                mLT();

            }
                break;
            case 86 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:462: BXOR
            {
                mBXOR();

            }
                break;
            case 87 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:467: BXOR_ASSIGN
            {
                mBXOR_ASSIGN();

            }
                break;
            case 88 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:479: BOR
            {
                mBOR();

            }
                break;
            case 89 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:483: BOR_ASSIGN
            {
                mBOR_ASSIGN();

            }
                break;
            case 90 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:494: LOR
            {
                mLOR();

            }
                break;
            case 91 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:498: BAND
            {
                mBAND();

            }
                break;
            case 92 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:503: BAND_ASSIGN
            {
                mBAND_ASSIGN();

            }
                break;
            case 93 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:515: LAND
            {
                mLAND();

            }
                break;
            case 94 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:520: SEMI
            {
                mSEMI();

            }
                break;
            case 95 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:525: WS
            {
                mWS();

            }
                break;
            case 96 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:528: SL_COMMENT
            {
                mSL_COMMENT();

            }
                break;
            case 97 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:539: ML_COMMENT
            {
                mML_COMMENT();

            }
                break;
            case 98 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:550: IDENT
            {
                mIDENT();

            }
                break;
            case 99 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:556: NUM_INT
            {
                mNUM_INT();

            }
                break;
            case 100 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:564: NUM_FLOAT
            {
                mNUM_FLOAT();

            }
                break;
            case 101 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:574: CHAR_LITERAL
            {
                mCHAR_LITERAL();

            }
                break;
            case 102 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:1:587: STRING_LITERAL
            {
                mSTRING_LITERAL();

            }
                break;

        }

    }

    protected DFA19              dfa19            = new DFA19( this );
    protected DFA25              dfa25            = new DFA25( this );
    public static final String   DFA19_eotS       = "\7\uffff\1\10\2\uffff";
    public static final String   DFA19_eofS       = "\12\uffff";
    public static final String   DFA19_minS       = "\2\56\2\uffff\1\53\1\uffff\2\60\2\uffff";
    public static final String   DFA19_maxS       = "\1\71\1\146\2\uffff\1\71\1\uffff\1\71\1\146\2\uffff";
    public static final String   DFA19_acceptS    = "\2\uffff\1\2\1\5\1\uffff\1\1\2\uffff\1\4\1\3";
    public static final String   DFA19_specialS   = "\12\uffff}>";
    public static final String[] DFA19_transition = {"\1\2\1\uffff\12\1", "\1\5\1\uffff\12\1\12\uffff\1\3\1\4\1\3\35\uffff\1\3\1\4\1\3", "", "", "\1\6\1\uffff\1\6\2\uffff\12\7", "", "\12\7",
            "\12\7\12\uffff\1\11\1\uffff\1\11\35\uffff\1\11\1\uffff\1\11", "", ""};

    class DFA19 extends DFA {
        public DFA19(final BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 19;
            this.eot = DFA.unpackEncodedString( DFA19_eotS );
            this.eof = DFA.unpackEncodedString( DFA19_eofS );
            this.min = DFA.unpackEncodedStringToUnsignedChars( DFA19_minS );
            this.max = DFA.unpackEncodedStringToUnsignedChars( DFA19_maxS );
            this.accept = DFA.unpackEncodedString( DFA19_acceptS );
            this.special = DFA.unpackEncodedString( DFA19_specialS );
            final int numStates = DFA19_transition.length;
            this.transition = new short[numStates][];
            for ( int i = 0; i < numStates; i++ ) {
                this.transition[i] = DFA.unpackEncodedString( DFA19_transition[i] );
            }
        }

        public String getDescription() {
            return "1104:1: NUM_FLOAT : ( DIGITS '.' ( DIGITS )? ( EXPONENT_PART )? ( FLOAT_TYPE_SUFFIX )? | '.' DIGITS ( EXPONENT_PART )? ( FLOAT_TYPE_SUFFIX )? | DIGITS EXPONENT_PART FLOAT_TYPE_SUFFIX | DIGITS EXPONENT_PART | DIGITS FLOAT_TYPE_SUFFIX );";
        }
    }

    public static final String   DFA25_eotS       = "\1\uffff\17\51\11\uffff\1\122\1\124\1\126\1\uffff\1\132\1\135\1" + "\140\1\142\1\144\1\147\1\152\1\154\1\157\1\162\3\uffff\2\163\2\uffff"
                                                    + "\17\51\1\u0089\5\51\1\u0090\15\51\25\uffff\1\u00a4\1\uffff\1\u00a6" + "\13\uffff\2\163\21\51\1\u00b9\1\51\1\uffff\1\u00bb\5\51\1\uffff"
                                                    + "\4\51\1\u00c5\7\51\1\u00ce\4\51\1\u00d4\4\uffff\1\u00d5\3\51\1\u00d9" + "\1\u00da\1\51\1\u00dc\12\51\1\uffff\1\51\1\uffff\3\51\1\u00eb\5"
                                                    + "\51\1\uffff\1\51\1\u00f2\2\51\1\u00f5\1\51\1\u00f7\1\51\1\uffff" + "\1\u00f9\3\51\3\uffff\2\51\1\u00ff\2\uffff\1\u0100\1\uffff\1\u0101"
                                                    + "\1\51\1\u0103\4\51\1\u0108\3\51\1\u010c\1\u010e\1\u010f\1\uffff" + "\6\51\1\uffff\1\51\1\u0118\1\uffff\1\51\1\uffff\1\51\1\uffff\1\51"
                                                    + "\1\u011c\3\51\3\uffff\1\51\1\uffff\2\51\1\u0123\1\u0124\1\uffff" + "\3\51\1\uffff\1\51\2\uffff\1\u0129\1\51\1\u012b\4\51\1\u0130\1\uffff"
                                                    + "\1\51\1\u0132\1\51\1\uffff\1\u0134\1\51\1\u0136\3\51\2\uffff\3\51" + "\1\u013d\1\uffff\1\u013e\1\uffff\1\51\1\u0140\2\51\1\uffff\1\51"
                                                    + "\1\uffff\1\u0144\1\uffff\1\u0145\1\uffff\1\u0146\1\51\1\u0148\3" + "\51\2\uffff\1\51\1\uffff\2\51\1\u014f\3\uffff\1\51\1\uffff\1\51"
                                                    + "\1\u0152\1\51\1\u0154\1\u0155\1\51\1\uffff\1\51\1\u0158\1\uffff" + "\1\u0159\2\uffff\1\u015a\1\51\3\uffff\1\u015c\1\uffff";
    public static final String   DFA25_eofS       = "\u015d\uffff";
    public static final String   DFA25_minS       = "\1\11\2\157\1\141\1\150\1\146\1\141\1\157\1\145\1\162\1\150\1\142" + "\1\141\1\154\1\150\1\145\11\uffff\1\60\2\75\1\uffff\1\52\1\53\1"
                                                    + "\55\3\75\1\74\2\75\1\46\3\uffff\2\56\2\uffff\1\151\1\157\1\145\1" + "\164\1\163\2\141\1\156\1\160\1\156\1\141\1\151\1\157\1\160\1\163"
                                                    + "\1\44\1\162\1\154\1\156\1\157\1\156\1\44\1\146\1\142\1\151\1\141" + "\1\151\1\163\1\154\1\164\1\167\1\163\1\164\1\151\1\164\25\uffff"
                                                    + "\1\75\1\uffff\1\75\13\uffff\2\56\1\144\1\141\1\154\1\141\2\145\1" + "\143\1\162\1\163\1\164\1\145\1\143\1\151\2\164\1\162\1\154\1\44"
                                                    + "\1\164\1\uffff\1\44\1\163\2\141\1\147\1\142\1\uffff\1\141\1\154" + "\1\164\1\166\1\44\1\156\2\145\1\163\1\164\1\154\1\151\1\44\2\145"
                                                    + "\1\154\1\165\1\75\4\uffff\1\44\1\164\1\145\1\153\2\44\1\150\1\44" + "\1\163\1\151\1\162\1\150\1\143\1\151\1\143\1\164\1\145\1\162\1\uffff"
                                                    + "\1\141\1\uffff\1\145\1\154\1\164\1\44\1\154\1\165\1\151\1\145\1" + "\141\1\uffff\1\163\1\44\1\141\1\167\1\44\1\162\1\44\1\166\1\uffff"
                                                    + "\1\44\1\156\1\145\1\162\3\uffff\1\151\1\141\1\44\2\uffff\1\44\1" + "\uffff\1\44\1\156\1\44\1\162\1\164\1\143\1\150\1\44\1\155\1\146"
                                                    + "\1\156\3\44\1\uffff\1\145\1\154\2\143\1\164\1\151\1\uffff\1\144" + "\1\44\1\uffff\1\141\1\uffff\1\145\1\uffff\1\144\1\44\1\156\1\154"
                                                    + "\1\156\3\uffff\1\165\1\uffff\1\157\1\146\2\44\1\uffff\1\145\1\141" + "\1\143\1\uffff\1\171\2\uffff\1\44\1\164\1\44\1\164\2\145\1\163\1"
                                                    + "\44\1\uffff\1\143\1\44\1\163\1\uffff\1\44\1\145\1\44\1\145\1\156" + "\1\160\2\uffff\1\156\1\143\1\145\1\44\1\uffff\1\44\1\uffff\1\145"
                                                    + "\1\44\1\156\1\141\1\uffff\1\164\1\uffff\1\44\1\uffff\1\44\1\uffff" + "\1\44\1\151\1\44\1\164\1\145\1\157\2\uffff\1\144\1\uffff\1\164\1"
                                                    + "\146\1\44\3\uffff\1\172\1\uffff\1\163\1\44\1\146\2\44\1\145\1\uffff" + "\1\145\1\44\1\uffff\1\44\2\uffff\1\44\1\144\3\uffff\1\44\1\uffff";
    public static final String   DFA25_maxS       = "\1\176\1\157\1\171\1\157\1\171\1\156\3\157\1\165\1\162\1\142\1\165" + "\1\170\1\150\1\145\11\uffff\1\71\2\75\1\uffff\5\75\1\76\2\75\1\174"
                                                    + "\1\75\3\uffff\2\146\2\uffff\1\154\1\157\1\145\2\164\2\141\1\156" + "\1\160\1\156\1\162\1\151\1\157\1\160\1\164\1\172\1\162\1\154\1\156"
                                                    + "\1\157\1\156\1\172\1\146\1\142\1\157\1\171\1\162\1\163\1\154\1\164" + "\1\167\1\163\1\164\1\151\1\164\25\uffff\1\76\1\uffff\1\75\13\uffff"
                                                    + "\2\146\1\144\1\141\1\154\1\141\2\145\1\143\1\162\1\163\1\164\1\145" + "\1\143\1\151\2\164\1\162\1\154\1\172\1\164\1\uffff\1\172\1\163\2"
                                                    + "\141\1\147\1\142\1\uffff\1\141\1\154\1\164\1\166\1\172\1\156\1\145" + "\1\157\1\163\1\164\1\154\1\151\1\172\2\145\1\154\1\165\1\75\4\uffff"
                                                    + "\1\172\1\164\1\145\1\153\2\172\1\150\1\172\1\163\1\151\1\162\1\150" + "\1\143\1\151\1\143\1\164\1\145\1\162\1\uffff\1\141\1\uffff\1\145"
                                                    + "\1\154\1\164\1\172\1\154\1\165\1\151\1\145\1\141\1\uffff\1\163\1" + "\172\1\141\1\167\1\172\1\162\1\172\1\166\1\uffff\1\172\1\156\1\145"
                                                    + "\1\162\3\uffff\1\151\1\141\1\172\2\uffff\1\172\1\uffff\1\172\1\156" + "\1\172\1\162\1\164\1\143\1\150\1\172\1\155\1\146\1\156\3\172\1\uffff"
                                                    + "\1\145\1\154\2\143\1\164\1\151\1\uffff\1\144\1\172\1\uffff\1\141" + "\1\uffff\1\145\1\uffff\1\144\1\172\1\156\1\154\1\156\3\uffff\1\165"
                                                    + "\1\uffff\1\157\1\146\2\172\1\uffff\1\145\1\141\1\143\1\uffff\1\171" + "\2\uffff\1\172\1\164\1\172\1\164\2\145\1\163\1\172\1\uffff\1\143"
                                                    + "\1\172\1\163\1\uffff\1\172\1\145\1\172\1\145\1\156\1\160\2\uffff" + "\1\156\1\143\1\145\1\172\1\uffff\1\172\1\uffff\1\145\1\172\1\156"
                                                    + "\1\141\1\uffff\1\164\1\uffff\1\172\1\uffff\1\172\1\uffff\1\172\1" + "\151\1\172\1\164\1\145\1\157\2\uffff\1\144\1\uffff\1\164\1\146\1"
                                                    + "\172\3\uffff\1\172\1\uffff\1\163\1\172\1\146\2\172\1\145\1\uffff" + "\1\145\1\172\1\uffff\1\172\2\uffff\1\172\1\144\3\uffff\1\172\1\uffff";
    public static final String   DFA25_acceptS    = "\20\uffff\1\61\1\62\1\63\1\64\1\65\1\66\1\67\1\70\1\71\3\uffff\1" + "\76\12\uffff\1\136\1\137\1\142\2\uffff\1\145\1\146\43\uffff\1\144"
                                                    + "\1\72\1\74\1\73\1\77\1\75\1\140\1\141\1\101\1\100\1\103\1\104\1" + "\102\1\107\1\106\1\105\1\111\1\110\1\113\1\112\1\120\1\uffff\1\121"
                                                    + "\1\uffff\1\124\1\125\1\127\1\126\1\132\1\131\1\130\1\135\1\134\1" + "\133\1\143\25\uffff\1\35\6\uffff\1\41\22\uffff\1\115\1\114\1\123"
                                                    + "\1\122\22\uffff\1\6\1\uffff\1\37\11\uffff\1\51\10\uffff\1\60\4\uffff" + "\1\117\1\116\1\1\3\uffff\1\3\1\47\1\uffff\1\4\16\uffff\1\10\6\uffff"
                                                    + "\1\55\2\uffff\1\32\1\uffff\1\57\1\uffff\1\36\5\uffff\1\42\1\53\1" + "\26\1\uffff\1\33\4\uffff\1\5\3\uffff\1\56\1\uffff\1\17\1\7\10\uffff"
                                                    + "\1\46\3\uffff\1\40\6\uffff\1\15\1\45\4\uffff\1\11\1\uffff\1\13\4" + "\uffff\1\34\1\uffff\1\21\1\uffff\1\44\1\uffff\1\2\6\uffff\1\52\1"
                                                    + "\50\1\uffff\1\12\3\uffff\1\27\1\24\1\43\1\uffff\1\25\6\uffff\1\20" + "\2\uffff\1\30\1\uffff\1\14\1\16\2\uffff\1\31\1\54\1\22\1\uffff\1" + "\23";
    public static final String   DFA25_specialS   = "\u015d\uffff}>";
    public static final String[] DFA25_transition = {
            "\2\50\1\uffff\2\50\22\uffff\1\50\1\33\1\55\1\uffff\1\51\1\41\1\46" + "\1\54\1\21\1\22\1\40\1\36\1\30\1\37\1\31\1\35\1\53\11\52\1\27\1" + "\47\1\43\1\32\1\42\1\20\1\uffff\32\51\1\23\1\uffff\1\24\1\44\1\51"
                    + "\1\uffff\1\13\1\2\1\3\1\10\1\15\1\6\2\51\1\5\2\51\1\7\1\51\1\14" + "\1\51\1\11\1\51\1\17\1\4\1\12\1\51\1\1\1\16\3\51\1\25\1\45\1\26" + "\1\34", "\1\56", "\1\57\2\uffff\1\60\6\uffff\1\61",
            "\1\62\6\uffff\1\63\3\uffff\1\64\2\uffff\1\65", "\1\72\13\uffff\1\70\1\66\1\uffff\1\71\1\uffff\1\67", "\1\75\6\uffff\1\73\1\74", "\1\77\7\uffff\1\100\2\uffff\1\101\2\uffff\1\76", "\1\102", "\1\104\11\uffff\1\103", "\1\106\2\uffff\1\105",
            "\1\110\11\uffff\1\107", "\1\111", "\1\113\3\uffff\1\114\17\uffff\1\112", "\1\115\13\uffff\1\116", "\1\117", "\1\120", "", "", "", "", "", "", "", "", "", "\12\121", "\1\123", "\1\125", "", "\1\130\4\uffff\1\127\15\uffff\1\131",
            "\1\134\21\uffff\1\133", "\1\136\17\uffff\1\137", "\1\141", "\1\143", "\1\145\1\146", "\1\150\1\151", "\1\153", "\1\156\76\uffff\1\155", "\1\160\26\uffff\1\161", "", "", "", "\1\121\1\uffff\12\164\12\uffff\3\121\35\uffff\3\121",
            "\1\121\1\uffff\10\165\2\121\12\uffff\3\121\35\uffff\3\121", "", "", "\1\166\2\uffff\1\167", "\1\170", "\1\171", "\1\172", "\1\173\1\174", "\1\175", "\1\176", "\1\177", "\1\u0080", "\1\u0081", "\1\u0083\20\uffff\1\u0082", "\1\u0084",
            "\1\u0085", "\1\u0086", "\1\u0088\1\u0087", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u008a", "\1\u008b", "\1\u008c", "\1\u008d", "\1\u008e",
            "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\24\51\1" + "\u008f\5\51", "\1\u0091", "\1\u0092", "\1\u0094\5\uffff\1\u0093", "\1\u0096\23\uffff\1\u0097\3\uffff\1\u0095", "\1\u0099\10\uffff\1\u0098", "\1\u009a", "\1\u009b",
            "\1\u009c", "\1\u009d", "\1\u009e", "\1\u009f", "\1\u00a0", "\1\u00a1", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "\1\u00a3\1\u00a2", "", "\1\u00a5", "", "", "", "", "", "", "", "", "", "", "",
            "\1\121\1\uffff\12\164\12\uffff\3\121\35\uffff\3\121", "\1\121\1\uffff\10\165\2\121\12\uffff\3\121\35\uffff\3\121", "\1\u00a7", "\1\u00a8", "\1\u00a9", "\1\u00aa", "\1\u00ab", "\1\u00ac", "\1\u00ad", "\1\u00ae", "\1\u00af", "\1\u00b0",
            "\1\u00b1", "\1\u00b2", "\1\u00b3", "\1\u00b4", "\1\u00b5", "\1\u00b6", "\1\u00b7", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\4\51\1\u00b8" + "\25\51", "\1\u00ba", "",
            "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u00bc", "\1\u00bd", "\1\u00be", "\1\u00bf", "\1\u00c0", "", "\1\u00c1", "\1\u00c2", "\1\u00c3", "\1\u00c4",
            "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u00c6", "\1\u00c7", "\1\u00c8\11\uffff\1\u00c9", "\1\u00ca", "\1\u00cb", "\1\u00cc", "\1\u00cd", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u00cf", "\1\u00d0", "\1\u00d1", "\1\u00d2", "\1\u00d3", "", "", "", "", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u00d6", "\1\u00d7", "\1\u00d8",
            "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u00db", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u00dd", "\1\u00de",
            "\1\u00df", "\1\u00e0", "\1\u00e1", "\1\u00e2", "\1\u00e3", "\1\u00e4", "\1\u00e5", "\1\u00e6", "", "\1\u00e7", "", "\1\u00e8", "\1\u00e9", "\1\u00ea", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u00ec",
            "\1\u00ed", "\1\u00ee", "\1\u00ef", "\1\u00f0", "", "\1\u00f1", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u00f3", "\1\u00f4", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u00f6",
            "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u00f8", "", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u00fa", "\1\u00fb", "\1\u00fc", "", "", "", "\1\u00fd", "\1\u00fe",
            "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "", "", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u0102",
            "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u0104", "\1\u0105", "\1\u0106", "\1\u0107", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u0109", "\1\u010a", "\1\u010b",
            "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\13\51\1" + "\u010d\16\51", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "", "\1\u0110",
            "\1\u0111", "\1\u0112", "\1\u0113", "\1\u0114", "\1\u0115", "", "\1\u0116", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\22\51\1" + "\u0117\7\51", "", "\1\u0119", "", "\1\u011a", "", "\1\u011b",
            "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u011d", "\1\u011e", "\1\u011f", "", "", "", "\1\u0120", "", "\1\u0121", "\1\u0122", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "", "\1\u0125", "\1\u0126", "\1\u0127", "", "\1\u0128", "", "", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u012a",
            "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u012c", "\1\u012d", "\1\u012e", "\1\u012f", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "", "\1\u0131",
            "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u0133", "", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u0135", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u0137",
            "\1\u0138", "\1\u0139", "", "", "\1\u013a", "\1\u013b", "\1\u013c", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "", "\1\u013f",
            "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u0141", "\1\u0142", "", "\1\u0143", "", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "",
            "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u0147", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u0149", "\1\u014a",
            "\1\u014b", "", "", "\1\u014c", "", "\1\u014d", "\1\u014e", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "", "", "", "\1\u0150", "", "\1\u0151", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0153", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u0156", "", "\1\u0157", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "", "", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", "\1\u015b", "", "", "", "\1\51\13\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51", ""};

    class DFA25 extends DFA {
        public DFA25(final BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 25;
            this.eot = DFA.unpackEncodedString( DFA25_eotS );
            this.eof = DFA.unpackEncodedString( DFA25_eofS );
            this.min = DFA.unpackEncodedStringToUnsignedChars( DFA25_minS );
            this.max = DFA.unpackEncodedStringToUnsignedChars( DFA25_maxS );
            this.accept = DFA.unpackEncodedString( DFA25_acceptS );
            this.special = DFA.unpackEncodedString( DFA25_specialS );
            final int numStates = DFA25_transition.length;
            this.transition = new short[numStates][];
            for ( int i = 0; i < numStates; i++ ) {
                this.transition[i] = DFA.unpackEncodedString( DFA25_transition[i] );
            }
        }

        public String getDescription() {
            return "1:1: Tokens : ( T68 | T69 | T70 | T71 | T72 | T73 | T74 | T75 | T76 | T77 | T78 | T79 | T80 | T81 | T82 | T83 | T84 | T85 | T86 | T87 | T88 | T89 | T90 | T91 | T92 | T93 | T94 | T95 | T96 | T97 | T98 | T99 | T100 | T101 | T102 | T103 | T104 | T105 | T106 | T107 | T108 | T109 | T110 | T111 | T112 | T113 | T114 | T115 | QUESTION | LPAREN | RPAREN | LBRACK | RBRACK | LCURLY | RCURLY | COLON | COMMA | DOT | ASSIGN | EQUAL | LNOT | BNOT | NOT_EQUAL | DIV | DIV_ASSIGN | PLUS | PLUS_ASSIGN | INC | MINUS | MINUS_ASSIGN | DEC | STAR | STAR_ASSIGN | MOD | MOD_ASSIGN | SR | SR_ASSIGN | BSR | BSR_ASSIGN | GE | GT | SL | SL_ASSIGN | LE | LT | BXOR | BXOR_ASSIGN | BOR | BOR_ASSIGN | LOR | BAND | BAND_ASSIGN | LAND | SEMI | WS | SL_COMMENT | ML_COMMENT | IDENT | NUM_INT | NUM_FLOAT | CHAR_LITERAL | STRING_LITERAL );";
        }
    }

}