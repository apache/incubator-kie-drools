// $ANTLR 3.3 Nov 30, 2010 12:45:30 /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g 2011-01-19 15:00:17

package org.drools.lang;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.FailedPredicateException;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.drools.compiler.DroolsParserException;

public class DRLExpressions extends Parser {
    public static final String[] tokenNames                   = new String[]{
                                                              "<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "WS", "Exponent", "FloatTypeSuffix", "FLOAT", "HexDigit", "IntegerTypeSuffix", "HEX", "DECIMAL", "EscapeSequence", "STRING", "TimePeriod",
                                                              "UnicodeEscape", "OctalEscape", "BOOL", "NULL", "AT", "PLUS_ASSIGN", "MINUS_ASSIGN", "MULT_ASSIGN", "DIV_ASSIGN", "AND_ASSIGN", "OR_ASSIGN", "XOR_ASSIGN", "MOD_ASSIGN", "DECR", "INCR",
                                                              "ARROW", "SEMICOLON", "COLON", "EQUALS",
                                                              "NOT_EQUALS", "GREATER_EQUALS", "LESS_EQUALS", "GREATER", "LESS", "EQUALS_ASSIGN", "LEFT_PAREN", "RIGHT_PAREN", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "COMMA", "DOT",
                                                              "DOUBLE_AMPER", "DOUBLE_PIPE", "QUESTION",
                                                              "NEGATION", "TILDE", "PIPE", "AMPER", "XOR", "MOD", "STAR", "MINUS", "PLUS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "IdentifierStart",
                                                              "IdentifierPart", "ID", "DIV", "MISC"
                                                              };
    public static final int      EOF                          = -1;
    public static final int      EOL                          = 4;
    public static final int      WS                           = 5;
    public static final int      Exponent                     = 6;
    public static final int      FloatTypeSuffix              = 7;
    public static final int      FLOAT                        = 8;
    public static final int      HexDigit                     = 9;
    public static final int      IntegerTypeSuffix            = 10;
    public static final int      HEX                          = 11;
    public static final int      DECIMAL                      = 12;
    public static final int      EscapeSequence               = 13;
    public static final int      STRING                       = 14;
    public static final int      TimePeriod                   = 15;
    public static final int      UnicodeEscape                = 16;
    public static final int      OctalEscape                  = 17;
    public static final int      BOOL                         = 18;
    public static final int      NULL                         = 19;
    public static final int      AT                           = 20;
    public static final int      PLUS_ASSIGN                  = 21;
    public static final int      MINUS_ASSIGN                 = 22;
    public static final int      MULT_ASSIGN                  = 23;
    public static final int      DIV_ASSIGN                   = 24;
    public static final int      AND_ASSIGN                   = 25;
    public static final int      OR_ASSIGN                    = 26;
    public static final int      XOR_ASSIGN                   = 27;
    public static final int      MOD_ASSIGN                   = 28;
    public static final int      DECR                         = 29;
    public static final int      INCR                         = 30;
    public static final int      ARROW                        = 31;
    public static final int      SEMICOLON                    = 32;
    public static final int      COLON                        = 33;
    public static final int      EQUALS                       = 34;
    public static final int      NOT_EQUALS                   = 35;
    public static final int      GREATER_EQUALS               = 36;
    public static final int      LESS_EQUALS                  = 37;
    public static final int      GREATER                      = 38;
    public static final int      LESS                         = 39;
    public static final int      EQUALS_ASSIGN                = 40;
    public static final int      LEFT_PAREN                   = 41;
    public static final int      RIGHT_PAREN                  = 42;
    public static final int      LEFT_SQUARE                  = 43;
    public static final int      RIGHT_SQUARE                 = 44;
    public static final int      LEFT_CURLY                   = 45;
    public static final int      RIGHT_CURLY                  = 46;
    public static final int      COMMA                        = 47;
    public static final int      DOT                          = 48;
    public static final int      DOUBLE_AMPER                 = 49;
    public static final int      DOUBLE_PIPE                  = 50;
    public static final int      QUESTION                     = 51;
    public static final int      NEGATION                     = 52;
    public static final int      TILDE                        = 53;
    public static final int      PIPE                         = 54;
    public static final int      AMPER                        = 55;
    public static final int      XOR                          = 56;
    public static final int      MOD                          = 57;
    public static final int      STAR                         = 58;
    public static final int      MINUS                        = 59;
    public static final int      PLUS                         = 60;
    public static final int      SH_STYLE_SINGLE_LINE_COMMENT = 61;
    public static final int      C_STYLE_SINGLE_LINE_COMMENT  = 62;
    public static final int      MULTI_LINE_COMMENT           = 63;
    public static final int      IdentifierStart              = 64;
    public static final int      IdentifierPart               = 65;
    public static final int      ID                           = 66;
    public static final int      DIV                          = 67;
    public static final int      MISC                         = 68;

    // delegates
    // delegators

    public DRLExpressions(TokenStream input) {
        this( input,
              new RecognizerSharedState() );
    }

    public DRLExpressions(TokenStream input,
                          RecognizerSharedState state) {
        super( input,
               state );
        this.state.ruleMemo = new HashMap[113 + 1];

    }

    public String[] getTokenNames() {
        return DRLExpressions.tokenNames;
    }

    public String getGrammarFileName() {
        return "/home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g";
    }

    private ParserXHelper helper = new ParserXHelper( tokenNames,
                                                        input,
                                                        state );

    public ParserXHelper getHelper() {
        return helper;
    }

    public boolean hasErrors() {
        return helper.hasErrors();
    }

    public List<DroolsParserException> getErrors() {
        return helper.getErrors();
    }

    public List<String> getErrorMessages() {
        return helper.getErrorMessages();
    }

    public void enableEditorInterface() {
        helper.enableEditorInterface();
    }

    public void disableEditorInterface() {
        helper.disableEditorInterface();
    }

    public LinkedList<DroolsSentence> getEditorInterface() {
        return helper.getEditorInterface();
    }

    public void reportError( RecognitionException ex ) {
        helper.reportError( ex );
    }

    public void emitErrorMessage( String msg ) {
    }

    // $ANTLR start "literal"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:38:1: literal : ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL );
    public final void literal() throws RecognitionException {
        Token STRING1 = null;
        Token DECIMAL2 = null;
        Token HEX3 = null;
        Token FLOAT4 = null;
        Token BOOL5 = null;
        Token NULL6 = null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:39:2: ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL )
            int alt1 = 6;
            switch ( input.LA( 1 ) ) {
                case STRING : {
                    alt1 = 1;
                }
                    break;
                case DECIMAL : {
                    alt1 = 2;
                }
                    break;
                case HEX : {
                    alt1 = 3;
                }
                    break;
                case FLOAT : {
                    alt1 = 4;
                }
                    break;
                case BOOL : {
                    alt1 = 5;
                }
                    break;
                case NULL : {
                    alt1 = 6;
                }
                    break;
                default :
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    NoViableAltException nvae =
                            new NoViableAltException( "",
                                                      1,
                                                      0,
                                                      input );

                    throw nvae;
            }

            switch ( alt1 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:39:4: STRING
                {
                    STRING1 = (Token) match( input,
                                             STRING,
                                             FOLLOW_STRING_in_literal66 );
                    if ( state.failed ) return;
                    if ( state.backtracking == 0 ) {
                        helper.emit( STRING1,
                                     DroolsEditorType.STRING_CONST );
                    }

                }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:40:4: DECIMAL
                {
                    DECIMAL2 = (Token) match( input,
                                              DECIMAL,
                                              FOLLOW_DECIMAL_in_literal89 );
                    if ( state.failed ) return;
                    if ( state.backtracking == 0 ) {
                        helper.emit( DECIMAL2,
                                     DroolsEditorType.NUMERIC_CONST );
                    }

                }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:41:4: HEX
                {
                    HEX3 = (Token) match( input,
                                          HEX,
                                          FOLLOW_HEX_in_literal98 );
                    if ( state.failed ) return;
                    if ( state.backtracking == 0 ) {
                        helper.emit( HEX3,
                                     DroolsEditorType.NUMERIC_CONST );
                    }

                }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:42:4: FLOAT
                {
                    FLOAT4 = (Token) match( input,
                                            FLOAT,
                                            FOLLOW_FLOAT_in_literal111 );
                    if ( state.failed ) return;
                    if ( state.backtracking == 0 ) {
                        helper.emit( FLOAT4,
                                     DroolsEditorType.NUMERIC_CONST );
                    }

                }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:43:4: BOOL
                {
                    BOOL5 = (Token) match( input,
                                           BOOL,
                                           FOLLOW_BOOL_in_literal122 );
                    if ( state.failed ) return;
                    if ( state.backtracking == 0 ) {
                        helper.emit( BOOL5,
                                     DroolsEditorType.BOOLEAN_CONST );
                    }

                }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:44:4: NULL
                {
                    NULL6 = (Token) match( input,
                                           NULL,
                                           FOLLOW_NULL_in_literal147 );
                    if ( state.failed ) return;
                    if ( state.backtracking == 0 ) {
                        helper.emit( NULL6,
                                     DroolsEditorType.NULL_CONST );
                    }

                }
                    break;

            }
        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "literal"

    // $ANTLR start "typeList"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:47:1: typeList : type ( COMMA type )* ;
    public final void typeList() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:48:2: ( type ( COMMA type )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:48:4: type ( COMMA type )*
            {
                pushFollow( FOLLOW_type_in_typeList178 );
                type();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:48:9: ( COMMA type )*
                loop2 : do {
                    int alt2 = 2;
                    int LA2_0 = input.LA( 1 );

                    if ( (LA2_0 == COMMA) ) {
                        alt2 = 1;
                    }

                    switch ( alt2 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:48:10: COMMA type
                        {
                            match( input,
                                   COMMA,
                                   FOLLOW_COMMA_in_typeList181 );
                            if ( state.failed ) return;
                            pushFollow( FOLLOW_type_in_typeList183 );
                            type();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                        default :
                            break loop2;
                    }
                } while ( true );

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "typeList"

    // $ANTLR start "type"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:51:1: type options {backtrack=true; memoize=true; } : ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) );
    public final void type() throws RecognitionException {
        int type_StartIndex = input.index();
        try {
            if ( state.backtracking > 0 && alreadyParsedRule( input,
                                                              3 ) ) {
                return;
            }
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:53:2: ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
            int alt8 = 2;
            int LA8_0 = input.LA( 1 );

            if ( (LA8_0 == ID) ) {
                int LA8_1 = input.LA( 2 );

                if ( (((synpred1_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.INT )))) || (synpred1_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE ))))
                       || (synpred1_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT )))) || (synpred1_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF ))))
                       || (synpred1_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG )))) || (synpred1_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))))
                       || (synpred1_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT )))) || (synpred1_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR )))))) ) {
                    alt8 = 1;
                } else if ( (true) ) {
                    alt8 = 2;
                } else {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    NoViableAltException nvae =
                            new NoViableAltException( "",
                                                      8,
                                                      1,
                                                      input );

                    throw nvae;
                }
            } else {
                if ( state.backtracking > 0 ) {
                    state.failed = true;
                    return;
                }
                NoViableAltException nvae =
                        new NoViableAltException( "",
                                                  8,
                                                  0,
                                                  input );

                throw nvae;
            }
            switch ( alt8 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:53:5: ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                {
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:53:24: ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:53:26: primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                        pushFollow( FOLLOW_primitiveType_in_type220 );
                        primitiveType();

                        state._fsp--;
                        if ( state.failed ) return;
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:53:40: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                        loop3 : do {
                            int alt3 = 2;
                            int LA3_0 = input.LA( 1 );

                            if ( (LA3_0 == LEFT_SQUARE) ) {
                                int LA3_2 = input.LA( 2 );

                                if ( (LA3_2 == RIGHT_SQUARE) ) {
                                    int LA3_3 = input.LA( 3 );

                                    if ( (synpred2_DRLExpressions()) ) {
                                        alt3 = 1;
                                    }

                                }

                            }

                            switch ( alt3 ) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:53:41: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                                {
                                    match( input,
                                           LEFT_SQUARE,
                                           FOLLOW_LEFT_SQUARE_in_type230 );
                                    if ( state.failed ) return;
                                    match( input,
                                           RIGHT_SQUARE,
                                           FOLLOW_RIGHT_SQUARE_in_type232 );
                                    if ( state.failed ) return;

                                }
                                    break;

                                default :
                                    break loop3;
                            }
                        } while ( true );

                    }

                }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:54:4: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                {
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:54:4: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:54:6: ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                        match( input,
                               ID,
                               FOLLOW_ID_in_type243 );
                        if ( state.failed ) return;
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:54:9: ( ( typeArguments )=> typeArguments )?
                        int alt4 = 2;
                        alt4 = dfa4.predict( input );
                        switch ( alt4 ) {
                            case 1 :
                                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:54:10: ( typeArguments )=> typeArguments
                            {
                                pushFollow( FOLLOW_typeArguments_in_type250 );
                                typeArguments();

                                state._fsp--;
                                if ( state.failed ) return;

                            }
                                break;

                        }

                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:54:43: ( DOT ID ( ( typeArguments )=> typeArguments )? )*
                        loop6 : do {
                            int alt6 = 2;
                            int LA6_0 = input.LA( 1 );

                            if ( (LA6_0 == DOT) ) {
                                alt6 = 1;
                            }

                            switch ( alt6 ) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:54:44: DOT ID ( ( typeArguments )=> typeArguments )?
                                {
                                    match( input,
                                           DOT,
                                           FOLLOW_DOT_in_type255 );
                                    if ( state.failed ) return;
                                    match( input,
                                           ID,
                                           FOLLOW_ID_in_type257 );
                                    if ( state.failed ) return;
                                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:54:51: ( ( typeArguments )=> typeArguments )?
                                    int alt5 = 2;
                                    alt5 = dfa5.predict( input );
                                    switch ( alt5 ) {
                                        case 1 :
                                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:54:52: ( typeArguments )=> typeArguments
                                        {
                                            pushFollow( FOLLOW_typeArguments_in_type264 );
                                            typeArguments();

                                            state._fsp--;
                                            if ( state.failed ) return;

                                        }
                                            break;

                                    }

                                }
                                    break;

                                default :
                                    break loop6;
                            }
                        } while ( true );

                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:54:88: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                        loop7 : do {
                            int alt7 = 2;
                            int LA7_0 = input.LA( 1 );

                            if ( (LA7_0 == LEFT_SQUARE) ) {
                                int LA7_2 = input.LA( 2 );

                                if ( (LA7_2 == RIGHT_SQUARE) ) {
                                    int LA7_3 = input.LA( 3 );

                                    if ( (synpred5_DRLExpressions()) ) {
                                        alt7 = 1;
                                    }

                                }

                            }

                            switch ( alt7 ) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:54:89: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                                {
                                    match( input,
                                           LEFT_SQUARE,
                                           FOLLOW_LEFT_SQUARE_in_type279 );
                                    if ( state.failed ) return;
                                    match( input,
                                           RIGHT_SQUARE,
                                           FOLLOW_RIGHT_SQUARE_in_type281 );
                                    if ( state.failed ) return;

                                }
                                    break;

                                default :
                                    break loop7;
                            }
                        } while ( true );

                    }

                }
                    break;

            }
        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
            if ( state.backtracking > 0 ) {
                memoize( input,
                         3,
                         type_StartIndex );
            }
        }
        return;
    }

    // $ANTLR end "type"

    // $ANTLR start "typeArguments"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:57:1: typeArguments : LESS typeArgument ( COMMA typeArgument )* GREATER ;
    public final void typeArguments() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:58:2: ( LESS typeArgument ( COMMA typeArgument )* GREATER )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:58:4: LESS typeArgument ( COMMA typeArgument )* GREATER
            {
                match( input,
                       LESS,
                       FOLLOW_LESS_in_typeArguments296 );
                if ( state.failed ) return;
                pushFollow( FOLLOW_typeArgument_in_typeArguments298 );
                typeArgument();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:58:22: ( COMMA typeArgument )*
                loop9 : do {
                    int alt9 = 2;
                    int LA9_0 = input.LA( 1 );

                    if ( (LA9_0 == COMMA) ) {
                        alt9 = 1;
                    }

                    switch ( alt9 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:58:23: COMMA typeArgument
                        {
                            match( input,
                                   COMMA,
                                   FOLLOW_COMMA_in_typeArguments301 );
                            if ( state.failed ) return;
                            pushFollow( FOLLOW_typeArgument_in_typeArguments303 );
                            typeArgument();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                        default :
                            break loop9;
                    }
                } while ( true );

                match( input,
                       GREATER,
                       FOLLOW_GREATER_in_typeArguments307 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "typeArguments"

    // $ANTLR start "typeArgument"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:61:1: typeArgument : ( type | QUESTION ( ( extends_key | super_key ) type )? );
    public final void typeArgument() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:62:2: ( type | QUESTION ( ( extends_key | super_key ) type )? )
            int alt12 = 2;
            int LA12_0 = input.LA( 1 );

            if ( (LA12_0 == ID) ) {
                alt12 = 1;
            } else if ( (LA12_0 == QUESTION) ) {
                alt12 = 2;
            } else {
                if ( state.backtracking > 0 ) {
                    state.failed = true;
                    return;
                }
                NoViableAltException nvae =
                        new NoViableAltException( "",
                                                  12,
                                                  0,
                                                  input );

                throw nvae;
            }
            switch ( alt12 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:62:4: type
                {
                    pushFollow( FOLLOW_type_in_typeArgument319 );
                    type();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:63:4: QUESTION ( ( extends_key | super_key ) type )?
                {
                    match( input,
                           QUESTION,
                           FOLLOW_QUESTION_in_typeArgument324 );
                    if ( state.failed ) return;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:63:13: ( ( extends_key | super_key ) type )?
                    int alt11 = 2;
                    int LA11_0 = input.LA( 1 );

                    if ( (LA11_0 == ID) && ((((helper.validateIdentifierKey( DroolsSoftKeywords.SUPER ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.EXTENDS ))))) ) {
                        alt11 = 1;
                    }
                    switch ( alt11 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:63:14: ( extends_key | super_key ) type
                        {
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:63:14: ( extends_key | super_key )
                            int alt10 = 2;
                            int LA10_0 = input.LA( 1 );

                            if ( (LA10_0 == ID) && ((((helper.validateIdentifierKey( DroolsSoftKeywords.SUPER ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.EXTENDS ))))) ) {
                                int LA10_1 = input.LA( 2 );

                                if ( (((helper.validateIdentifierKey( DroolsSoftKeywords.EXTENDS )))) ) {
                                    alt10 = 1;
                                } else if ( (((helper.validateIdentifierKey( DroolsSoftKeywords.SUPER )))) ) {
                                    alt10 = 2;
                                } else {
                                    if ( state.backtracking > 0 ) {
                                        state.failed = true;
                                        return;
                                    }
                                    NoViableAltException nvae =
                                            new NoViableAltException( "",
                                                                      10,
                                                                      1,
                                                                      input );

                                    throw nvae;
                                }
                            } else {
                                if ( state.backtracking > 0 ) {
                                    state.failed = true;
                                    return;
                                }
                                NoViableAltException nvae =
                                        new NoViableAltException( "",
                                                                  10,
                                                                  0,
                                                                  input );

                                throw nvae;
                            }
                            switch ( alt10 ) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:63:15: extends_key
                                {
                                    pushFollow( FOLLOW_extends_key_in_typeArgument328 );
                                    extends_key();

                                    state._fsp--;
                                    if ( state.failed ) return;

                                }
                                    break;
                                case 2 :
                                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:63:29: super_key
                                {
                                    pushFollow( FOLLOW_super_key_in_typeArgument332 );
                                    super_key();

                                    state._fsp--;
                                    if ( state.failed ) return;

                                }
                                    break;

                            }

                            pushFollow( FOLLOW_type_in_typeArgument335 );
                            type();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                    }

                }
                    break;

            }
        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "typeArgument"

    // $ANTLR start "expression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:69:1: expression options {backtrack=true; memoize=true; } : conditionalExpression ( ( assignmentOperator )=> assignmentOperator expression )? ;
    public final void expression() throws RecognitionException {
        int expression_StartIndex = input.index();
        try {
            if ( state.backtracking > 0 && alreadyParsedRule( input,
                                                              6 ) ) {
                return;
            }
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:2: ( conditionalExpression ( ( assignmentOperator )=> assignmentOperator expression )? )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:4: conditionalExpression ( ( assignmentOperator )=> assignmentOperator expression )?
            {
                pushFollow( FOLLOW_conditionalExpression_in_expression365 );
                conditionalExpression();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:26: ( ( assignmentOperator )=> assignmentOperator expression )?
                int alt13 = 2;
                alt13 = dfa13.predict( input );
                switch ( alt13 ) {
                    case 1 :
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:27: ( assignmentOperator )=> assignmentOperator expression
                    {
                        pushFollow( FOLLOW_assignmentOperator_in_expression374 );
                        assignmentOperator();

                        state._fsp--;
                        if ( state.failed ) return;
                        pushFollow( FOLLOW_expression_in_expression376 );
                        expression();

                        state._fsp--;
                        if ( state.failed ) return;

                    }
                        break;

                }

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
            if ( state.backtracking > 0 ) {
                memoize( input,
                         6,
                         expression_StartIndex );
            }
        }
        return;
    }

    // $ANTLR end "expression"

    // $ANTLR start "conditionalExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:74:1: conditionalExpression : conditionalOrExpression ( QUESTION expression COLON expression )? ;
    public final void conditionalExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:75:9: ( conditionalOrExpression ( QUESTION expression COLON expression )? )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:75:17: conditionalOrExpression ( QUESTION expression COLON expression )?
            {
                pushFollow( FOLLOW_conditionalOrExpression_in_conditionalExpression402 );
                conditionalOrExpression();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:75:41: ( QUESTION expression COLON expression )?
                int alt14 = 2;
                int LA14_0 = input.LA( 1 );

                if ( (LA14_0 == QUESTION) ) {
                    alt14 = 1;
                }
                switch ( alt14 ) {
                    case 1 :
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:75:43: QUESTION expression COLON expression
                    {
                        match( input,
                               QUESTION,
                               FOLLOW_QUESTION_in_conditionalExpression406 );
                        if ( state.failed ) return;
                        pushFollow( FOLLOW_expression_in_conditionalExpression408 );
                        expression();

                        state._fsp--;
                        if ( state.failed ) return;
                        match( input,
                               COLON,
                               FOLLOW_COLON_in_conditionalExpression410 );
                        if ( state.failed ) return;
                        pushFollow( FOLLOW_expression_in_conditionalExpression412 );
                        expression();

                        state._fsp--;
                        if ( state.failed ) return;

                    }
                        break;

                }

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "conditionalExpression"

    // $ANTLR start "conditionalOrExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:77:1: conditionalOrExpression : conditionalAndExpression ( DOUBLE_PIPE conditionalAndExpression )* ;
    public final void conditionalOrExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:78:5: ( conditionalAndExpression ( DOUBLE_PIPE conditionalAndExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:78:9: conditionalAndExpression ( DOUBLE_PIPE conditionalAndExpression )*
            {
                pushFollow( FOLLOW_conditionalAndExpression_in_conditionalOrExpression430 );
                conditionalAndExpression();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:78:34: ( DOUBLE_PIPE conditionalAndExpression )*
                loop15 : do {
                    int alt15 = 2;
                    int LA15_0 = input.LA( 1 );

                    if ( (LA15_0 == DOUBLE_PIPE) ) {
                        alt15 = 1;
                    }

                    switch ( alt15 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:78:36: DOUBLE_PIPE conditionalAndExpression
                        {
                            match( input,
                                   DOUBLE_PIPE,
                                   FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression434 );
                            if ( state.failed ) return;
                            pushFollow( FOLLOW_conditionalAndExpression_in_conditionalOrExpression436 );
                            conditionalAndExpression();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                        default :
                            break loop15;
                    }
                } while ( true );

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "conditionalOrExpression"

    // $ANTLR start "conditionalAndExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:81:1: conditionalAndExpression : inclusiveOrExpression ( DOUBLE_AMPER inclusiveOrExpression )* ;
    public final void conditionalAndExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:82:5: ( inclusiveOrExpression ( DOUBLE_AMPER inclusiveOrExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:82:9: inclusiveOrExpression ( DOUBLE_AMPER inclusiveOrExpression )*
            {
                pushFollow( FOLLOW_inclusiveOrExpression_in_conditionalAndExpression455 );
                inclusiveOrExpression();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:82:31: ( DOUBLE_AMPER inclusiveOrExpression )*
                loop16 : do {
                    int alt16 = 2;
                    int LA16_0 = input.LA( 1 );

                    if ( (LA16_0 == DOUBLE_AMPER) ) {
                        alt16 = 1;
                    }

                    switch ( alt16 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:82:33: DOUBLE_AMPER inclusiveOrExpression
                        {
                            match( input,
                                   DOUBLE_AMPER,
                                   FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression459 );
                            if ( state.failed ) return;
                            pushFollow( FOLLOW_inclusiveOrExpression_in_conditionalAndExpression461 );
                            inclusiveOrExpression();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                        default :
                            break loop16;
                    }
                } while ( true );

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "conditionalAndExpression"

    // $ANTLR start "inclusiveOrExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:85:1: inclusiveOrExpression : exclusiveOrExpression ( PIPE exclusiveOrExpression )* ;
    public final void inclusiveOrExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:86:5: ( exclusiveOrExpression ( PIPE exclusiveOrExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:86:9: exclusiveOrExpression ( PIPE exclusiveOrExpression )*
            {
                pushFollow( FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression480 );
                exclusiveOrExpression();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:86:31: ( PIPE exclusiveOrExpression )*
                loop17 : do {
                    int alt17 = 2;
                    int LA17_0 = input.LA( 1 );

                    if ( (LA17_0 == PIPE) ) {
                        alt17 = 1;
                    }

                    switch ( alt17 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:86:33: PIPE exclusiveOrExpression
                        {
                            match( input,
                                   PIPE,
                                   FOLLOW_PIPE_in_inclusiveOrExpression484 );
                            if ( state.failed ) return;
                            pushFollow( FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression486 );
                            exclusiveOrExpression();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                        default :
                            break loop17;
                    }
                } while ( true );

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "inclusiveOrExpression"

    // $ANTLR start "exclusiveOrExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:89:1: exclusiveOrExpression : andExpression ( XOR andExpression )* ;
    public final void exclusiveOrExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:90:5: ( andExpression ( XOR andExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:90:9: andExpression ( XOR andExpression )*
            {
                pushFollow( FOLLOW_andExpression_in_exclusiveOrExpression505 );
                andExpression();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:90:23: ( XOR andExpression )*
                loop18 : do {
                    int alt18 = 2;
                    int LA18_0 = input.LA( 1 );

                    if ( (LA18_0 == XOR) ) {
                        alt18 = 1;
                    }

                    switch ( alt18 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:90:25: XOR andExpression
                        {
                            match( input,
                                   XOR,
                                   FOLLOW_XOR_in_exclusiveOrExpression509 );
                            if ( state.failed ) return;
                            pushFollow( FOLLOW_andExpression_in_exclusiveOrExpression511 );
                            andExpression();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                        default :
                            break loop18;
                    }
                } while ( true );

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "exclusiveOrExpression"

    // $ANTLR start "andExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:93:1: andExpression : equalityExpression ( AMPER equalityExpression )* ;
    public final void andExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:5: ( equalityExpression ( AMPER equalityExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:9: equalityExpression ( AMPER equalityExpression )*
            {
                pushFollow( FOLLOW_equalityExpression_in_andExpression530 );
                equalityExpression();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:28: ( AMPER equalityExpression )*
                loop19 : do {
                    int alt19 = 2;
                    int LA19_0 = input.LA( 1 );

                    if ( (LA19_0 == AMPER) ) {
                        alt19 = 1;
                    }

                    switch ( alt19 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:30: AMPER equalityExpression
                        {
                            match( input,
                                   AMPER,
                                   FOLLOW_AMPER_in_andExpression534 );
                            if ( state.failed ) return;
                            pushFollow( FOLLOW_equalityExpression_in_andExpression536 );
                            equalityExpression();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                        default :
                            break loop19;
                    }
                } while ( true );

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "andExpression"

    // $ANTLR start "equalityExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:97:1: equalityExpression : instanceOfExpression ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )* ;
    public final void equalityExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:98:5: ( instanceOfExpression ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:98:9: instanceOfExpression ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )*
            {
                pushFollow( FOLLOW_instanceOfExpression_in_equalityExpression555 );
                instanceOfExpression();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:98:30: ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )*
                loop20 : do {
                    int alt20 = 2;
                    int LA20_0 = input.LA( 1 );

                    if ( ((LA20_0 >= EQUALS && LA20_0 <= NOT_EQUALS)) ) {
                        alt20 = 1;
                    }

                    switch ( alt20 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:98:32: ( EQUALS | NOT_EQUALS ) instanceOfExpression
                        {
                            if ( (input.LA( 1 ) >= EQUALS && input.LA( 1 ) <= NOT_EQUALS) ) {
                                input.consume();
                                state.errorRecovery = false;
                                state.failed = false;
                            } else {
                                if ( state.backtracking > 0 ) {
                                    state.failed = true;
                                    return;
                                }
                                MismatchedSetException mse = new MismatchedSetException( null,
                                                                                         input );
                                throw mse;
                            }

                            pushFollow( FOLLOW_instanceOfExpression_in_equalityExpression569 );
                            instanceOfExpression();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                        default :
                            break loop20;
                    }
                } while ( true );

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "equalityExpression"

    // $ANTLR start "instanceOfExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:101:1: instanceOfExpression : relationalExpression ( instanceof_key type )? ;
    public final void instanceOfExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:102:5: ( relationalExpression ( instanceof_key type )? )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:102:9: relationalExpression ( instanceof_key type )?
            {
                pushFollow( FOLLOW_relationalExpression_in_instanceOfExpression588 );
                relationalExpression();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:102:30: ( instanceof_key type )?
                int alt21 = 2;
                alt21 = dfa21.predict( input );
                switch ( alt21 ) {
                    case 1 :
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:102:31: instanceof_key type
                    {
                        pushFollow( FOLLOW_instanceof_key_in_instanceOfExpression591 );
                        instanceof_key();

                        state._fsp--;
                        if ( state.failed ) return;
                        pushFollow( FOLLOW_type_in_instanceOfExpression593 );
                        type();

                        state._fsp--;
                        if ( state.failed ) return;

                    }
                        break;

                }

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "instanceOfExpression"

    // $ANTLR start "relationalExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:105:1: relationalExpression : shiftExpression ( ( LESS )=> relationalOp shiftExpression )* ;
    public final void relationalExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:5: ( shiftExpression ( ( LESS )=> relationalOp shiftExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:9: shiftExpression ( ( LESS )=> relationalOp shiftExpression )*
            {
                pushFollow( FOLLOW_shiftExpression_in_relationalExpression611 );
                shiftExpression();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:25: ( ( LESS )=> relationalOp shiftExpression )*
                loop22 : do {
                    int alt22 = 2;
                    alt22 = dfa22.predict( input );
                    switch ( alt22 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:27: ( LESS )=> relationalOp shiftExpression
                        {
                            pushFollow( FOLLOW_relationalOp_in_relationalExpression620 );
                            relationalOp();

                            state._fsp--;
                            if ( state.failed ) return;
                            pushFollow( FOLLOW_shiftExpression_in_relationalExpression622 );
                            shiftExpression();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                        default :
                            break loop22;
                    }
                } while ( true );

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "relationalExpression"

    // $ANTLR start "relationalOp"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:109:1: relationalOp : ( LESS_EQUALS | GREATER_EQUALS | LESS | GREATER ) ;
    public final void relationalOp() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:110:2: ( ( LESS_EQUALS | GREATER_EQUALS | LESS | GREATER ) )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:110:4: ( LESS_EQUALS | GREATER_EQUALS | LESS | GREATER )
            {
                if ( (input.LA( 1 ) >= GREATER_EQUALS && input.LA( 1 ) <= LESS) ) {
                    input.consume();
                    state.errorRecovery = false;
                    state.failed = false;
                } else {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    MismatchedSetException mse = new MismatchedSetException( null,
                                                                             input );
                    throw mse;
                }

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "relationalOp"

    // $ANTLR start "shiftExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:113:1: shiftExpression : additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* ;
    public final void shiftExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:5: ( additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:9: additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )*
            {
                pushFollow( FOLLOW_additiveExpression_in_shiftExpression669 );
                additiveExpression();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:28: ( ( shiftOp )=> shiftOp additiveExpression )*
                loop23 : do {
                    int alt23 = 2;
                    alt23 = dfa23.predict( input );
                    switch ( alt23 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:30: ( shiftOp )=> shiftOp additiveExpression
                        {
                            pushFollow( FOLLOW_shiftOp_in_shiftExpression677 );
                            shiftOp();

                            state._fsp--;
                            if ( state.failed ) return;
                            pushFollow( FOLLOW_additiveExpression_in_shiftExpression679 );
                            additiveExpression();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                        default :
                            break loop23;
                    }
                } while ( true );

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "shiftExpression"

    // $ANTLR start "shiftOp"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:117:1: shiftOp : ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) ;
    public final void shiftOp() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:118:2: ( ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:118:4: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
            {
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:118:4: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
                int alt24 = 3;
                int LA24_0 = input.LA( 1 );

                if ( (LA24_0 == LESS) ) {
                    alt24 = 1;
                } else if ( (LA24_0 == GREATER) ) {
                    int LA24_2 = input.LA( 2 );

                    if ( (LA24_2 == GREATER) ) {
                        int LA24_3 = input.LA( 3 );

                        if ( (LA24_3 == GREATER) ) {
                            alt24 = 2;
                        } else if ( (LA24_3 == EOF || LA24_3 == FLOAT || (LA24_3 >= HEX && LA24_3 <= DECIMAL) || LA24_3 == STRING || (LA24_3 >= BOOL && LA24_3 <= NULL) || (LA24_3 >= DECR && LA24_3 <= INCR) || LA24_3 == LESS || LA24_3 == LEFT_PAREN
                                     || LA24_3 == LEFT_SQUARE || (LA24_3 >= NEGATION && LA24_3 <= TILDE) || (LA24_3 >= MINUS && LA24_3 <= PLUS) || LA24_3 == ID) ) {
                            alt24 = 3;
                        } else {
                            if ( state.backtracking > 0 ) {
                                state.failed = true;
                                return;
                            }
                            NoViableAltException nvae =
                                    new NoViableAltException( "",
                                                              24,
                                                              3,
                                                              input );

                            throw nvae;
                        }
                    } else {
                        if ( state.backtracking > 0 ) {
                            state.failed = true;
                            return;
                        }
                        NoViableAltException nvae =
                                new NoViableAltException( "",
                                                          24,
                                                          2,
                                                          input );

                        throw nvae;
                    }
                } else {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    NoViableAltException nvae =
                            new NoViableAltException( "",
                                                      24,
                                                      0,
                                                      input );

                    throw nvae;
                }
                switch ( alt24 ) {
                    case 1 :
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:118:5: LESS LESS
                    {
                        match( input,
                               LESS,
                               FOLLOW_LESS_in_shiftOp694 );
                        if ( state.failed ) return;
                        match( input,
                               LESS,
                               FOLLOW_LESS_in_shiftOp696 );
                        if ( state.failed ) return;

                    }
                        break;
                    case 2 :
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:118:17: GREATER GREATER GREATER
                    {
                        match( input,
                               GREATER,
                               FOLLOW_GREATER_in_shiftOp700 );
                        if ( state.failed ) return;
                        match( input,
                               GREATER,
                               FOLLOW_GREATER_in_shiftOp702 );
                        if ( state.failed ) return;
                        match( input,
                               GREATER,
                               FOLLOW_GREATER_in_shiftOp704 );
                        if ( state.failed ) return;

                    }
                        break;
                    case 3 :
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:118:43: GREATER GREATER
                    {
                        match( input,
                               GREATER,
                               FOLLOW_GREATER_in_shiftOp708 );
                        if ( state.failed ) return;
                        match( input,
                               GREATER,
                               FOLLOW_GREATER_in_shiftOp710 );
                        if ( state.failed ) return;

                    }
                        break;

                }

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "shiftOp"

    // $ANTLR start "additiveExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:121:1: additiveExpression : multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* ;
    public final void additiveExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:5: ( multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:9: multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
            {
                pushFollow( FOLLOW_multiplicativeExpression_in_additiveExpression728 );
                multiplicativeExpression();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:34: ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
                loop25 : do {
                    int alt25 = 2;
                    alt25 = dfa25.predict( input );
                    switch ( alt25 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:36: ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression
                        {
                            if ( (input.LA( 1 ) >= MINUS && input.LA( 1 ) <= PLUS) ) {
                                input.consume();
                                state.errorRecovery = false;
                                state.failed = false;
                            } else {
                                if ( state.backtracking > 0 ) {
                                    state.failed = true;
                                    return;
                                }
                                MismatchedSetException mse = new MismatchedSetException( null,
                                                                                         input );
                                throw mse;
                            }

                            pushFollow( FOLLOW_multiplicativeExpression_in_additiveExpression747 );
                            multiplicativeExpression();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                        default :
                            break loop25;
                    }
                } while ( true );

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "additiveExpression"

    // $ANTLR start "multiplicativeExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:125:1: multiplicativeExpression : unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* ;
    public final void multiplicativeExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:126:5: ( unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:126:9: unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )*
            {
                pushFollow( FOLLOW_unaryExpression_in_multiplicativeExpression766 );
                unaryExpression();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:126:25: ( ( STAR | DIV | MOD ) unaryExpression )*
                loop26 : do {
                    int alt26 = 2;
                    int LA26_0 = input.LA( 1 );

                    if ( ((LA26_0 >= MOD && LA26_0 <= STAR) || LA26_0 == DIV) ) {
                        alt26 = 1;
                    }

                    switch ( alt26 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:126:27: ( STAR | DIV | MOD ) unaryExpression
                        {
                            if ( (input.LA( 1 ) >= MOD && input.LA( 1 ) <= STAR) || input.LA( 1 ) == DIV ) {
                                input.consume();
                                state.errorRecovery = false;
                                state.failed = false;
                            } else {
                                if ( state.backtracking > 0 ) {
                                    state.failed = true;
                                    return;
                                }
                                MismatchedSetException mse = new MismatchedSetException( null,
                                                                                         input );
                                throw mse;
                            }

                            pushFollow( FOLLOW_unaryExpression_in_multiplicativeExpression784 );
                            unaryExpression();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                        default :
                            break loop26;
                    }
                } while ( true );

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "multiplicativeExpression"

    // $ANTLR start "unaryExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:129:1: unaryExpression : ( PLUS unaryExpression | MINUS unaryExpression | INCR primary | DECR primary | unaryExpressionNotPlusMinus );
    public final void unaryExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:5: ( PLUS unaryExpression | MINUS unaryExpression | INCR primary | DECR primary | unaryExpressionNotPlusMinus )
            int alt27 = 5;
            switch ( input.LA( 1 ) ) {
                case PLUS : {
                    alt27 = 1;
                }
                    break;
                case MINUS : {
                    alt27 = 2;
                }
                    break;
                case INCR : {
                    alt27 = 3;
                }
                    break;
                case DECR : {
                    alt27 = 4;
                }
                    break;
                case FLOAT :
                case HEX :
                case DECIMAL :
                case STRING :
                case BOOL :
                case NULL :
                case LESS :
                case LEFT_PAREN :
                case LEFT_SQUARE :
                case NEGATION :
                case TILDE :
                case ID : {
                    alt27 = 5;
                }
                    break;
                default :
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    NoViableAltException nvae =
                            new NoViableAltException( "",
                                                      27,
                                                      0,
                                                      input );

                    throw nvae;
            }

            switch ( alt27 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:9: PLUS unaryExpression
                {
                    match( input,
                           PLUS,
                           FOLLOW_PLUS_in_unaryExpression804 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_unaryExpression_in_unaryExpression806 );
                    unaryExpression();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:131:7: MINUS unaryExpression
                {
                    match( input,
                           MINUS,
                           FOLLOW_MINUS_in_unaryExpression814 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_unaryExpression_in_unaryExpression816 );
                    unaryExpression();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:132:9: INCR primary
                {
                    match( input,
                           INCR,
                           FOLLOW_INCR_in_unaryExpression826 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_primary_in_unaryExpression828 );
                    primary();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:133:9: DECR primary
                {
                    match( input,
                           DECR,
                           FOLLOW_DECR_in_unaryExpression838 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_primary_in_unaryExpression840 );
                    primary();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:134:9: unaryExpressionNotPlusMinus
                {
                    pushFollow( FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression850 );
                    unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;

            }
        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "unaryExpression"

    // $ANTLR start "unaryExpressionNotPlusMinus"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:137:1: unaryExpressionNotPlusMinus options {backtrack=true; memoize=true; } : ( TILDE unaryExpression | NEGATION unaryExpression | castExpression | primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );
    public final void unaryExpressionNotPlusMinus() throws RecognitionException {
        int unaryExpressionNotPlusMinus_StartIndex = input.index();
        try {
            if ( state.backtracking > 0 && alreadyParsedRule( input,
                                                              22 ) ) {
                return;
            }
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:139:5: ( TILDE unaryExpression | NEGATION unaryExpression | castExpression | primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? )
            int alt30 = 4;
            alt30 = dfa30.predict( input );
            switch ( alt30 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:139:9: TILDE unaryExpression
                {
                    match( input,
                           TILDE,
                           FOLLOW_TILDE_in_unaryExpressionNotPlusMinus883 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus885 );
                    unaryExpression();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:140:8: NEGATION unaryExpression
                {
                    match( input,
                           NEGATION,
                           FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus894 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus896 );
                    unaryExpression();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:141:9: castExpression
                {
                    pushFollow( FOLLOW_castExpression_in_unaryExpressionNotPlusMinus906 );
                    castExpression();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:9: primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )?
                {
                    pushFollow( FOLLOW_primary_in_unaryExpressionNotPlusMinus916 );
                    primary();

                    state._fsp--;
                    if ( state.failed ) return;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:17: ( ( selector )=> selector )*
                    loop28 : do {
                        int alt28 = 2;
                        alt28 = dfa28.predict( input );
                        switch ( alt28 ) {
                            case 1 :
                                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:18: ( selector )=> selector
                            {
                                pushFollow( FOLLOW_selector_in_unaryExpressionNotPlusMinus923 );
                                selector();

                                state._fsp--;
                                if ( state.failed ) return;

                            }
                                break;

                            default :
                                break loop28;
                        }
                    } while ( true );

                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:41: ( ( INCR | DECR )=> ( INCR | DECR ) )?
                    int alt29 = 2;
                    alt29 = dfa29.predict( input );
                    switch ( alt29 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:42: ( INCR | DECR )=> ( INCR | DECR )
                        {
                            if ( (input.LA( 1 ) >= DECR && input.LA( 1 ) <= INCR) ) {
                                input.consume();
                                state.errorRecovery = false;
                                state.failed = false;
                            } else {
                                if ( state.backtracking > 0 ) {
                                    state.failed = true;
                                    return;
                                }
                                MismatchedSetException mse = new MismatchedSetException( null,
                                                                                         input );
                                throw mse;
                            }

                        }
                            break;

                    }

                }
                    break;

            }
        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
            if ( state.backtracking > 0 ) {
                memoize( input,
                         22,
                         unaryExpressionNotPlusMinus_StartIndex );
            }
        }
        return;
    }

    // $ANTLR end "unaryExpressionNotPlusMinus"

    // $ANTLR start "castExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:145:1: castExpression options {backtrack=true; memoize=true; } : ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus | LEFT_PAREN expression RIGHT_PAREN unaryExpressionNotPlusMinus );
    public final void castExpression() throws RecognitionException {
        int castExpression_StartIndex = input.index();
        try {
            if ( state.backtracking > 0 && alreadyParsedRule( input,
                                                              23 ) ) {
                return;
            }
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:147:5: ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus | LEFT_PAREN expression RIGHT_PAREN unaryExpressionNotPlusMinus )
            int alt31 = 3;
            int LA31_0 = input.LA( 1 );

            if ( (LA31_0 == LEFT_PAREN) ) {
                int LA31_1 = input.LA( 2 );

                if ( (synpred15_DRLExpressions()) ) {
                    alt31 = 1;
                } else if ( (synpred16_DRLExpressions()) ) {
                    alt31 = 2;
                } else if ( (true) ) {
                    alt31 = 3;
                } else {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    NoViableAltException nvae =
                            new NoViableAltException( "",
                                                      31,
                                                      1,
                                                      input );

                    throw nvae;
                }
            } else {
                if ( state.backtracking > 0 ) {
                    state.failed = true;
                    return;
                }
                NoViableAltException nvae =
                        new NoViableAltException( "",
                                                  31,
                                                  0,
                                                  input );

                throw nvae;
            }
            switch ( alt31 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:147:8: ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression
                {
                    match( input,
                           LEFT_PAREN,
                           FOLLOW_LEFT_PAREN_in_castExpression985 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_primitiveType_in_castExpression987 );
                    primitiveType();

                    state._fsp--;
                    if ( state.failed ) return;
                    match( input,
                           RIGHT_PAREN,
                           FOLLOW_RIGHT_PAREN_in_castExpression989 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_unaryExpression_in_castExpression991 );
                    unaryExpression();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:148:8: ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus
                {
                    match( input,
                           LEFT_PAREN,
                           FOLLOW_LEFT_PAREN_in_castExpression1008 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_type_in_castExpression1010 );
                    type();

                    state._fsp--;
                    if ( state.failed ) return;
                    match( input,
                           RIGHT_PAREN,
                           FOLLOW_RIGHT_PAREN_in_castExpression1012 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_unaryExpressionNotPlusMinus_in_castExpression1014 );
                    unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:149:8: LEFT_PAREN expression RIGHT_PAREN unaryExpressionNotPlusMinus
                {
                    match( input,
                           LEFT_PAREN,
                           FOLLOW_LEFT_PAREN_in_castExpression1023 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_expression_in_castExpression1025 );
                    expression();

                    state._fsp--;
                    if ( state.failed ) return;
                    match( input,
                           RIGHT_PAREN,
                           FOLLOW_RIGHT_PAREN_in_castExpression1027 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_unaryExpressionNotPlusMinus_in_castExpression1029 );
                    unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;

            }
        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
            if ( state.backtracking > 0 ) {
                memoize( input,
                         23,
                         castExpression_StartIndex );
            }
        }
        return;
    }

    // $ANTLR end "castExpression"

    // $ANTLR start "primitiveType"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:152:1: primitiveType options {backtrack=true; memoize=true; } : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );
    public final void primitiveType() throws RecognitionException {
        int primitiveType_StartIndex = input.index();
        try {
            if ( state.backtracking > 0 && alreadyParsedRule( input,
                                                              24 ) ) {
                return;
            }
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:154:5: ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key )
            int alt32 = 8;
            alt32 = dfa32.predict( input );
            switch ( alt32 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:154:9: boolean_key
                {
                    pushFollow( FOLLOW_boolean_key_in_primitiveType1066 );
                    boolean_key();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:155:7: char_key
                {
                    pushFollow( FOLLOW_char_key_in_primitiveType1074 );
                    char_key();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:156:7: byte_key
                {
                    pushFollow( FOLLOW_byte_key_in_primitiveType1082 );
                    byte_key();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:157:7: short_key
                {
                    pushFollow( FOLLOW_short_key_in_primitiveType1090 );
                    short_key();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:158:7: int_key
                {
                    pushFollow( FOLLOW_int_key_in_primitiveType1098 );
                    int_key();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:159:7: long_key
                {
                    pushFollow( FOLLOW_long_key_in_primitiveType1106 );
                    long_key();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:160:7: float_key
                {
                    pushFollow( FOLLOW_float_key_in_primitiveType1114 );
                    float_key();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:161:7: double_key
                {
                    pushFollow( FOLLOW_double_key_in_primitiveType1122 );
                    double_key();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;

            }
        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
            if ( state.backtracking > 0 ) {
                memoize( input,
                         24,
                         primitiveType_StartIndex );
            }
        }
        return;
    }

    // $ANTLR end "primitiveType"

    // $ANTLR start "primary"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:164:1: primary : ( ( parExpression )=> parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )? );
    public final void primary() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:166:5: ( ( parExpression )=> parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )? )
            int alt37 = 9;
            alt37 = dfa37.predict( input );
            switch ( alt37 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:166:7: ( parExpression )=> parExpression
                {
                    pushFollow( FOLLOW_parExpression_in_primary1145 );
                    parExpression();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:167:9: ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments )
                {
                    pushFollow( FOLLOW_nonWildcardTypeArguments_in_primary1160 );
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if ( state.failed ) return;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:167:63: ( explicitGenericInvocationSuffix | this_key arguments )
                    int alt33 = 2;
                    int LA33_0 = input.LA( 1 );

                    if ( (LA33_0 == ID) ) {
                        int LA33_1 = input.LA( 2 );

                        if ( (!((((helper.validateIdentifierKey( DroolsSoftKeywords.THIS )))))) ) {
                            alt33 = 1;
                        } else if ( (((helper.validateIdentifierKey( DroolsSoftKeywords.THIS )))) ) {
                            alt33 = 2;
                        } else {
                            if ( state.backtracking > 0 ) {
                                state.failed = true;
                                return;
                            }
                            NoViableAltException nvae =
                                    new NoViableAltException( "",
                                                              33,
                                                              1,
                                                              input );

                            throw nvae;
                        }
                    } else {
                        if ( state.backtracking > 0 ) {
                            state.failed = true;
                            return;
                        }
                        NoViableAltException nvae =
                                new NoViableAltException( "",
                                                          33,
                                                          0,
                                                          input );

                        throw nvae;
                    }
                    switch ( alt33 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:167:64: explicitGenericInvocationSuffix
                        {
                            pushFollow( FOLLOW_explicitGenericInvocationSuffix_in_primary1163 );
                            explicitGenericInvocationSuffix();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:167:98: this_key arguments
                        {
                            pushFollow( FOLLOW_this_key_in_primary1167 );
                            this_key();

                            state._fsp--;
                            if ( state.failed ) return;
                            pushFollow( FOLLOW_arguments_in_primary1169 );
                            arguments();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                    }

                }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:168:9: ( literal )=> literal
                {
                    pushFollow( FOLLOW_literal_in_primary1185 );
                    literal();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:170:9: ( super_key )=> super_key superSuffix
                {
                    pushFollow( FOLLOW_super_key_in_primary1205 );
                    super_key();

                    state._fsp--;
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_superSuffix_in_primary1207 );
                    superSuffix();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:171:9: ( new_key )=> new_key creator
                {
                    pushFollow( FOLLOW_new_key_in_primary1222 );
                    new_key();

                    state._fsp--;
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_creator_in_primary1224 );
                    creator();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:172:9: ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key
                {
                    pushFollow( FOLLOW_primitiveType_in_primary1239 );
                    primitiveType();

                    state._fsp--;
                    if ( state.failed ) return;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:172:41: ( LEFT_SQUARE RIGHT_SQUARE )*
                    loop34 : do {
                        int alt34 = 2;
                        int LA34_0 = input.LA( 1 );

                        if ( (LA34_0 == LEFT_SQUARE) ) {
                            alt34 = 1;
                        }

                        switch ( alt34 ) {
                            case 1 :
                                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:172:42: LEFT_SQUARE RIGHT_SQUARE
                            {
                                match( input,
                                       LEFT_SQUARE,
                                       FOLLOW_LEFT_SQUARE_in_primary1242 );
                                if ( state.failed ) return;
                                match( input,
                                       RIGHT_SQUARE,
                                       FOLLOW_RIGHT_SQUARE_in_primary1244 );
                                if ( state.failed ) return;

                            }
                                break;

                            default :
                                break loop34;
                        }
                    } while ( true );

                    match( input,
                           DOT,
                           FOLLOW_DOT_in_primary1248 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_class_key_in_primary1250 );
                    class_key();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:174:9: ( inlineMapExpression )=> inlineMapExpression
                {
                    pushFollow( FOLLOW_inlineMapExpression_in_primary1270 );
                    inlineMapExpression();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:175:9: ( inlineListExpression )=> inlineListExpression
                {
                    pushFollow( FOLLOW_inlineListExpression_in_primary1285 );
                    inlineListExpression();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:176:9: ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )?
                {
                    match( input,
                           ID,
                           FOLLOW_ID_in_primary1299 );
                    if ( state.failed ) return;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:176:18: ( ( DOT ID )=> DOT ID )*
                    loop35 : do {
                        int alt35 = 2;
                        int LA35_0 = input.LA( 1 );

                        if ( (LA35_0 == DOT) ) {
                            int LA35_2 = input.LA( 2 );

                            if ( (LA35_2 == ID) ) {
                                int LA35_3 = input.LA( 3 );

                                if ( (synpred33_DRLExpressions()) ) {
                                    alt35 = 1;
                                }

                            }

                        }

                        switch ( alt35 ) {
                            case 1 :
                                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:176:19: ( DOT ID )=> DOT ID
                            {
                                match( input,
                                       DOT,
                                       FOLLOW_DOT_in_primary1308 );
                                if ( state.failed ) return;
                                match( input,
                                       ID,
                                       FOLLOW_ID_in_primary1310 );
                                if ( state.failed ) return;

                            }
                                break;

                            default :
                                break loop35;
                        }
                    } while ( true );

                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:176:38: ( ( identifierSuffix )=> identifierSuffix )?
                    int alt36 = 2;
                    alt36 = dfa36.predict( input );
                    switch ( alt36 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:176:39: ( identifierSuffix )=> identifierSuffix
                        {
                            pushFollow( FOLLOW_identifierSuffix_in_primary1319 );
                            identifierSuffix();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                    }

                }
                    break;

            }
        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "primary"

    // $ANTLR start "inlineListExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:179:1: inlineListExpression : LEFT_SQUARE ( expressionList )? RIGHT_SQUARE ;
    public final void inlineListExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:180:5: ( LEFT_SQUARE ( expressionList )? RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:180:9: LEFT_SQUARE ( expressionList )? RIGHT_SQUARE
            {
                match( input,
                       LEFT_SQUARE,
                       FOLLOW_LEFT_SQUARE_in_inlineListExpression1340 );
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:180:21: ( expressionList )?
                int alt38 = 2;
                int LA38_0 = input.LA( 1 );

                if ( (LA38_0 == FLOAT || (LA38_0 >= HEX && LA38_0 <= DECIMAL) || LA38_0 == STRING || (LA38_0 >= BOOL && LA38_0 <= NULL) || (LA38_0 >= DECR && LA38_0 <= INCR) || LA38_0 == LESS || LA38_0 == LEFT_PAREN || LA38_0 == LEFT_SQUARE
                      || (LA38_0 >= NEGATION && LA38_0 <= TILDE) || (LA38_0 >= MINUS && LA38_0 <= PLUS) || LA38_0 == ID) ) {
                    alt38 = 1;
                }
                switch ( alt38 ) {
                    case 1 :
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:180:21: expressionList
                    {
                        pushFollow( FOLLOW_expressionList_in_inlineListExpression1342 );
                        expressionList();

                        state._fsp--;
                        if ( state.failed ) return;

                    }
                        break;

                }

                match( input,
                       RIGHT_SQUARE,
                       FOLLOW_RIGHT_SQUARE_in_inlineListExpression1345 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "inlineListExpression"

    // $ANTLR start "inlineMapExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:183:1: inlineMapExpression : LEFT_SQUARE ( mapExpressionList )+ RIGHT_SQUARE ;
    public final void inlineMapExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:184:5: ( LEFT_SQUARE ( mapExpressionList )+ RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:184:7: LEFT_SQUARE ( mapExpressionList )+ RIGHT_SQUARE
            {
                match( input,
                       LEFT_SQUARE,
                       FOLLOW_LEFT_SQUARE_in_inlineMapExpression1367 );
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:184:19: ( mapExpressionList )+
                int cnt39 = 0;
                loop39 : do {
                    int alt39 = 2;
                    int LA39_0 = input.LA( 1 );

                    if ( (LA39_0 == FLOAT || (LA39_0 >= HEX && LA39_0 <= DECIMAL) || LA39_0 == STRING || (LA39_0 >= BOOL && LA39_0 <= NULL) || (LA39_0 >= DECR && LA39_0 <= INCR) || LA39_0 == LESS || LA39_0 == LEFT_PAREN || LA39_0 == LEFT_SQUARE
                          || (LA39_0 >= NEGATION && LA39_0 <= TILDE) || (LA39_0 >= MINUS && LA39_0 <= PLUS) || LA39_0 == ID) ) {
                        alt39 = 1;
                    }

                    switch ( alt39 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:184:19: mapExpressionList
                        {
                            pushFollow( FOLLOW_mapExpressionList_in_inlineMapExpression1369 );
                            mapExpressionList();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                        default :
                            if ( cnt39 >= 1 ) break loop39;
                            if ( state.backtracking > 0 ) {
                                state.failed = true;
                                return;
                            }
                            EarlyExitException eee =
                                    new EarlyExitException( 39,
                                                            input );
                            throw eee;
                    }
                    cnt39++;
                } while ( true );

                match( input,
                       RIGHT_SQUARE,
                       FOLLOW_RIGHT_SQUARE_in_inlineMapExpression1372 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "inlineMapExpression"

    // $ANTLR start "mapExpressionList"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:187:1: mapExpressionList : mapEntry ( COMMA mapEntry )* ;
    public final void mapExpressionList() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:188:5: ( mapEntry ( COMMA mapEntry )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:188:7: mapEntry ( COMMA mapEntry )*
            {
                pushFollow( FOLLOW_mapEntry_in_mapExpressionList1389 );
                mapEntry();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:188:16: ( COMMA mapEntry )*
                loop40 : do {
                    int alt40 = 2;
                    int LA40_0 = input.LA( 1 );

                    if ( (LA40_0 == COMMA) ) {
                        alt40 = 1;
                    }

                    switch ( alt40 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:188:17: COMMA mapEntry
                        {
                            match( input,
                                   COMMA,
                                   FOLLOW_COMMA_in_mapExpressionList1392 );
                            if ( state.failed ) return;
                            pushFollow( FOLLOW_mapEntry_in_mapExpressionList1394 );
                            mapEntry();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                        default :
                            break loop40;
                    }
                } while ( true );

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "mapExpressionList"

    // $ANTLR start "mapEntry"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:191:1: mapEntry : expression COLON expression ;
    public final void mapEntry() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:192:5: ( expression COLON expression )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:192:7: expression COLON expression
            {
                pushFollow( FOLLOW_expression_in_mapEntry1417 );
                expression();

                state._fsp--;
                if ( state.failed ) return;
                match( input,
                       COLON,
                       FOLLOW_COLON_in_mapEntry1419 );
                if ( state.failed ) return;
                pushFollow( FOLLOW_expression_in_mapEntry1421 );
                expression();

                state._fsp--;
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "mapEntry"

    // $ANTLR start "parExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:195:1: parExpression : LEFT_PAREN expression RIGHT_PAREN ;
    public final void parExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:196:2: ( LEFT_PAREN expression RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:196:4: LEFT_PAREN expression RIGHT_PAREN
            {
                match( input,
                       LEFT_PAREN,
                       FOLLOW_LEFT_PAREN_in_parExpression1435 );
                if ( state.failed ) return;
                pushFollow( FOLLOW_expression_in_parExpression1437 );
                expression();

                state._fsp--;
                if ( state.failed ) return;
                match( input,
                       RIGHT_PAREN,
                       FOLLOW_RIGHT_PAREN_in_parExpression1439 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "parExpression"

    // $ANTLR start "identifierSuffix"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:199:1: identifierSuffix options {backtrack=true; memoize=true; } : ( ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments );
    public final void identifierSuffix() throws RecognitionException {
        int identifierSuffix_StartIndex = input.index();
        try {
            if ( state.backtracking > 0 && alreadyParsedRule( input,
                                                              31 ) ) {
                return;
            }
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:201:5: ( ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments )
            int alt43 = 3;
            int LA43_0 = input.LA( 1 );

            if ( (LA43_0 == LEFT_SQUARE) ) {
                int LA43_1 = input.LA( 2 );

                if ( (LA43_1 == RIGHT_SQUARE) ) {
                    alt43 = 1;
                } else if ( (LA43_1 == FLOAT || (LA43_1 >= HEX && LA43_1 <= DECIMAL) || LA43_1 == STRING || (LA43_1 >= BOOL && LA43_1 <= NULL) || (LA43_1 >= DECR && LA43_1 <= INCR) || LA43_1 == LESS || LA43_1 == LEFT_PAREN || LA43_1 == LEFT_SQUARE
                             || (LA43_1 >= NEGATION && LA43_1 <= TILDE) || (LA43_1 >= MINUS && LA43_1 <= PLUS) || LA43_1 == ID) ) {
                    alt43 = 2;
                } else {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    NoViableAltException nvae =
                            new NoViableAltException( "",
                                                      43,
                                                      1,
                                                      input );

                    throw nvae;
                }
            } else if ( (LA43_0 == LEFT_PAREN) ) {
                alt43 = 3;
            } else {
                if ( state.backtracking > 0 ) {
                    state.failed = true;
                    return;
                }
                NoViableAltException nvae =
                        new NoViableAltException( "",
                                                  43,
                                                  0,
                                                  input );

                throw nvae;
            }
            switch ( alt43 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:201:7: ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key
                {
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:201:7: ( LEFT_SQUARE RIGHT_SQUARE )+
                    int cnt41 = 0;
                    loop41 : do {
                        int alt41 = 2;
                        int LA41_0 = input.LA( 1 );

                        if ( (LA41_0 == LEFT_SQUARE) ) {
                            alt41 = 1;
                        }

                        switch ( alt41 ) {
                            case 1 :
                                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:201:8: LEFT_SQUARE RIGHT_SQUARE
                            {
                                match( input,
                                       LEFT_SQUARE,
                                       FOLLOW_LEFT_SQUARE_in_identifierSuffix1469 );
                                if ( state.failed ) return;
                                match( input,
                                       RIGHT_SQUARE,
                                       FOLLOW_RIGHT_SQUARE_in_identifierSuffix1471 );
                                if ( state.failed ) return;

                            }
                                break;

                            default :
                                if ( cnt41 >= 1 ) break loop41;
                                if ( state.backtracking > 0 ) {
                                    state.failed = true;
                                    return;
                                }
                                EarlyExitException eee =
                                        new EarlyExitException( 41,
                                                                input );
                                throw eee;
                        }
                        cnt41++;
                    } while ( true );

                    match( input,
                           DOT,
                           FOLLOW_DOT_in_identifierSuffix1475 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_class_key_in_identifierSuffix1477 );
                    class_key();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:202:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
                {
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:202:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
                    int cnt42 = 0;
                    loop42 : do {
                        int alt42 = 2;
                        alt42 = dfa42.predict( input );
                        switch ( alt42 ) {
                            case 1 :
                                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:202:8: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
                            {
                                match( input,
                                       LEFT_SQUARE,
                                       FOLLOW_LEFT_SQUARE_in_identifierSuffix1492 );
                                if ( state.failed ) return;
                                pushFollow( FOLLOW_expression_in_identifierSuffix1494 );
                                expression();

                                state._fsp--;
                                if ( state.failed ) return;
                                match( input,
                                       RIGHT_SQUARE,
                                       FOLLOW_RIGHT_SQUARE_in_identifierSuffix1496 );
                                if ( state.failed ) return;

                            }
                                break;

                            default :
                                if ( cnt42 >= 1 ) break loop42;
                                if ( state.backtracking > 0 ) {
                                    state.failed = true;
                                    return;
                                }
                                EarlyExitException eee =
                                        new EarlyExitException( 42,
                                                                input );
                                throw eee;
                        }
                        cnt42++;
                    } while ( true );

                }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:203:9: arguments
                {
                    pushFollow( FOLLOW_arguments_in_identifierSuffix1509 );
                    arguments();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;

            }
        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
            if ( state.backtracking > 0 ) {
                memoize( input,
                         31,
                         identifierSuffix_StartIndex );
            }
        }
        return;
    }

    // $ANTLR end "identifierSuffix"

    // $ANTLR start "creator"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:211:1: creator : ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) ;
    public final void creator() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:212:2: ( ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:212:4: ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest )
            {
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:212:4: ( nonWildcardTypeArguments )?
                int alt44 = 2;
                int LA44_0 = input.LA( 1 );

                if ( (LA44_0 == LESS) ) {
                    alt44 = 1;
                }
                switch ( alt44 ) {
                    case 1 :
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:212:4: nonWildcardTypeArguments
                    {
                        pushFollow( FOLLOW_nonWildcardTypeArguments_in_creator1527 );
                        nonWildcardTypeArguments();

                        state._fsp--;
                        if ( state.failed ) return;

                    }
                        break;

                }

                pushFollow( FOLLOW_createdName_in_creator1530 );
                createdName();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:213:9: ( arrayCreatorRest | classCreatorRest )
                int alt45 = 2;
                int LA45_0 = input.LA( 1 );

                if ( (LA45_0 == LEFT_SQUARE) ) {
                    alt45 = 1;
                } else if ( (LA45_0 == LEFT_PAREN) ) {
                    alt45 = 2;
                } else {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    NoViableAltException nvae =
                            new NoViableAltException( "",
                                                      45,
                                                      0,
                                                      input );

                    throw nvae;
                }
                switch ( alt45 ) {
                    case 1 :
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:213:10: arrayCreatorRest
                    {
                        pushFollow( FOLLOW_arrayCreatorRest_in_creator1541 );
                        arrayCreatorRest();

                        state._fsp--;
                        if ( state.failed ) return;

                    }
                        break;
                    case 2 :
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:213:29: classCreatorRest
                    {
                        pushFollow( FOLLOW_classCreatorRest_in_creator1545 );
                        classCreatorRest();

                        state._fsp--;
                        if ( state.failed ) return;

                    }
                        break;

                }

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "creator"

    // $ANTLR start "createdName"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:216:1: createdName : ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType );
    public final void createdName() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:217:2: ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType )
            int alt49 = 2;
            int LA49_0 = input.LA( 1 );

            if ( (LA49_0 == ID)
                 && ((!(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                          || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                          || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT )))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || ((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT )))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE )))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR )))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG )))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                      || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT )))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))
                      || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                             || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR )))))) || !(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT )))
                                                                                                                                                                    || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG )))
                                                                                                                                                                    || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                                                                                                                                                                    || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE )))
                                                                                                                                                                    || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT )))
                                                                                                                                                                    || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                                                                                                                                                                    || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR )))))))) ) {
                int LA49_1 = input.LA( 2 );

                if ( (!(((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                          || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                          || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))))) ) {
                    alt49 = 1;
                } else if ( ((((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))
                              || ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))
                              || ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))) || ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))) ) {
                    alt49 = 2;
                } else {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    NoViableAltException nvae =
                            new NoViableAltException( "",
                                                      49,
                                                      1,
                                                      input );

                    throw nvae;
                }
            } else {
                if ( state.backtracking > 0 ) {
                    state.failed = true;
                    return;
                }
                NoViableAltException nvae =
                        new NoViableAltException( "",
                                                  49,
                                                  0,
                                                  input );

                throw nvae;
            }
            switch ( alt49 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:217:4: ID ( typeArguments )? ( DOT ID ( typeArguments )? )*
                {
                    match( input,
                           ID,
                           FOLLOW_ID_in_createdName1557 );
                    if ( state.failed ) return;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:217:7: ( typeArguments )?
                    int alt46 = 2;
                    int LA46_0 = input.LA( 1 );

                    if ( (LA46_0 == LESS) ) {
                        alt46 = 1;
                    }
                    switch ( alt46 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:217:7: typeArguments
                        {
                            pushFollow( FOLLOW_typeArguments_in_createdName1559 );
                            typeArguments();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                    }

                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:218:9: ( DOT ID ( typeArguments )? )*
                    loop48 : do {
                        int alt48 = 2;
                        int LA48_0 = input.LA( 1 );

                        if ( (LA48_0 == DOT) ) {
                            alt48 = 1;
                        }

                        switch ( alt48 ) {
                            case 1 :
                                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:218:11: DOT ID ( typeArguments )?
                            {
                                match( input,
                                       DOT,
                                       FOLLOW_DOT_in_createdName1572 );
                                if ( state.failed ) return;
                                match( input,
                                       ID,
                                       FOLLOW_ID_in_createdName1574 );
                                if ( state.failed ) return;
                                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:218:18: ( typeArguments )?
                                int alt47 = 2;
                                int LA47_0 = input.LA( 1 );

                                if ( (LA47_0 == LESS) ) {
                                    alt47 = 1;
                                }
                                switch ( alt47 ) {
                                    case 1 :
                                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:218:18: typeArguments
                                    {
                                        pushFollow( FOLLOW_typeArguments_in_createdName1576 );
                                        typeArguments();

                                        state._fsp--;
                                        if ( state.failed ) return;

                                    }
                                        break;

                                }

                            }
                                break;

                            default :
                                break loop48;
                        }
                    } while ( true );

                }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:219:11: primitiveType
                {
                    pushFollow( FOLLOW_primitiveType_in_createdName1591 );
                    primitiveType();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;

            }
        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "createdName"

    // $ANTLR start "innerCreator"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:222:1: innerCreator : {...}? => ID classCreatorRest ;
    public final void innerCreator() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:223:2: ({...}? => ID classCreatorRest )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:223:4: {...}? => ID classCreatorRest
            {
                if ( !((!(helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))) ) {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    throw new FailedPredicateException( input,
                                                        "innerCreator",
                                                        "!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))" );
                }
                match( input,
                       ID,
                       FOLLOW_ID_in_innerCreator1606 );
                if ( state.failed ) return;
                pushFollow( FOLLOW_classCreatorRest_in_innerCreator1608 );
                classCreatorRest();

                state._fsp--;
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "innerCreator"

    // $ANTLR start "arrayCreatorRest"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:226:1: arrayCreatorRest : LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) ;
    public final void arrayCreatorRest() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:227:2: ( LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:227:6: LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
            {
                match( input,
                       LEFT_SQUARE,
                       FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1621 );
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:228:2: ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                int alt53 = 2;
                int LA53_0 = input.LA( 1 );

                if ( (LA53_0 == RIGHT_SQUARE) ) {
                    alt53 = 1;
                } else if ( (LA53_0 == FLOAT || (LA53_0 >= HEX && LA53_0 <= DECIMAL) || LA53_0 == STRING || (LA53_0 >= BOOL && LA53_0 <= NULL) || (LA53_0 >= DECR && LA53_0 <= INCR) || LA53_0 == LESS || LA53_0 == LEFT_PAREN || LA53_0 == LEFT_SQUARE
                             || (LA53_0 >= NEGATION && LA53_0 <= TILDE) || (LA53_0 >= MINUS && LA53_0 <= PLUS) || LA53_0 == ID) ) {
                    alt53 = 2;
                } else {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    NoViableAltException nvae =
                            new NoViableAltException( "",
                                                      53,
                                                      0,
                                                      input );

                    throw nvae;
                }
                switch ( alt53 ) {
                    case 1 :
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:228:6: RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer
                    {
                        match( input,
                               RIGHT_SQUARE,
                               FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1629 );
                        if ( state.failed ) return;
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:228:19: ( LEFT_SQUARE RIGHT_SQUARE )*
                        loop50 : do {
                            int alt50 = 2;
                            int LA50_0 = input.LA( 1 );

                            if ( (LA50_0 == LEFT_SQUARE) ) {
                                alt50 = 1;
                            }

                            switch ( alt50 ) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:228:20: LEFT_SQUARE RIGHT_SQUARE
                                {
                                    match( input,
                                           LEFT_SQUARE,
                                           FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1632 );
                                    if ( state.failed ) return;
                                    match( input,
                                           RIGHT_SQUARE,
                                           FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1634 );
                                    if ( state.failed ) return;

                                }
                                    break;

                                default :
                                    break loop50;
                            }
                        } while ( true );

                        pushFollow( FOLLOW_arrayInitializer_in_arrayCreatorRest1638 );
                        arrayInitializer();

                        state._fsp--;
                        if ( state.failed ) return;

                    }
                        break;
                    case 2 :
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:229:13: expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                        pushFollow( FOLLOW_expression_in_arrayCreatorRest1652 );
                        expression();

                        state._fsp--;
                        if ( state.failed ) return;
                        match( input,
                               RIGHT_SQUARE,
                               FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1654 );
                        if ( state.failed ) return;
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:229:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*
                        loop51 : do {
                            int alt51 = 2;
                            alt51 = dfa51.predict( input );
                            switch ( alt51 ) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:229:38: {...}? => LEFT_SQUARE expression RIGHT_SQUARE
                                {
                                    if ( !((!helper.validateLT( 2,
                                                                "]" ))) ) {
                                        if ( state.backtracking > 0 ) {
                                            state.failed = true;
                                            return;
                                        }
                                        throw new FailedPredicateException( input,
                                                                            "arrayCreatorRest",
                                                                            "!helper.validateLT(2,\"]\")" );
                                    }
                                    match( input,
                                           LEFT_SQUARE,
                                           FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1659 );
                                    if ( state.failed ) return;
                                    pushFollow( FOLLOW_expression_in_arrayCreatorRest1661 );
                                    expression();

                                    state._fsp--;
                                    if ( state.failed ) return;
                                    match( input,
                                           RIGHT_SQUARE,
                                           FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1663 );
                                    if ( state.failed ) return;

                                }
                                    break;

                                default :
                                    break loop51;
                            }
                        } while ( true );

                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:229:106: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                        loop52 : do {
                            int alt52 = 2;
                            int LA52_0 = input.LA( 1 );

                            if ( (LA52_0 == LEFT_SQUARE) ) {
                                int LA52_2 = input.LA( 2 );

                                if ( (LA52_2 == RIGHT_SQUARE) ) {
                                    int LA52_3 = input.LA( 3 );

                                    if ( (synpred38_DRLExpressions()) ) {
                                        alt52 = 1;
                                    }

                                }

                            }

                            switch ( alt52 ) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:229:107: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                                {
                                    match( input,
                                           LEFT_SQUARE,
                                           FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1675 );
                                    if ( state.failed ) return;
                                    match( input,
                                           RIGHT_SQUARE,
                                           FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1677 );
                                    if ( state.failed ) return;

                                }
                                    break;

                                default :
                                    break loop52;
                            }
                        } while ( true );

                    }
                        break;

                }

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "arrayCreatorRest"

    // $ANTLR start "variableInitializer"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:233:1: variableInitializer : ( arrayInitializer | expression );
    public final void variableInitializer() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:234:2: ( arrayInitializer | expression )
            int alt54 = 2;
            int LA54_0 = input.LA( 1 );

            if ( (LA54_0 == LEFT_CURLY) ) {
                alt54 = 1;
            } else if ( (LA54_0 == FLOAT || (LA54_0 >= HEX && LA54_0 <= DECIMAL) || LA54_0 == STRING || (LA54_0 >= BOOL && LA54_0 <= NULL) || (LA54_0 >= DECR && LA54_0 <= INCR) || LA54_0 == LESS || LA54_0 == LEFT_PAREN || LA54_0 == LEFT_SQUARE
                         || (LA54_0 >= NEGATION && LA54_0 <= TILDE) || (LA54_0 >= MINUS && LA54_0 <= PLUS) || LA54_0 == ID) ) {
                alt54 = 2;
            } else {
                if ( state.backtracking > 0 ) {
                    state.failed = true;
                    return;
                }
                NoViableAltException nvae =
                        new NoViableAltException( "",
                                                  54,
                                                  0,
                                                  input );

                throw nvae;
            }
            switch ( alt54 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:234:4: arrayInitializer
                {
                    pushFollow( FOLLOW_arrayInitializer_in_variableInitializer1700 );
                    arrayInitializer();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:235:10: expression
                {
                    pushFollow( FOLLOW_expression_in_variableInitializer1711 );
                    expression();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;

            }
        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "variableInitializer"

    // $ANTLR start "arrayInitializer"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:238:1: arrayInitializer : LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY ;
    public final void arrayInitializer() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:2: ( LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:4: LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY
            {
                match( input,
                       LEFT_CURLY,
                       FOLLOW_LEFT_CURLY_in_arrayInitializer1723 );
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:15: ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )?
                int alt57 = 2;
                int LA57_0 = input.LA( 1 );

                if ( (LA57_0 == FLOAT || (LA57_0 >= HEX && LA57_0 <= DECIMAL) || LA57_0 == STRING || (LA57_0 >= BOOL && LA57_0 <= NULL) || (LA57_0 >= DECR && LA57_0 <= INCR) || LA57_0 == LESS || LA57_0 == LEFT_PAREN || LA57_0 == LEFT_SQUARE
                      || LA57_0 == LEFT_CURLY || (LA57_0 >= NEGATION && LA57_0 <= TILDE) || (LA57_0 >= MINUS && LA57_0 <= PLUS) || LA57_0 == ID) ) {
                    alt57 = 1;
                }
                switch ( alt57 ) {
                    case 1 :
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:16: variableInitializer ( COMMA variableInitializer )* ( COMMA )?
                    {
                        pushFollow( FOLLOW_variableInitializer_in_arrayInitializer1726 );
                        variableInitializer();

                        state._fsp--;
                        if ( state.failed ) return;
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:36: ( COMMA variableInitializer )*
                        loop55 : do {
                            int alt55 = 2;
                            int LA55_0 = input.LA( 1 );

                            if ( (LA55_0 == COMMA) ) {
                                int LA55_1 = input.LA( 2 );

                                if ( (LA55_1 == FLOAT || (LA55_1 >= HEX && LA55_1 <= DECIMAL) || LA55_1 == STRING || (LA55_1 >= BOOL && LA55_1 <= NULL) || (LA55_1 >= DECR && LA55_1 <= INCR) || LA55_1 == LESS || LA55_1 == LEFT_PAREN
                                      || LA55_1 == LEFT_SQUARE || LA55_1 == LEFT_CURLY || (LA55_1 >= NEGATION && LA55_1 <= TILDE) || (LA55_1 >= MINUS && LA55_1 <= PLUS) || LA55_1 == ID) ) {
                                    alt55 = 1;
                                }

                            }

                            switch ( alt55 ) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:37: COMMA variableInitializer
                                {
                                    match( input,
                                           COMMA,
                                           FOLLOW_COMMA_in_arrayInitializer1729 );
                                    if ( state.failed ) return;
                                    pushFollow( FOLLOW_variableInitializer_in_arrayInitializer1731 );
                                    variableInitializer();

                                    state._fsp--;
                                    if ( state.failed ) return;

                                }
                                    break;

                                default :
                                    break loop55;
                            }
                        } while ( true );

                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:65: ( COMMA )?
                        int alt56 = 2;
                        int LA56_0 = input.LA( 1 );

                        if ( (LA56_0 == COMMA) ) {
                            alt56 = 1;
                        }
                        switch ( alt56 ) {
                            case 1 :
                                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:66: COMMA
                            {
                                match( input,
                                       COMMA,
                                       FOLLOW_COMMA_in_arrayInitializer1736 );
                                if ( state.failed ) return;

                            }
                                break;

                        }

                    }
                        break;

                }

                match( input,
                       RIGHT_CURLY,
                       FOLLOW_RIGHT_CURLY_in_arrayInitializer1743 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "arrayInitializer"

    // $ANTLR start "classCreatorRest"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:242:1: classCreatorRest : arguments ;
    public final void classCreatorRest() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:243:2: ( arguments )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:243:4: arguments
            {
                pushFollow( FOLLOW_arguments_in_classCreatorRest1754 );
                arguments();

                state._fsp--;
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "classCreatorRest"

    // $ANTLR start "explicitGenericInvocation"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:246:1: explicitGenericInvocation : nonWildcardTypeArguments arguments ;
    public final void explicitGenericInvocation() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:247:2: ( nonWildcardTypeArguments arguments )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:247:4: nonWildcardTypeArguments arguments
            {
                pushFollow( FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation1767 );
                nonWildcardTypeArguments();

                state._fsp--;
                if ( state.failed ) return;
                pushFollow( FOLLOW_arguments_in_explicitGenericInvocation1769 );
                arguments();

                state._fsp--;
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "explicitGenericInvocation"

    // $ANTLR start "nonWildcardTypeArguments"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:250:1: nonWildcardTypeArguments : LESS typeList GREATER ;
    public final void nonWildcardTypeArguments() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:251:2: ( LESS typeList GREATER )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:251:4: LESS typeList GREATER
            {
                match( input,
                       LESS,
                       FOLLOW_LESS_in_nonWildcardTypeArguments1781 );
                if ( state.failed ) return;
                pushFollow( FOLLOW_typeList_in_nonWildcardTypeArguments1783 );
                typeList();

                state._fsp--;
                if ( state.failed ) return;
                match( input,
                       GREATER,
                       FOLLOW_GREATER_in_nonWildcardTypeArguments1785 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "nonWildcardTypeArguments"

    // $ANTLR start "explicitGenericInvocationSuffix"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:254:1: explicitGenericInvocationSuffix : ( super_key superSuffix | ID arguments );
    public final void explicitGenericInvocationSuffix() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:255:2: ( super_key superSuffix | ID arguments )
            int alt58 = 2;
            int LA58_0 = input.LA( 1 );

            if ( (LA58_0 == ID) ) {
                int LA58_1 = input.LA( 2 );

                if ( (((helper.validateIdentifierKey( DroolsSoftKeywords.SUPER )))) ) {
                    alt58 = 1;
                } else if ( (true) ) {
                    alt58 = 2;
                } else {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    NoViableAltException nvae =
                            new NoViableAltException( "",
                                                      58,
                                                      1,
                                                      input );

                    throw nvae;
                }
            } else {
                if ( state.backtracking > 0 ) {
                    state.failed = true;
                    return;
                }
                NoViableAltException nvae =
                        new NoViableAltException( "",
                                                  58,
                                                  0,
                                                  input );

                throw nvae;
            }
            switch ( alt58 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:255:4: super_key superSuffix
                {
                    pushFollow( FOLLOW_super_key_in_explicitGenericInvocationSuffix1797 );
                    super_key();

                    state._fsp--;
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_superSuffix_in_explicitGenericInvocationSuffix1799 );
                    superSuffix();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:256:7: ID arguments
                {
                    match( input,
                           ID,
                           FOLLOW_ID_in_explicitGenericInvocationSuffix1807 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_arguments_in_explicitGenericInvocationSuffix1809 );
                    arguments();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;

            }
        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "explicitGenericInvocationSuffix"

    // $ANTLR start "selector"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:259:1: selector options {backtrack=true; memoize=true; } : ( DOT ID ( ( LEFT_PAREN )=> arguments )? | DOT super_key superSuffix | DOT new_key ( nonWildcardTypeArguments )? innerCreator | LEFT_SQUARE expression RIGHT_SQUARE );
    public final void selector() throws RecognitionException {
        int selector_StartIndex = input.index();
        try {
            if ( state.backtracking > 0 && alreadyParsedRule( input,
                                                              42 ) ) {
                return;
            }
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:261:2: ( DOT ID ( ( LEFT_PAREN )=> arguments )? | DOT super_key superSuffix | DOT new_key ( nonWildcardTypeArguments )? innerCreator | LEFT_SQUARE expression RIGHT_SQUARE )
            int alt61 = 4;
            int LA61_0 = input.LA( 1 );

            if ( (LA61_0 == DOT) ) {
                int LA61_1 = input.LA( 2 );

                if ( (synpred40_DRLExpressions()) ) {
                    alt61 = 1;
                } else if ( (synpred41_DRLExpressions()) ) {
                    alt61 = 2;
                } else if ( (synpred42_DRLExpressions()) ) {
                    alt61 = 3;
                } else {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    NoViableAltException nvae =
                            new NoViableAltException( "",
                                                      61,
                                                      1,
                                                      input );

                    throw nvae;
                }
            } else if ( (LA61_0 == LEFT_SQUARE) ) {
                alt61 = 4;
            } else {
                if ( state.backtracking > 0 ) {
                    state.failed = true;
                    return;
                }
                NoViableAltException nvae =
                        new NoViableAltException( "",
                                                  61,
                                                  0,
                                                  input );

                throw nvae;
            }
            switch ( alt61 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:261:6: DOT ID ( ( LEFT_PAREN )=> arguments )?
                {
                    match( input,
                           DOT,
                           FOLLOW_DOT_in_selector1836 );
                    if ( state.failed ) return;
                    match( input,
                           ID,
                           FOLLOW_ID_in_selector1838 );
                    if ( state.failed ) return;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:261:13: ( ( LEFT_PAREN )=> arguments )?
                    int alt59 = 2;
                    alt59 = dfa59.predict( input );
                    switch ( alt59 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:261:14: ( LEFT_PAREN )=> arguments
                        {
                            pushFollow( FOLLOW_arguments_in_selector1847 );
                            arguments();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                    }

                }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:263:6: DOT super_key superSuffix
                {
                    match( input,
                           DOT,
                           FOLLOW_DOT_in_selector1858 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_super_key_in_selector1860 );
                    super_key();

                    state._fsp--;
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_superSuffix_in_selector1862 );
                    superSuffix();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:264:6: DOT new_key ( nonWildcardTypeArguments )? innerCreator
                {
                    match( input,
                           DOT,
                           FOLLOW_DOT_in_selector1869 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_new_key_in_selector1871 );
                    new_key();

                    state._fsp--;
                    if ( state.failed ) return;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:264:18: ( nonWildcardTypeArguments )?
                    int alt60 = 2;
                    int LA60_0 = input.LA( 1 );

                    if ( (LA60_0 == LESS) ) {
                        alt60 = 1;
                    }
                    switch ( alt60 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:264:19: nonWildcardTypeArguments
                        {
                            pushFollow( FOLLOW_nonWildcardTypeArguments_in_selector1874 );
                            nonWildcardTypeArguments();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                    }

                    pushFollow( FOLLOW_innerCreator_in_selector1878 );
                    innerCreator();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:265:6: LEFT_SQUARE expression RIGHT_SQUARE
                {
                    match( input,
                           LEFT_SQUARE,
                           FOLLOW_LEFT_SQUARE_in_selector1885 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_expression_in_selector1887 );
                    expression();

                    state._fsp--;
                    if ( state.failed ) return;
                    match( input,
                           RIGHT_SQUARE,
                           FOLLOW_RIGHT_SQUARE_in_selector1889 );
                    if ( state.failed ) return;

                }
                    break;

            }
        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
            if ( state.backtracking > 0 ) {
                memoize( input,
                         42,
                         selector_StartIndex );
            }
        }
        return;
    }

    // $ANTLR end "selector"

    // $ANTLR start "superSuffix"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:268:1: superSuffix : ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? );
    public final void superSuffix() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:269:2: ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? )
            int alt63 = 2;
            int LA63_0 = input.LA( 1 );

            if ( (LA63_0 == LEFT_PAREN) ) {
                alt63 = 1;
            } else if ( (LA63_0 == DOT) ) {
                alt63 = 2;
            } else {
                if ( state.backtracking > 0 ) {
                    state.failed = true;
                    return;
                }
                NoViableAltException nvae =
                        new NoViableAltException( "",
                                                  63,
                                                  0,
                                                  input );

                throw nvae;
            }
            switch ( alt63 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:269:4: arguments
                {
                    pushFollow( FOLLOW_arguments_in_superSuffix1901 );
                    arguments();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:270:7: DOT ID ( ( LEFT_PAREN )=> arguments )?
                {
                    match( input,
                           DOT,
                           FOLLOW_DOT_in_superSuffix1909 );
                    if ( state.failed ) return;
                    match( input,
                           ID,
                           FOLLOW_ID_in_superSuffix1911 );
                    if ( state.failed ) return;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:270:14: ( ( LEFT_PAREN )=> arguments )?
                    int alt62 = 2;
                    alt62 = dfa62.predict( input );
                    switch ( alt62 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:270:15: ( LEFT_PAREN )=> arguments
                        {
                            pushFollow( FOLLOW_arguments_in_superSuffix1920 );
                            arguments();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                    }

                }
                    break;

            }
        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "superSuffix"

    // $ANTLR start "arguments"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:273:1: arguments options {backtrack=true; memoize=true; } : LEFT_PAREN ( expressionList )? RIGHT_PAREN ;
    public final void arguments() throws RecognitionException {
        int arguments_StartIndex = input.index();
        try {
            if ( state.backtracking > 0 && alreadyParsedRule( input,
                                                              44 ) ) {
                return;
            }
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:275:2: ( LEFT_PAREN ( expressionList )? RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:275:4: LEFT_PAREN ( expressionList )? RIGHT_PAREN
            {
                match( input,
                       LEFT_PAREN,
                       FOLLOW_LEFT_PAREN_in_arguments1954 );
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:275:15: ( expressionList )?
                int alt64 = 2;
                int LA64_0 = input.LA( 1 );

                if ( (LA64_0 == FLOAT || (LA64_0 >= HEX && LA64_0 <= DECIMAL) || LA64_0 == STRING || (LA64_0 >= BOOL && LA64_0 <= NULL) || (LA64_0 >= DECR && LA64_0 <= INCR) || LA64_0 == LESS || LA64_0 == LEFT_PAREN || LA64_0 == LEFT_SQUARE
                      || (LA64_0 >= NEGATION && LA64_0 <= TILDE) || (LA64_0 >= MINUS && LA64_0 <= PLUS) || LA64_0 == ID) ) {
                    alt64 = 1;
                }
                switch ( alt64 ) {
                    case 1 :
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:275:15: expressionList
                    {
                        pushFollow( FOLLOW_expressionList_in_arguments1956 );
                        expressionList();

                        state._fsp--;
                        if ( state.failed ) return;

                    }
                        break;

                }

                match( input,
                       RIGHT_PAREN,
                       FOLLOW_RIGHT_PAREN_in_arguments1959 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
            if ( state.backtracking > 0 ) {
                memoize( input,
                         44,
                         arguments_StartIndex );
            }
        }
        return;
    }

    // $ANTLR end "arguments"

    // $ANTLR start "expressionList"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:278:1: expressionList : expression ( COMMA expression )* ;
    public final void expressionList() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:279:5: ( expression ( COMMA expression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:279:9: expression ( COMMA expression )*
            {
                pushFollow( FOLLOW_expression_in_expressionList1975 );
                expression();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:279:20: ( COMMA expression )*
                loop65 : do {
                    int alt65 = 2;
                    int LA65_0 = input.LA( 1 );

                    if ( (LA65_0 == COMMA) ) {
                        alt65 = 1;
                    }

                    switch ( alt65 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:279:21: COMMA expression
                        {
                            match( input,
                                   COMMA,
                                   FOLLOW_COMMA_in_expressionList1978 );
                            if ( state.failed ) return;
                            pushFollow( FOLLOW_expression_in_expressionList1980 );
                            expression();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                        default :
                            break loop65;
                    }
                } while ( true );

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "expressionList"

    // $ANTLR start "assignmentOperator"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:282:1: assignmentOperator options {k=1; } : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );
    public final void assignmentOperator() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:284:2: ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN )
            int alt66 = 12;
            alt66 = dfa66.predict( input );
            switch ( alt66 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:284:6: EQUALS_ASSIGN
                {
                    match( input,
                           EQUALS_ASSIGN,
                           FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2007 );
                    if ( state.failed ) return;

                }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:285:13: PLUS_ASSIGN
                {
                    match( input,
                           PLUS_ASSIGN,
                           FOLLOW_PLUS_ASSIGN_in_assignmentOperator2021 );
                    if ( state.failed ) return;

                }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:286:13: MINUS_ASSIGN
                {
                    match( input,
                           MINUS_ASSIGN,
                           FOLLOW_MINUS_ASSIGN_in_assignmentOperator2035 );
                    if ( state.failed ) return;

                }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:287:13: MULT_ASSIGN
                {
                    match( input,
                           MULT_ASSIGN,
                           FOLLOW_MULT_ASSIGN_in_assignmentOperator2049 );
                    if ( state.failed ) return;

                }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:288:13: DIV_ASSIGN
                {
                    match( input,
                           DIV_ASSIGN,
                           FOLLOW_DIV_ASSIGN_in_assignmentOperator2063 );
                    if ( state.failed ) return;

                }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:289:13: AND_ASSIGN
                {
                    match( input,
                           AND_ASSIGN,
                           FOLLOW_AND_ASSIGN_in_assignmentOperator2077 );
                    if ( state.failed ) return;

                }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:290:13: OR_ASSIGN
                {
                    match( input,
                           OR_ASSIGN,
                           FOLLOW_OR_ASSIGN_in_assignmentOperator2091 );
                    if ( state.failed ) return;

                }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:291:13: XOR_ASSIGN
                {
                    match( input,
                           XOR_ASSIGN,
                           FOLLOW_XOR_ASSIGN_in_assignmentOperator2105 );
                    if ( state.failed ) return;

                }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:292:13: MOD_ASSIGN
                {
                    match( input,
                           MOD_ASSIGN,
                           FOLLOW_MOD_ASSIGN_in_assignmentOperator2119 );
                    if ( state.failed ) return;

                }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:293:13: LESS LESS EQUALS_ASSIGN
                {
                    match( input,
                           LESS,
                           FOLLOW_LESS_in_assignmentOperator2133 );
                    if ( state.failed ) return;
                    match( input,
                           LESS,
                           FOLLOW_LESS_in_assignmentOperator2135 );
                    if ( state.failed ) return;
                    match( input,
                           EQUALS_ASSIGN,
                           FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2137 );
                    if ( state.failed ) return;

                }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:294:13: ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN
                {
                    match( input,
                           GREATER,
                           FOLLOW_GREATER_in_assignmentOperator2160 );
                    if ( state.failed ) return;
                    match( input,
                           GREATER,
                           FOLLOW_GREATER_in_assignmentOperator2162 );
                    if ( state.failed ) return;
                    match( input,
                           GREATER,
                           FOLLOW_GREATER_in_assignmentOperator2164 );
                    if ( state.failed ) return;
                    match( input,
                           EQUALS_ASSIGN,
                           FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2166 );
                    if ( state.failed ) return;

                }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:295:13: ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN
                {
                    match( input,
                           GREATER,
                           FOLLOW_GREATER_in_assignmentOperator2187 );
                    if ( state.failed ) return;
                    match( input,
                           GREATER,
                           FOLLOW_GREATER_in_assignmentOperator2189 );
                    if ( state.failed ) return;
                    match( input,
                           EQUALS_ASSIGN,
                           FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2191 );
                    if ( state.failed ) return;

                }
                    break;

            }
        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "assignmentOperator"

    // $ANTLR start "annotations"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:302:1: annotations : ( annotation )+ ;
    public final void annotations() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:303:2: ( ( annotation )+ )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:303:4: ( annotation )+
            {
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:303:4: ( annotation )+
                int cnt67 = 0;
                loop67 : do {
                    int alt67 = 2;
                    int LA67_0 = input.LA( 1 );

                    if ( (LA67_0 == AT) ) {
                        alt67 = 1;
                    }

                    switch ( alt67 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:303:4: annotation
                        {
                            pushFollow( FOLLOW_annotation_in_annotations2206 );
                            annotation();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                        default :
                            if ( cnt67 >= 1 ) break loop67;
                            if ( state.backtracking > 0 ) {
                                state.failed = true;
                                return;
                            }
                            EarlyExitException eee =
                                    new EarlyExitException( 67,
                                                            input );
                            throw eee;
                    }
                    cnt67++;
                } while ( true );

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "annotations"

    // $ANTLR start "annotation"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:306:1: annotation : AT ann= annotationName ( LEFT_PAREN RIGHT_PAREN | LEFT_PAREN elementValuePairs RIGHT_PAREN | ) ;
    public final void annotation() throws RecognitionException {
        Token AT7 = null;
        String ann = null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:307:2: ( AT ann= annotationName ( LEFT_PAREN RIGHT_PAREN | LEFT_PAREN elementValuePairs RIGHT_PAREN | ) )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:307:4: AT ann= annotationName ( LEFT_PAREN RIGHT_PAREN | LEFT_PAREN elementValuePairs RIGHT_PAREN | )
            {
                AT7 = (Token) match( input,
                                     AT,
                                     FOLLOW_AT_in_annotation2218 );
                if ( state.failed ) return;
                if ( state.backtracking == 0 ) {
                    helper.emit( AT7,
                                 DroolsEditorType.SYMBOL );
                }
                pushFollow( FOLLOW_annotationName_in_annotation2226 );
                ann = annotationName();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:309:4: ( LEFT_PAREN RIGHT_PAREN | LEFT_PAREN elementValuePairs RIGHT_PAREN | )
                int alt68 = 3;
                int LA68_0 = input.LA( 1 );

                if ( (LA68_0 == LEFT_PAREN) ) {
                    int LA68_1 = input.LA( 2 );

                    if ( (LA68_1 == RIGHT_PAREN) ) {
                        alt68 = 1;
                    } else if ( (LA68_1 == FLOAT || (LA68_1 >= HEX && LA68_1 <= DECIMAL) || (LA68_1 >= STRING && LA68_1 <= TimePeriod) || (LA68_1 >= BOOL && LA68_1 <= AT) || (LA68_1 >= DECR && LA68_1 <= INCR) || LA68_1 == LESS
                                 || LA68_1 == LEFT_PAREN || LA68_1 == LEFT_SQUARE || LA68_1 == LEFT_CURLY || (LA68_1 >= NEGATION && LA68_1 <= TILDE) || (LA68_1 >= MINUS && LA68_1 <= PLUS) || LA68_1 == ID) ) {
                        alt68 = 2;
                    } else {
                        if ( state.backtracking > 0 ) {
                            state.failed = true;
                            return;
                        }
                        NoViableAltException nvae =
                                new NoViableAltException( "",
                                                          68,
                                                          1,
                                                          input );

                        throw nvae;
                    }
                } else if ( (LA68_0 == EOF || LA68_0 == AT || LA68_0 == RIGHT_PAREN || (LA68_0 >= RIGHT_CURLY && LA68_0 <= COMMA)) ) {
                    alt68 = 3;
                } else {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    NoViableAltException nvae =
                            new NoViableAltException( "",
                                                      68,
                                                      0,
                                                      input );

                    throw nvae;
                }
                switch ( alt68 ) {
                    case 1 :
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:310:5: LEFT_PAREN RIGHT_PAREN
                    {
                        match( input,
                               LEFT_PAREN,
                               FOLLOW_LEFT_PAREN_in_annotation2238 );
                        if ( state.failed ) return;
                        match( input,
                               RIGHT_PAREN,
                               FOLLOW_RIGHT_PAREN_in_annotation2240 );
                        if ( state.failed ) return;

                    }
                        break;
                    case 2 :
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:311:7: LEFT_PAREN elementValuePairs RIGHT_PAREN
                    {
                        match( input,
                               LEFT_PAREN,
                               FOLLOW_LEFT_PAREN_in_annotation2248 );
                        if ( state.failed ) return;
                        pushFollow( FOLLOW_elementValuePairs_in_annotation2250 );
                        elementValuePairs();

                        state._fsp--;
                        if ( state.failed ) return;
                        match( input,
                               RIGHT_PAREN,
                               FOLLOW_RIGHT_PAREN_in_annotation2252 );
                        if ( state.failed ) return;

                    }
                        break;
                    case 3 :
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:313:4: 
                    {
                    }
                        break;

                }

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "annotation"

    // $ANTLR start "annotationName"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:317:1: annotationName returns [String name] : id= ID ( DOT mid= ID )* ;
    public final String annotationName() throws RecognitionException {
        String name = null;

        Token id = null;
        Token mid = null;

        name = "";
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:319:2: (id= ID ( DOT mid= ID )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:319:4: id= ID ( DOT mid= ID )*
            {
                id = (Token) match( input,
                                    ID,
                                    FOLLOW_ID_in_annotationName2287 );
                if ( state.failed ) return name;
                if ( state.backtracking == 0 ) {
                    name += (id != null ? id.getText() : null);
                    helper.emit( id,
                                 DroolsEditorType.IDENTIFIER );
                }
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:320:3: ( DOT mid= ID )*
                loop69 : do {
                    int alt69 = 2;
                    int LA69_0 = input.LA( 1 );

                    if ( (LA69_0 == DOT) ) {
                        alt69 = 1;
                    }

                    switch ( alt69 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:320:4: DOT mid= ID
                        {
                            match( input,
                                   DOT,
                                   FOLLOW_DOT_in_annotationName2295 );
                            if ( state.failed ) return name;
                            mid = (Token) match( input,
                                                 ID,
                                                 FOLLOW_ID_in_annotationName2299 );
                            if ( state.failed ) return name;
                            if ( state.backtracking == 0 ) {
                                name += (mid != null ? mid.getText() : null);
                            }

                        }
                            break;

                        default :
                            break loop69;
                    }
                } while ( true );

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return name;
    }

    // $ANTLR end "annotationName"

    // $ANTLR start "elementValuePairs"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:323:1: elementValuePairs : elementValuePair ( COMMA elementValuePair )* ;
    public final void elementValuePairs() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:324:2: ( elementValuePair ( COMMA elementValuePair )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:324:4: elementValuePair ( COMMA elementValuePair )*
            {
                pushFollow( FOLLOW_elementValuePair_in_elementValuePairs2316 );
                elementValuePair();

                state._fsp--;
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:324:21: ( COMMA elementValuePair )*
                loop70 : do {
                    int alt70 = 2;
                    int LA70_0 = input.LA( 1 );

                    if ( (LA70_0 == COMMA) ) {
                        alt70 = 1;
                    }

                    switch ( alt70 ) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:324:22: COMMA elementValuePair
                        {
                            match( input,
                                   COMMA,
                                   FOLLOW_COMMA_in_elementValuePairs2319 );
                            if ( state.failed ) return;
                            pushFollow( FOLLOW_elementValuePair_in_elementValuePairs2321 );
                            elementValuePair();

                            state._fsp--;
                            if ( state.failed ) return;

                        }
                            break;

                        default :
                            break loop70;
                    }
                } while ( true );

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "elementValuePairs"

    // $ANTLR start "elementValuePair"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:327:1: elementValuePair : ( ( ID EQUALS_ASSIGN )=>key= ID EQUALS_ASSIGN val= elementValue | value= elementValue );
    public final void elementValuePair() throws RecognitionException {
        Token key = null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:328:2: ( ( ID EQUALS_ASSIGN )=>key= ID EQUALS_ASSIGN val= elementValue | value= elementValue )
            int alt71 = 2;
            int LA71_0 = input.LA( 1 );

            if ( (LA71_0 == ID) ) {
                int LA71_1 = input.LA( 2 );

                if ( (LA71_1 == EQUALS_ASSIGN) && (synpred46_DRLExpressions()) ) {
                    alt71 = 1;
                } else if ( ((LA71_1 >= DECR && LA71_1 <= INCR) || (LA71_1 >= EQUALS && LA71_1 <= LESS) || (LA71_1 >= LEFT_PAREN && LA71_1 <= LEFT_SQUARE) || (LA71_1 >= COMMA && LA71_1 <= QUESTION) || (LA71_1 >= PIPE && LA71_1 <= PLUS) || (LA71_1 >= ID && LA71_1 <= DIV)) ) {
                    alt71 = 2;
                } else {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    NoViableAltException nvae =
                            new NoViableAltException( "",
                                                      71,
                                                      1,
                                                      input );

                    throw nvae;
                }
            } else if ( (LA71_0 == FLOAT || (LA71_0 >= HEX && LA71_0 <= DECIMAL) || (LA71_0 >= STRING && LA71_0 <= TimePeriod) || (LA71_0 >= BOOL && LA71_0 <= AT) || (LA71_0 >= DECR && LA71_0 <= INCR) || LA71_0 == LESS || LA71_0 == LEFT_PAREN
                         || LA71_0 == LEFT_SQUARE || LA71_0 == LEFT_CURLY || (LA71_0 >= NEGATION && LA71_0 <= TILDE) || (LA71_0 >= MINUS && LA71_0 <= PLUS)) ) {
                alt71 = 2;
            } else {
                if ( state.backtracking > 0 ) {
                    state.failed = true;
                    return;
                }
                NoViableAltException nvae =
                        new NoViableAltException( "",
                                                  71,
                                                  0,
                                                  input );

                throw nvae;
            }
            switch ( alt71 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:328:4: ( ID EQUALS_ASSIGN )=>key= ID EQUALS_ASSIGN val= elementValue
                {
                    key = (Token) match( input,
                                         ID,
                                         FOLLOW_ID_in_elementValuePair2344 );
                    if ( state.failed ) return;
                    match( input,
                           EQUALS_ASSIGN,
                           FOLLOW_EQUALS_ASSIGN_in_elementValuePair2346 );
                    if ( state.failed ) return;
                    pushFollow( FOLLOW_elementValue_in_elementValuePair2350 );
                    elementValue();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:329:4: value= elementValue
                {
                    pushFollow( FOLLOW_elementValue_in_elementValuePair2358 );
                    elementValue();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;

            }
        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "elementValuePair"

    // $ANTLR start "elementValue"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:332:1: elementValue : ( TimePeriod | conditionalExpression | annotation | elementValueArrayInitializer );
    public final void elementValue() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:333:2: ( TimePeriod | conditionalExpression | annotation | elementValueArrayInitializer )
            int alt72 = 4;
            switch ( input.LA( 1 ) ) {
                case TimePeriod : {
                    alt72 = 1;
                }
                    break;
                case FLOAT :
                case HEX :
                case DECIMAL :
                case STRING :
                case BOOL :
                case NULL :
                case DECR :
                case INCR :
                case LESS :
                case LEFT_PAREN :
                case LEFT_SQUARE :
                case NEGATION :
                case TILDE :
                case MINUS :
                case PLUS :
                case ID : {
                    alt72 = 2;
                }
                    break;
                case AT : {
                    alt72 = 3;
                }
                    break;
                case LEFT_CURLY : {
                    alt72 = 4;
                }
                    break;
                default :
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    NoViableAltException nvae =
                            new NoViableAltException( "",
                                                      72,
                                                      0,
                                                      input );

                    throw nvae;
            }

            switch ( alt72 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:333:4: TimePeriod
                {
                    match( input,
                           TimePeriod,
                           FOLLOW_TimePeriod_in_elementValue2371 );
                    if ( state.failed ) return;

                }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:334:4: conditionalExpression
                {
                    pushFollow( FOLLOW_conditionalExpression_in_elementValue2376 );
                    conditionalExpression();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:335:6: annotation
                {
                    pushFollow( FOLLOW_annotation_in_elementValue2383 );
                    annotation();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:336:6: elementValueArrayInitializer
                {
                    pushFollow( FOLLOW_elementValueArrayInitializer_in_elementValue2390 );
                    elementValueArrayInitializer();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;

            }
        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "elementValue"

    // $ANTLR start "elementValueArrayInitializer"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:339:1: elementValueArrayInitializer : LEFT_CURLY ( elementValue ( COMMA elementValue )* )? RIGHT_CURLY ;
    public final void elementValueArrayInitializer() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:340:2: ( LEFT_CURLY ( elementValue ( COMMA elementValue )* )? RIGHT_CURLY )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:340:4: LEFT_CURLY ( elementValue ( COMMA elementValue )* )? RIGHT_CURLY
            {
                match( input,
                       LEFT_CURLY,
                       FOLLOW_LEFT_CURLY_in_elementValueArrayInitializer2401 );
                if ( state.failed ) return;
                // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:340:15: ( elementValue ( COMMA elementValue )* )?
                int alt74 = 2;
                int LA74_0 = input.LA( 1 );

                if ( (LA74_0 == FLOAT || (LA74_0 >= HEX && LA74_0 <= DECIMAL) || (LA74_0 >= STRING && LA74_0 <= TimePeriod) || (LA74_0 >= BOOL && LA74_0 <= AT) || (LA74_0 >= DECR && LA74_0 <= INCR) || LA74_0 == LESS || LA74_0 == LEFT_PAREN
                      || LA74_0 == LEFT_SQUARE || LA74_0 == LEFT_CURLY || (LA74_0 >= NEGATION && LA74_0 <= TILDE) || (LA74_0 >= MINUS && LA74_0 <= PLUS) || LA74_0 == ID) ) {
                    alt74 = 1;
                }
                switch ( alt74 ) {
                    case 1 :
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:340:16: elementValue ( COMMA elementValue )*
                    {
                        pushFollow( FOLLOW_elementValue_in_elementValueArrayInitializer2404 );
                        elementValue();

                        state._fsp--;
                        if ( state.failed ) return;
                        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:340:29: ( COMMA elementValue )*
                        loop73 : do {
                            int alt73 = 2;
                            int LA73_0 = input.LA( 1 );

                            if ( (LA73_0 == COMMA) ) {
                                alt73 = 1;
                            }

                            switch ( alt73 ) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:340:30: COMMA elementValue
                                {
                                    match( input,
                                           COMMA,
                                           FOLLOW_COMMA_in_elementValueArrayInitializer2407 );
                                    if ( state.failed ) return;
                                    pushFollow( FOLLOW_elementValue_in_elementValueArrayInitializer2409 );
                                    elementValue();

                                    state._fsp--;
                                    if ( state.failed ) return;

                                }
                                    break;

                                default :
                                    break loop73;
                            }
                        } while ( true );

                    }
                        break;

                }

                match( input,
                       RIGHT_CURLY,
                       FOLLOW_RIGHT_CURLY_in_elementValueArrayInitializer2416 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "elementValueArrayInitializer"

    // $ANTLR start "extends_key"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:346:1: extends_key : {...}? =>id= ID ;
    public final void extends_key() throws RecognitionException {
        Token id = null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:347:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:347:9: {...}? =>id= ID
            {
                if ( !(((helper.validateIdentifierKey( DroolsSoftKeywords.EXTENDS )))) ) {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    throw new FailedPredicateException( input,
                                                        "extends_key",
                                                        "(helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS))" );
                }
                id = (Token) match( input,
                                    ID,
                                    FOLLOW_ID_in_extends_key2440 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "extends_key"

    // $ANTLR start "super_key"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:350:1: super_key : {...}? =>id= ID ;
    public final void super_key() throws RecognitionException {
        Token id = null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:351:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:351:9: {...}? =>id= ID
            {
                if ( !(((helper.validateIdentifierKey( DroolsSoftKeywords.SUPER )))) ) {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    throw new FailedPredicateException( input,
                                                        "super_key",
                                                        "(helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))" );
                }
                id = (Token) match( input,
                                    ID,
                                    FOLLOW_ID_in_super_key2461 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "super_key"

    // $ANTLR start "instanceof_key"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:354:1: instanceof_key : {...}? =>id= ID ;
    public final void instanceof_key() throws RecognitionException {
        Token id = null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:355:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:355:9: {...}? =>id= ID
            {
                if ( !(((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))) ) {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    throw new FailedPredicateException( input,
                                                        "instanceof_key",
                                                        "(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))" );
                }
                id = (Token) match( input,
                                    ID,
                                    FOLLOW_ID_in_instanceof_key2482 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "instanceof_key"

    // $ANTLR start "boolean_key"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:358:1: boolean_key : {...}? =>id= ID ;
    public final void boolean_key() throws RecognitionException {
        Token id = null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:359:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:359:9: {...}? =>id= ID
            {
                if ( !(((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))) ) {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    throw new FailedPredicateException( input,
                                                        "boolean_key",
                                                        "(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))" );
                }
                id = (Token) match( input,
                                    ID,
                                    FOLLOW_ID_in_boolean_key2503 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "boolean_key"

    // $ANTLR start "char_key"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:362:1: char_key : {...}? =>id= ID ;
    public final void char_key() throws RecognitionException {
        Token id = null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:363:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:363:9: {...}? =>id= ID
            {
                if ( !(((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR )))) ) {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    throw new FailedPredicateException( input,
                                                        "char_key",
                                                        "(helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))" );
                }
                id = (Token) match( input,
                                    ID,
                                    FOLLOW_ID_in_char_key2524 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "char_key"

    // $ANTLR start "byte_key"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:366:1: byte_key : {...}? =>id= ID ;
    public final void byte_key() throws RecognitionException {
        Token id = null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:367:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:367:9: {...}? =>id= ID
            {
                if ( !(((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE )))) ) {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    throw new FailedPredicateException( input,
                                                        "byte_key",
                                                        "(helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))" );
                }
                id = (Token) match( input,
                                    ID,
                                    FOLLOW_ID_in_byte_key2545 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "byte_key"

    // $ANTLR start "short_key"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:370:1: short_key : {...}? =>id= ID ;
    public final void short_key() throws RecognitionException {
        Token id = null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:371:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:371:9: {...}? =>id= ID
            {
                if ( !(((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT )))) ) {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    throw new FailedPredicateException( input,
                                                        "short_key",
                                                        "(helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))" );
                }
                id = (Token) match( input,
                                    ID,
                                    FOLLOW_ID_in_short_key2566 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "short_key"

    // $ANTLR start "int_key"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:374:1: int_key : {...}? =>id= ID ;
    public final void int_key() throws RecognitionException {
        Token id = null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:375:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:375:9: {...}? =>id= ID
            {
                if ( !(((helper.validateIdentifierKey( DroolsSoftKeywords.INT )))) ) {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    throw new FailedPredicateException( input,
                                                        "int_key",
                                                        "(helper.validateIdentifierKey(DroolsSoftKeywords.INT))" );
                }
                id = (Token) match( input,
                                    ID,
                                    FOLLOW_ID_in_int_key2587 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "int_key"

    // $ANTLR start "float_key"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:378:1: float_key : {...}? =>id= ID ;
    public final void float_key() throws RecognitionException {
        Token id = null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:379:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:379:9: {...}? =>id= ID
            {
                if ( !(((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT )))) ) {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    throw new FailedPredicateException( input,
                                                        "float_key",
                                                        "(helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))" );
                }
                id = (Token) match( input,
                                    ID,
                                    FOLLOW_ID_in_float_key2608 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "float_key"

    // $ANTLR start "long_key"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:382:1: long_key : {...}? =>id= ID ;
    public final void long_key() throws RecognitionException {
        Token id = null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:383:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:383:9: {...}? =>id= ID
            {
                if ( !(((helper.validateIdentifierKey( DroolsSoftKeywords.LONG )))) ) {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    throw new FailedPredicateException( input,
                                                        "long_key",
                                                        "(helper.validateIdentifierKey(DroolsSoftKeywords.LONG))" );
                }
                id = (Token) match( input,
                                    ID,
                                    FOLLOW_ID_in_long_key2629 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "long_key"

    // $ANTLR start "double_key"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:386:1: double_key : {...}? =>id= ID ;
    public final void double_key() throws RecognitionException {
        Token id = null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:387:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:387:9: {...}? =>id= ID
            {
                if ( !(((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))) ) {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    throw new FailedPredicateException( input,
                                                        "double_key",
                                                        "(helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))" );
                }
                id = (Token) match( input,
                                    ID,
                                    FOLLOW_ID_in_double_key2650 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "double_key"

    // $ANTLR start "this_key"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:390:1: this_key : {...}? =>id= ID ;
    public final void this_key() throws RecognitionException {
        Token id = null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:391:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:391:9: {...}? =>id= ID
            {
                if ( !(((helper.validateIdentifierKey( DroolsSoftKeywords.THIS )))) ) {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    throw new FailedPredicateException( input,
                                                        "this_key",
                                                        "(helper.validateIdentifierKey(DroolsSoftKeywords.THIS))" );
                }
                id = (Token) match( input,
                                    ID,
                                    FOLLOW_ID_in_this_key2671 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "this_key"

    // $ANTLR start "class_key"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:394:1: class_key : {...}? =>id= ID ;
    public final void class_key() throws RecognitionException {
        Token id = null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:395:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:395:9: {...}? =>id= ID
            {
                if ( !(((helper.validateIdentifierKey( DroolsSoftKeywords.CLASS )))) ) {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    throw new FailedPredicateException( input,
                                                        "class_key",
                                                        "(helper.validateIdentifierKey(DroolsSoftKeywords.CLASS))" );
                }
                id = (Token) match( input,
                                    ID,
                                    FOLLOW_ID_in_class_key2692 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "class_key"

    // $ANTLR start "new_key"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:398:1: new_key : {...}? =>id= ID ;
    public final void new_key() throws RecognitionException {
        Token id = null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:399:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:399:9: {...}? =>id= ID
            {
                if ( !(((helper.validateIdentifierKey( DroolsSoftKeywords.NEW )))) ) {
                    if ( state.backtracking > 0 ) {
                        state.failed = true;
                        return;
                    }
                    throw new FailedPredicateException( input,
                                                        "new_key",
                                                        "(helper.validateIdentifierKey(DroolsSoftKeywords.NEW))" );
                }
                id = (Token) match( input,
                                    ID,
                                    FOLLOW_ID_in_new_key2713 );
                if ( state.failed ) return;

            }

        } catch ( RecognitionException re ) {
            reportError( re );
            recover( input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end "new_key"

    // $ANTLR start synpred1_DRLExpressions
    public final void synpred1_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:53:5: ( primitiveType )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:53:6: primitiveType
        {
            pushFollow( FOLLOW_primitiveType_in_synpred1_DRLExpressions213 );
            primitiveType();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred1_DRLExpressions

    // $ANTLR start synpred2_DRLExpressions
    public final void synpred2_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:53:41: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:53:42: LEFT_SQUARE RIGHT_SQUARE
        {
            match( input,
                   LEFT_SQUARE,
                   FOLLOW_LEFT_SQUARE_in_synpred2_DRLExpressions224 );
            if ( state.failed ) return;
            match( input,
                   RIGHT_SQUARE,
                   FOLLOW_RIGHT_SQUARE_in_synpred2_DRLExpressions226 );
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred2_DRLExpressions

    // $ANTLR start synpred3_DRLExpressions
    public final void synpred3_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:54:10: ( typeArguments )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:54:11: typeArguments
        {
            pushFollow( FOLLOW_typeArguments_in_synpred3_DRLExpressions247 );
            typeArguments();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred3_DRLExpressions

    // $ANTLR start synpred4_DRLExpressions
    public final void synpred4_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:54:52: ( typeArguments )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:54:53: typeArguments
        {
            pushFollow( FOLLOW_typeArguments_in_synpred4_DRLExpressions261 );
            typeArguments();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred4_DRLExpressions

    // $ANTLR start synpred5_DRLExpressions
    public final void synpred5_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:54:89: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:54:90: LEFT_SQUARE RIGHT_SQUARE
        {
            match( input,
                   LEFT_SQUARE,
                   FOLLOW_LEFT_SQUARE_in_synpred5_DRLExpressions273 );
            if ( state.failed ) return;
            match( input,
                   RIGHT_SQUARE,
                   FOLLOW_RIGHT_SQUARE_in_synpred5_DRLExpressions275 );
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred5_DRLExpressions

    // $ANTLR start synpred6_DRLExpressions
    public final void synpred6_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:27: ( assignmentOperator )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:28: assignmentOperator
        {
            pushFollow( FOLLOW_assignmentOperator_in_synpred6_DRLExpressions369 );
            assignmentOperator();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred6_DRLExpressions

    // $ANTLR start synpred7_DRLExpressions
    public final void synpred7_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:27: ( LESS )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:28: LESS
        {
            match( input,
                   LESS,
                   FOLLOW_LESS_in_synpred7_DRLExpressions616 );
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred7_DRLExpressions

    // $ANTLR start synpred8_DRLExpressions
    public final void synpred8_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:30: ( shiftOp )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:31: shiftOp
        {
            pushFollow( FOLLOW_shiftOp_in_synpred8_DRLExpressions674 );
            shiftOp();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred8_DRLExpressions

    // $ANTLR start synpred9_DRLExpressions
    public final void synpred9_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:36: ( PLUS | MINUS )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:
        {
            if ( (input.LA( 1 ) >= MINUS && input.LA( 1 ) <= PLUS) ) {
                input.consume();
                state.errorRecovery = false;
                state.failed = false;
            } else {
                if ( state.backtracking > 0 ) {
                    state.failed = true;
                    return;
                }
                MismatchedSetException mse = new MismatchedSetException( null,
                                                                         input );
                throw mse;
            }

        }
    }

    // $ANTLR end synpred9_DRLExpressions

    // $ANTLR start synpred12_DRLExpressions
    public final void synpred12_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:141:9: ( castExpression )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:141:9: castExpression
        {
            pushFollow( FOLLOW_castExpression_in_synpred12_DRLExpressions906 );
            castExpression();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred12_DRLExpressions

    // $ANTLR start synpred13_DRLExpressions
    public final void synpred13_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:18: ( selector )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:19: selector
        {
            pushFollow( FOLLOW_selector_in_synpred13_DRLExpressions920 );
            selector();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred13_DRLExpressions

    // $ANTLR start synpred14_DRLExpressions
    public final void synpred14_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:42: ( INCR | DECR )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:
        {
            if ( (input.LA( 1 ) >= DECR && input.LA( 1 ) <= INCR) ) {
                input.consume();
                state.errorRecovery = false;
                state.failed = false;
            } else {
                if ( state.backtracking > 0 ) {
                    state.failed = true;
                    return;
                }
                MismatchedSetException mse = new MismatchedSetException( null,
                                                                         input );
                throw mse;
            }

        }
    }

    // $ANTLR end synpred14_DRLExpressions

    // $ANTLR start synpred15_DRLExpressions
    public final void synpred15_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:147:8: ( LEFT_PAREN primitiveType )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:147:9: LEFT_PAREN primitiveType
        {
            match( input,
                   LEFT_PAREN,
                   FOLLOW_LEFT_PAREN_in_synpred15_DRLExpressions978 );
            if ( state.failed ) return;
            pushFollow( FOLLOW_primitiveType_in_synpred15_DRLExpressions980 );
            primitiveType();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred15_DRLExpressions

    // $ANTLR start synpred16_DRLExpressions
    public final void synpred16_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:148:8: ( LEFT_PAREN type )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:148:9: LEFT_PAREN type
        {
            match( input,
                   LEFT_PAREN,
                   FOLLOW_LEFT_PAREN_in_synpred16_DRLExpressions1001 );
            if ( state.failed ) return;
            pushFollow( FOLLOW_type_in_synpred16_DRLExpressions1003 );
            type();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred16_DRLExpressions

    // $ANTLR start synpred17_DRLExpressions
    public final void synpred17_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:154:9: ( boolean_key )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:154:9: boolean_key
        {
            pushFollow( FOLLOW_boolean_key_in_synpred17_DRLExpressions1066 );
            boolean_key();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred17_DRLExpressions

    // $ANTLR start synpred18_DRLExpressions
    public final void synpred18_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:155:7: ( char_key )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:155:7: char_key
        {
            pushFollow( FOLLOW_char_key_in_synpred18_DRLExpressions1074 );
            char_key();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred18_DRLExpressions

    // $ANTLR start synpred19_DRLExpressions
    public final void synpred19_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:156:7: ( byte_key )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:156:7: byte_key
        {
            pushFollow( FOLLOW_byte_key_in_synpred19_DRLExpressions1082 );
            byte_key();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred19_DRLExpressions

    // $ANTLR start synpred20_DRLExpressions
    public final void synpred20_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:157:7: ( short_key )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:157:7: short_key
        {
            pushFollow( FOLLOW_short_key_in_synpred20_DRLExpressions1090 );
            short_key();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred20_DRLExpressions

    // $ANTLR start synpred21_DRLExpressions
    public final void synpred21_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:158:7: ( int_key )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:158:7: int_key
        {
            pushFollow( FOLLOW_int_key_in_synpred21_DRLExpressions1098 );
            int_key();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred21_DRLExpressions

    // $ANTLR start synpred22_DRLExpressions
    public final void synpred22_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:159:7: ( long_key )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:159:7: long_key
        {
            pushFollow( FOLLOW_long_key_in_synpred22_DRLExpressions1106 );
            long_key();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred22_DRLExpressions

    // $ANTLR start synpred23_DRLExpressions
    public final void synpred23_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:160:7: ( float_key )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:160:7: float_key
        {
            pushFollow( FOLLOW_float_key_in_synpred23_DRLExpressions1114 );
            float_key();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred23_DRLExpressions

    // $ANTLR start synpred24_DRLExpressions
    public final void synpred24_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:166:7: ( parExpression )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:166:8: parExpression
        {
            pushFollow( FOLLOW_parExpression_in_synpred24_DRLExpressions1141 );
            parExpression();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred24_DRLExpressions

    // $ANTLR start synpred25_DRLExpressions
    public final void synpred25_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:167:9: ( nonWildcardTypeArguments )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:167:10: nonWildcardTypeArguments
        {
            pushFollow( FOLLOW_nonWildcardTypeArguments_in_synpred25_DRLExpressions1156 );
            nonWildcardTypeArguments();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred25_DRLExpressions

    // $ANTLR start synpred26_DRLExpressions
    public final void synpred26_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:168:9: ( literal )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:168:10: literal
        {
            pushFollow( FOLLOW_literal_in_synpred26_DRLExpressions1181 );
            literal();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred26_DRLExpressions

    // $ANTLR start synpred27_DRLExpressions
    public final void synpred27_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:170:9: ( super_key )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:170:10: super_key
        {
            pushFollow( FOLLOW_super_key_in_synpred27_DRLExpressions1201 );
            super_key();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred27_DRLExpressions

    // $ANTLR start synpred28_DRLExpressions
    public final void synpred28_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:171:9: ( new_key )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:171:10: new_key
        {
            pushFollow( FOLLOW_new_key_in_synpred28_DRLExpressions1218 );
            new_key();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred28_DRLExpressions

    // $ANTLR start synpred29_DRLExpressions
    public final void synpred29_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:172:9: ( primitiveType )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:172:10: primitiveType
        {
            pushFollow( FOLLOW_primitiveType_in_synpred29_DRLExpressions1235 );
            primitiveType();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred29_DRLExpressions

    // $ANTLR start synpred30_DRLExpressions
    public final void synpred30_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:174:9: ( inlineMapExpression )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:174:10: inlineMapExpression
        {
            pushFollow( FOLLOW_inlineMapExpression_in_synpred30_DRLExpressions1266 );
            inlineMapExpression();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred30_DRLExpressions

    // $ANTLR start synpred31_DRLExpressions
    public final void synpred31_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:175:9: ( inlineListExpression )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:175:10: inlineListExpression
        {
            pushFollow( FOLLOW_inlineListExpression_in_synpred31_DRLExpressions1281 );
            inlineListExpression();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred31_DRLExpressions

    // $ANTLR start synpred32_DRLExpressions
    public final void synpred32_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:176:9: ( ID )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:176:10: ID
        {
            match( input,
                   ID,
                   FOLLOW_ID_in_synpred32_DRLExpressions1296 );
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred32_DRLExpressions

    // $ANTLR start synpred33_DRLExpressions
    public final void synpred33_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:176:19: ( DOT ID )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:176:20: DOT ID
        {
            match( input,
                   DOT,
                   FOLLOW_DOT_in_synpred33_DRLExpressions1303 );
            if ( state.failed ) return;
            match( input,
                   ID,
                   FOLLOW_ID_in_synpred33_DRLExpressions1305 );
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred33_DRLExpressions

    // $ANTLR start synpred34_DRLExpressions
    public final void synpred34_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:176:39: ( identifierSuffix )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:176:40: identifierSuffix
        {
            pushFollow( FOLLOW_identifierSuffix_in_synpred34_DRLExpressions1316 );
            identifierSuffix();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred34_DRLExpressions

    // $ANTLR start synpred36_DRLExpressions
    public final void synpred36_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:202:8: ( LEFT_SQUARE )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:202:9: LEFT_SQUARE
        {
            match( input,
                   LEFT_SQUARE,
                   FOLLOW_LEFT_SQUARE_in_synpred36_DRLExpressions1487 );
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred36_DRLExpressions

    // $ANTLR start synpred38_DRLExpressions
    public final void synpred38_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:229:107: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:229:108: LEFT_SQUARE RIGHT_SQUARE
        {
            match( input,
                   LEFT_SQUARE,
                   FOLLOW_LEFT_SQUARE_in_synpred38_DRLExpressions1669 );
            if ( state.failed ) return;
            match( input,
                   RIGHT_SQUARE,
                   FOLLOW_RIGHT_SQUARE_in_synpred38_DRLExpressions1671 );
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred38_DRLExpressions

    // $ANTLR start synpred39_DRLExpressions
    public final void synpred39_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:261:14: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:261:15: LEFT_PAREN
        {
            match( input,
                   LEFT_PAREN,
                   FOLLOW_LEFT_PAREN_in_synpred39_DRLExpressions1842 );
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred39_DRLExpressions

    // $ANTLR start synpred40_DRLExpressions
    public final void synpred40_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:261:6: ( DOT ID ( ( LEFT_PAREN )=> arguments )? )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:261:6: DOT ID ( ( LEFT_PAREN )=> arguments )?
        {
            match( input,
                   DOT,
                   FOLLOW_DOT_in_synpred40_DRLExpressions1836 );
            if ( state.failed ) return;
            match( input,
                   ID,
                   FOLLOW_ID_in_synpred40_DRLExpressions1838 );
            if ( state.failed ) return;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:261:13: ( ( LEFT_PAREN )=> arguments )?
            int alt77 = 2;
            int LA77_0 = input.LA( 1 );

            if ( (LA77_0 == LEFT_PAREN) && (synpred39_DRLExpressions()) ) {
                alt77 = 1;
            }
            switch ( alt77 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:261:14: ( LEFT_PAREN )=> arguments
                {
                    pushFollow( FOLLOW_arguments_in_synpred40_DRLExpressions1847 );
                    arguments();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;

            }

        }
    }

    // $ANTLR end synpred40_DRLExpressions

    // $ANTLR start synpred41_DRLExpressions
    public final void synpred41_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:263:6: ( DOT super_key superSuffix )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:263:6: DOT super_key superSuffix
        {
            match( input,
                   DOT,
                   FOLLOW_DOT_in_synpred41_DRLExpressions1858 );
            if ( state.failed ) return;
            pushFollow( FOLLOW_super_key_in_synpred41_DRLExpressions1860 );
            super_key();

            state._fsp--;
            if ( state.failed ) return;
            pushFollow( FOLLOW_superSuffix_in_synpred41_DRLExpressions1862 );
            superSuffix();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred41_DRLExpressions

    // $ANTLR start synpred42_DRLExpressions
    public final void synpred42_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:264:6: ( DOT new_key ( nonWildcardTypeArguments )? innerCreator )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:264:6: DOT new_key ( nonWildcardTypeArguments )? innerCreator
        {
            match( input,
                   DOT,
                   FOLLOW_DOT_in_synpred42_DRLExpressions1869 );
            if ( state.failed ) return;
            pushFollow( FOLLOW_new_key_in_synpred42_DRLExpressions1871 );
            new_key();

            state._fsp--;
            if ( state.failed ) return;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:264:18: ( nonWildcardTypeArguments )?
            int alt78 = 2;
            int LA78_0 = input.LA( 1 );

            if ( (LA78_0 == LESS) ) {
                alt78 = 1;
            }
            switch ( alt78 ) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:264:19: nonWildcardTypeArguments
                {
                    pushFollow( FOLLOW_nonWildcardTypeArguments_in_synpred42_DRLExpressions1874 );
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if ( state.failed ) return;

                }
                    break;

            }

            pushFollow( FOLLOW_innerCreator_in_synpred42_DRLExpressions1878 );
            innerCreator();

            state._fsp--;
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred42_DRLExpressions

    // $ANTLR start synpred43_DRLExpressions
    public final void synpred43_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:270:15: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:270:16: LEFT_PAREN
        {
            match( input,
                   LEFT_PAREN,
                   FOLLOW_LEFT_PAREN_in_synpred43_DRLExpressions1915 );
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred43_DRLExpressions

    // $ANTLR start synpred44_DRLExpressions
    public final void synpred44_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:294:13: ( GREATER GREATER GREATER )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:294:14: GREATER GREATER GREATER
        {
            match( input,
                   GREATER,
                   FOLLOW_GREATER_in_synpred44_DRLExpressions2152 );
            if ( state.failed ) return;
            match( input,
                   GREATER,
                   FOLLOW_GREATER_in_synpred44_DRLExpressions2154 );
            if ( state.failed ) return;
            match( input,
                   GREATER,
                   FOLLOW_GREATER_in_synpred44_DRLExpressions2156 );
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred44_DRLExpressions

    // $ANTLR start synpred45_DRLExpressions
    public final void synpred45_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:295:13: ( GREATER GREATER )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:295:14: GREATER GREATER
        {
            match( input,
                   GREATER,
                   FOLLOW_GREATER_in_synpred45_DRLExpressions2181 );
            if ( state.failed ) return;
            match( input,
                   GREATER,
                   FOLLOW_GREATER_in_synpred45_DRLExpressions2183 );
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred45_DRLExpressions

    // $ANTLR start synpred46_DRLExpressions
    public final void synpred46_DRLExpressions_fragment() throws RecognitionException {
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:328:4: ( ID EQUALS_ASSIGN )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:328:5: ID EQUALS_ASSIGN
        {
            match( input,
                   ID,
                   FOLLOW_ID_in_synpred46_DRLExpressions2336 );
            if ( state.failed ) return;
            match( input,
                   EQUALS_ASSIGN,
                   FOLLOW_EQUALS_ASSIGN_in_synpred46_DRLExpressions2338 );
            if ( state.failed ) return;

        }
    }

    // $ANTLR end synpred46_DRLExpressions

    // Delegated rules

    public final boolean synpred24_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred24_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred13_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred13_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred17_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred17_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred3_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred23_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred23_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred6_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred2_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred20_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred20_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred30_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred30_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred36_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred36_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred12_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred12_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred26_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred26_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred33_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred33_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred22_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred22_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred27_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred27_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred4_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred7_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred15_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred15_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred21_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred21_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred8_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred18_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred18_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred16_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred16_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred1_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred42_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred42_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred19_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred19_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred45_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred45_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred44_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred44_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred28_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred28_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred32_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred32_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred29_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred29_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred25_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred25_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred43_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred43_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred9_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred9_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred40_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred40_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred34_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred34_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred41_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred41_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred5_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred14_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred14_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred38_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred38_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred31_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred31_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred46_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred46_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    public final boolean synpred39_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred39_DRLExpressions_fragment(); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    protected DFA4         dfa4             = new DFA4( this );
    protected DFA5         dfa5             = new DFA5( this );
    protected DFA13        dfa13            = new DFA13( this );
    protected DFA21        dfa21            = new DFA21( this );
    protected DFA22        dfa22            = new DFA22( this );
    protected DFA23        dfa23            = new DFA23( this );
    protected DFA25        dfa25            = new DFA25( this );
    protected DFA30        dfa30            = new DFA30( this );
    protected DFA28        dfa28            = new DFA28( this );
    protected DFA29        dfa29            = new DFA29( this );
    protected DFA32        dfa32            = new DFA32( this );
    protected DFA37        dfa37            = new DFA37( this );
    protected DFA36        dfa36            = new DFA36( this );
    protected DFA42        dfa42            = new DFA42( this );
    protected DFA51        dfa51            = new DFA51( this );
    protected DFA59        dfa59            = new DFA59( this );
    protected DFA62        dfa62            = new DFA62( this );
    protected DFA66        dfa66            = new DFA66( this );
    static final String    DFA4_eotS        =
                                                    "\52\uffff";
    static final String    DFA4_eofS        =
                                                    "\1\2\51\uffff";
    static final String    DFA4_minS        =
                                                    "\1\10\1\0\50\uffff";
    static final String    DFA4_maxS        =
                                                    "\1\102\1\0\50\uffff";
    static final String    DFA4_acceptS     =
                                                    "\2\uffff\1\2\46\uffff\1\1";
    static final String    DFA4_specialS    =
                                                    "\1\uffff\1\0\50\uffff}>";
    static final String[]  DFA4_transitionS = {
                                            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\12\2\2\uffff" +
                                                    "\3\2\2\uffff\1\2\1\1\5\2\1\uffff\13\2\2\uffff\2\2\5\uffff\1" +
                                                    "\2",
                                            "\1\uffff",
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
                                            ""
                                            };

    static final short[]   DFA4_eot         = DFA.unpackEncodedString( DFA4_eotS );
    static final short[]   DFA4_eof         = DFA.unpackEncodedString( DFA4_eofS );
    static final char[]    DFA4_min         = DFA.unpackEncodedStringToUnsignedChars( DFA4_minS );
    static final char[]    DFA4_max         = DFA.unpackEncodedStringToUnsignedChars( DFA4_maxS );
    static final short[]   DFA4_accept      = DFA.unpackEncodedString( DFA4_acceptS );
    static final short[]   DFA4_special     = DFA.unpackEncodedString( DFA4_specialS );
    static final short[][] DFA4_transition;

    static {
        int numStates = DFA4_transitionS.length;
        DFA4_transition = new short[numStates][];
        for ( int i = 0; i < numStates; i++ ) {
            DFA4_transition[i] = DFA.unpackEncodedString( DFA4_transitionS[i] );
        }
    }

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA4_eot;
            this.eof = DFA4_eof;
            this.min = DFA4_min;
            this.max = DFA4_max;
            this.accept = DFA4_accept;
            this.special = DFA4_special;
            this.transition = DFA4_transition;
        }

        public String getDescription() {
            return "54:9: ( ( typeArguments )=> typeArguments )?";
        }

        public int specialStateTransition( int s,
                                           IntStream _input ) throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch ( s ) {
                case 0 :
                    int LA4_1 = input.LA( 1 );

                    int index4_1 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred3_DRLExpressions()) ) {
                        s = 41;
                    }

                    else if ( (true) ) {
                        s = 2;
                    }

                    input.seek( index4_1 );
                    if ( s >= 0 ) return s;
                    break;
            }
            if ( state.backtracking > 0 ) {
                state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                    new NoViableAltException( getDescription(),
                                              4,
                                              _s,
                                              input );
            error( nvae );
            throw nvae;
        }
    }

    static final String    DFA5_eotS        =
                                                    "\52\uffff";
    static final String    DFA5_eofS        =
                                                    "\1\2\51\uffff";
    static final String    DFA5_minS        =
                                                    "\1\10\1\0\50\uffff";
    static final String    DFA5_maxS        =
                                                    "\1\102\1\0\50\uffff";
    static final String    DFA5_acceptS     =
                                                    "\2\uffff\1\2\46\uffff\1\1";
    static final String    DFA5_specialS    =
                                                    "\1\uffff\1\0\50\uffff}>";
    static final String[]  DFA5_transitionS = {
                                            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\12\2\2\uffff" +
                                                    "\3\2\2\uffff\1\2\1\1\5\2\1\uffff\13\2\2\uffff\2\2\5\uffff\1" +
                                                    "\2",
                                            "\1\uffff",
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
                                            ""
                                            };

    static final short[]   DFA5_eot         = DFA.unpackEncodedString( DFA5_eotS );
    static final short[]   DFA5_eof         = DFA.unpackEncodedString( DFA5_eofS );
    static final char[]    DFA5_min         = DFA.unpackEncodedStringToUnsignedChars( DFA5_minS );
    static final char[]    DFA5_max         = DFA.unpackEncodedStringToUnsignedChars( DFA5_maxS );
    static final short[]   DFA5_accept      = DFA.unpackEncodedString( DFA5_acceptS );
    static final short[]   DFA5_special     = DFA.unpackEncodedString( DFA5_specialS );
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for ( int i = 0; i < numStates; i++ ) {
            DFA5_transition[i] = DFA.unpackEncodedString( DFA5_transitionS[i] );
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }

        public String getDescription() {
            return "54:51: ( ( typeArguments )=> typeArguments )?";
        }

        public int specialStateTransition( int s,
                                           IntStream _input ) throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch ( s ) {
                case 0 :
                    int LA5_1 = input.LA( 1 );

                    int index5_1 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred4_DRLExpressions()) ) {
                        s = 41;
                    }

                    else if ( (true) ) {
                        s = 2;
                    }

                    input.seek( index5_1 );
                    if ( s >= 0 ) return s;
                    break;
            }
            if ( state.backtracking > 0 ) {
                state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                    new NoViableAltException( getDescription(),
                                              5,
                                              _s,
                                              input );
            error( nvae );
            throw nvae;
        }
    }

    static final String    DFA13_eotS        =
                                                     "\16\uffff";
    static final String    DFA13_eofS        =
                                                     "\16\uffff";
    static final String    DFA13_minS        =
                                                     "\1\10\13\0\2\uffff";
    static final String    DFA13_maxS        =
                                                     "\1\102\13\0\2\uffff";
    static final String    DFA13_acceptS     =
                                                     "\14\uffff\1\2\1\1";
    static final String    DFA13_specialS    =
                                                     "\1\uffff\1\7\1\3\1\4\1\0\1\1\1\11\1\12\1\6\1\10\1\2\1\5\2\uffff}>";
    static final String[]  DFA13_transitionS = {
                                             "\1\14\2\uffff\2\14\1\uffff\1\14\3\uffff\2\14\1\uffff\1\2\1\3" +
                                                     "\1\4\1\5\1\6\1\7\1\10\1\11\2\14\2\uffff\1\14\4\uffff\1\13\1" +
                                                     "\12\1\1\4\14\1\uffff\2\14\4\uffff\2\14\5\uffff\2\14\5\uffff" +
                                                     "\1\14",
                                             "\1\uffff",
                                             "\1\uffff",
                                             "\1\uffff",
                                             "\1\uffff",
                                             "\1\uffff",
                                             "\1\uffff",
                                             "\1\uffff",
                                             "\1\uffff",
                                             "\1\uffff",
                                             "\1\uffff",
                                             "\1\uffff",
                                             "",
                                             ""
                                             };

    static final short[]   DFA13_eot         = DFA.unpackEncodedString( DFA13_eotS );
    static final short[]   DFA13_eof         = DFA.unpackEncodedString( DFA13_eofS );
    static final char[]    DFA13_min         = DFA.unpackEncodedStringToUnsignedChars( DFA13_minS );
    static final char[]    DFA13_max         = DFA.unpackEncodedStringToUnsignedChars( DFA13_maxS );
    static final short[]   DFA13_accept      = DFA.unpackEncodedString( DFA13_acceptS );
    static final short[]   DFA13_special     = DFA.unpackEncodedString( DFA13_specialS );
    static final short[][] DFA13_transition;

    static {
        int numStates = DFA13_transitionS.length;
        DFA13_transition = new short[numStates][];
        for ( int i = 0; i < numStates; i++ ) {
            DFA13_transition[i] = DFA.unpackEncodedString( DFA13_transitionS[i] );
        }
    }

    class DFA13 extends DFA {

        public DFA13(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 13;
            this.eot = DFA13_eot;
            this.eof = DFA13_eof;
            this.min = DFA13_min;
            this.max = DFA13_max;
            this.accept = DFA13_accept;
            this.special = DFA13_special;
            this.transition = DFA13_transition;
        }

        public String getDescription() {
            return "71:26: ( ( assignmentOperator )=> assignmentOperator expression )?";
        }

        public int specialStateTransition( int s,
                                           IntStream _input ) throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch ( s ) {
                case 0 :
                    int LA13_4 = input.LA( 1 );

                    int index13_4 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred6_DRLExpressions()) ) {
                        s = 13;
                    }

                    else if ( (true) ) {
                        s = 12;
                    }

                    input.seek( index13_4 );
                    if ( s >= 0 ) return s;
                    break;
                case 1 :
                    int LA13_5 = input.LA( 1 );

                    int index13_5 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred6_DRLExpressions()) ) {
                        s = 13;
                    }

                    else if ( (true) ) {
                        s = 12;
                    }

                    input.seek( index13_5 );
                    if ( s >= 0 ) return s;
                    break;
                case 2 :
                    int LA13_10 = input.LA( 1 );

                    int index13_10 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred6_DRLExpressions()) ) {
                        s = 13;
                    }

                    else if ( (true) ) {
                        s = 12;
                    }

                    input.seek( index13_10 );
                    if ( s >= 0 ) return s;
                    break;
                case 3 :
                    int LA13_2 = input.LA( 1 );

                    int index13_2 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred6_DRLExpressions()) ) {
                        s = 13;
                    }

                    else if ( (true) ) {
                        s = 12;
                    }

                    input.seek( index13_2 );
                    if ( s >= 0 ) return s;
                    break;
                case 4 :
                    int LA13_3 = input.LA( 1 );

                    int index13_3 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred6_DRLExpressions()) ) {
                        s = 13;
                    }

                    else if ( (true) ) {
                        s = 12;
                    }

                    input.seek( index13_3 );
                    if ( s >= 0 ) return s;
                    break;
                case 5 :
                    int LA13_11 = input.LA( 1 );

                    int index13_11 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred6_DRLExpressions()) ) {
                        s = 13;
                    }

                    else if ( (true) ) {
                        s = 12;
                    }

                    input.seek( index13_11 );
                    if ( s >= 0 ) return s;
                    break;
                case 6 :
                    int LA13_8 = input.LA( 1 );

                    int index13_8 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred6_DRLExpressions()) ) {
                        s = 13;
                    }

                    else if ( (true) ) {
                        s = 12;
                    }

                    input.seek( index13_8 );
                    if ( s >= 0 ) return s;
                    break;
                case 7 :
                    int LA13_1 = input.LA( 1 );

                    int index13_1 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred6_DRLExpressions()) ) {
                        s = 13;
                    }

                    else if ( (true) ) {
                        s = 12;
                    }

                    input.seek( index13_1 );
                    if ( s >= 0 ) return s;
                    break;
                case 8 :
                    int LA13_9 = input.LA( 1 );

                    int index13_9 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred6_DRLExpressions()) ) {
                        s = 13;
                    }

                    else if ( (true) ) {
                        s = 12;
                    }

                    input.seek( index13_9 );
                    if ( s >= 0 ) return s;
                    break;
                case 9 :
                    int LA13_6 = input.LA( 1 );

                    int index13_6 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred6_DRLExpressions()) ) {
                        s = 13;
                    }

                    else if ( (true) ) {
                        s = 12;
                    }

                    input.seek( index13_6 );
                    if ( s >= 0 ) return s;
                    break;
                case 10 :
                    int LA13_7 = input.LA( 1 );

                    int index13_7 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred6_DRLExpressions()) ) {
                        s = 13;
                    }

                    else if ( (true) ) {
                        s = 12;
                    }

                    input.seek( index13_7 );
                    if ( s >= 0 ) return s;
                    break;
            }
            if ( state.backtracking > 0 ) {
                state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                    new NoViableAltException( getDescription(),
                                              13,
                                              _s,
                                              input );
            error( nvae );
            throw nvae;
        }
    }

    static final String    DFA21_eotS        =
                                                     "\50\uffff";
    static final String    DFA21_eofS        =
                                                     "\50\uffff";
    static final String    DFA21_minS        =
                                                     "\1\10\1\0\46\uffff";
    static final String    DFA21_maxS        =
                                                     "\1\102\1\0\46\uffff";
    static final String    DFA21_acceptS     =
                                                     "\2\uffff\1\2\44\uffff\1\1";
    static final String    DFA21_specialS    =
                                                     "\1\uffff\1\0\46\uffff}>";
    static final String[]  DFA21_transitionS = {
                                             "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\12\2\2\uffff" +
                                                     "\3\2\2\uffff\7\2\1\uffff\2\2\1\uffff\10\2\2\uffff\2\2\5\uffff" +
                                                     "\1\1",
                                             "\1\uffff",
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
                                             ""
                                             };

    static final short[]   DFA21_eot         = DFA.unpackEncodedString( DFA21_eotS );
    static final short[]   DFA21_eof         = DFA.unpackEncodedString( DFA21_eofS );
    static final char[]    DFA21_min         = DFA.unpackEncodedStringToUnsignedChars( DFA21_minS );
    static final char[]    DFA21_max         = DFA.unpackEncodedStringToUnsignedChars( DFA21_maxS );
    static final short[]   DFA21_accept      = DFA.unpackEncodedString( DFA21_acceptS );
    static final short[]   DFA21_special     = DFA.unpackEncodedString( DFA21_specialS );
    static final short[][] DFA21_transition;

    static {
        int numStates = DFA21_transitionS.length;
        DFA21_transition = new short[numStates][];
        for ( int i = 0; i < numStates; i++ ) {
            DFA21_transition[i] = DFA.unpackEncodedString( DFA21_transitionS[i] );
        }
    }

    class DFA21 extends DFA {

        public DFA21(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 21;
            this.eot = DFA21_eot;
            this.eof = DFA21_eof;
            this.min = DFA21_min;
            this.max = DFA21_max;
            this.accept = DFA21_accept;
            this.special = DFA21_special;
            this.transition = DFA21_transition;
        }

        public String getDescription() {
            return "102:30: ( instanceof_key type )?";
        }

        public int specialStateTransition( int s,
                                           IntStream _input ) throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch ( s ) {
                case 0 :
                    int LA21_1 = input.LA( 1 );

                    int index21_1 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF )))) ) {
                        s = 39;
                    }

                    else if ( (true) ) {
                        s = 2;
                    }

                    input.seek( index21_1 );
                    if ( s >= 0 ) return s;
                    break;
            }
            if ( state.backtracking > 0 ) {
                state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                    new NoViableAltException( getDescription(),
                                              21,
                                              _s,
                                              input );
            error( nvae );
            throw nvae;
        }
    }

    static final String    DFA22_eotS        =
                                                     "\50\uffff";
    static final String    DFA22_eofS        =
                                                     "\50\uffff";
    static final String    DFA22_minS        =
                                                     "\1\10\21\uffff\2\0\24\uffff";
    static final String    DFA22_maxS        =
                                                     "\1\102\21\uffff\2\0\24\uffff";
    static final String    DFA22_acceptS     =
                                                     "\1\uffff\1\2\45\uffff\1\1";
    static final String    DFA22_specialS    =
                                                     "\1\0\21\uffff\1\1\1\2\24\uffff}>";
    static final String[]  DFA22_transitionS = {
                                             "\1\1\2\uffff\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\12\1\2\uffff" +
                                                     "\3\1\2\47\1\23\1\22\5\1\1\uffff\2\1\1\uffff\10\1\2\uffff\2\1" +
                                                     "\5\uffff\1\1",
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
                                             "\1\uffff",
                                             "\1\uffff",
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
                                             ""
                                             };

    static final short[]   DFA22_eot         = DFA.unpackEncodedString( DFA22_eotS );
    static final short[]   DFA22_eof         = DFA.unpackEncodedString( DFA22_eofS );
    static final char[]    DFA22_min         = DFA.unpackEncodedStringToUnsignedChars( DFA22_minS );
    static final char[]    DFA22_max         = DFA.unpackEncodedStringToUnsignedChars( DFA22_maxS );
    static final short[]   DFA22_accept      = DFA.unpackEncodedString( DFA22_acceptS );
    static final short[]   DFA22_special     = DFA.unpackEncodedString( DFA22_specialS );
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for ( int i = 0; i < numStates; i++ ) {
            DFA22_transition[i] = DFA.unpackEncodedString( DFA22_transitionS[i] );
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }

        public String getDescription() {
            return "()* loopback of 106:25: ( ( LESS )=> relationalOp shiftExpression )*";
        }

        public int specialStateTransition( int s,
                                           IntStream _input ) throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch ( s ) {
                case 0 :
                    int LA22_0 = input.LA( 1 );

                    int index22_0 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (LA22_0 == FLOAT || (LA22_0 >= HEX && LA22_0 <= DECIMAL) || LA22_0 == STRING || (LA22_0 >= BOOL && LA22_0 <= NULL) || (LA22_0 >= PLUS_ASSIGN && LA22_0 <= INCR) || (LA22_0 >= COLON && LA22_0 <= NOT_EQUALS)
                          || (LA22_0 >= EQUALS_ASSIGN && LA22_0 <= RIGHT_SQUARE) || (LA22_0 >= RIGHT_CURLY && LA22_0 <= COMMA) || (LA22_0 >= DOUBLE_AMPER && LA22_0 <= XOR) || (LA22_0 >= MINUS && LA22_0 <= PLUS) || LA22_0 == ID) ) {
                        s = 1;
                    }

                    else if ( (LA22_0 == LESS) ) {
                        s = 18;
                    }

                    else if ( (LA22_0 == GREATER) ) {
                        s = 19;
                    }

                    else if ( ((LA22_0 >= GREATER_EQUALS && LA22_0 <= LESS_EQUALS)) && (synpred7_DRLExpressions()) ) {
                        s = 39;
                    }

                    input.seek( index22_0 );
                    if ( s >= 0 ) return s;
                    break;
                case 1 :
                    int LA22_18 = input.LA( 1 );

                    int index22_18 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred7_DRLExpressions()) ) {
                        s = 39;
                    }

                    else if ( (true) ) {
                        s = 1;
                    }

                    input.seek( index22_18 );
                    if ( s >= 0 ) return s;
                    break;
                case 2 :
                    int LA22_19 = input.LA( 1 );

                    int index22_19 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred7_DRLExpressions()) ) {
                        s = 39;
                    }

                    else if ( (true) ) {
                        s = 1;
                    }

                    input.seek( index22_19 );
                    if ( s >= 0 ) return s;
                    break;
            }
            if ( state.backtracking > 0 ) {
                state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                    new NoViableAltException( getDescription(),
                                              22,
                                              _s,
                                              input );
            error( nvae );
            throw nvae;
        }
    }

    static final String    DFA23_eotS        =
                                                     "\51\uffff";
    static final String    DFA23_eofS        =
                                                     "\51\uffff";
    static final String    DFA23_minS        =
                                                     "\1\10\1\0\21\uffff\1\0\25\uffff";
    static final String    DFA23_maxS        =
                                                     "\1\102\1\0\21\uffff\1\0\25\uffff";
    static final String    DFA23_acceptS     =
                                                     "\2\uffff\1\2\45\uffff\1\1";
    static final String    DFA23_specialS    =
                                                     "\1\uffff\1\0\21\uffff\1\1\25\uffff}>";
    static final String[]  DFA23_transitionS = {
                                             "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\12\2\2\uffff" +
                                                     "\5\2\1\23\1\1\5\2\1\uffff\2\2\1\uffff\10\2\2\uffff\2\2\5\uffff" +
                                                     "\1\2",
                                             "\1\uffff",
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
                                             "\1\uffff",
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
                                             ""
                                             };

    static final short[]   DFA23_eot         = DFA.unpackEncodedString( DFA23_eotS );
    static final short[]   DFA23_eof         = DFA.unpackEncodedString( DFA23_eofS );
    static final char[]    DFA23_min         = DFA.unpackEncodedStringToUnsignedChars( DFA23_minS );
    static final char[]    DFA23_max         = DFA.unpackEncodedStringToUnsignedChars( DFA23_maxS );
    static final short[]   DFA23_accept      = DFA.unpackEncodedString( DFA23_acceptS );
    static final short[]   DFA23_special     = DFA.unpackEncodedString( DFA23_specialS );
    static final short[][] DFA23_transition;

    static {
        int numStates = DFA23_transitionS.length;
        DFA23_transition = new short[numStates][];
        for ( int i = 0; i < numStates; i++ ) {
            DFA23_transition[i] = DFA.unpackEncodedString( DFA23_transitionS[i] );
        }
    }

    class DFA23 extends DFA {

        public DFA23(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 23;
            this.eot = DFA23_eot;
            this.eof = DFA23_eof;
            this.min = DFA23_min;
            this.max = DFA23_max;
            this.accept = DFA23_accept;
            this.special = DFA23_special;
            this.transition = DFA23_transition;
        }

        public String getDescription() {
            return "()* loopback of 114:28: ( ( shiftOp )=> shiftOp additiveExpression )*";
        }

        public int specialStateTransition( int s,
                                           IntStream _input ) throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch ( s ) {
                case 0 :
                    int LA23_1 = input.LA( 1 );

                    int index23_1 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred8_DRLExpressions()) ) {
                        s = 40;
                    }

                    else if ( (true) ) {
                        s = 2;
                    }

                    input.seek( index23_1 );
                    if ( s >= 0 ) return s;
                    break;
                case 1 :
                    int LA23_19 = input.LA( 1 );

                    int index23_19 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred8_DRLExpressions()) ) {
                        s = 40;
                    }

                    else if ( (true) ) {
                        s = 2;
                    }

                    input.seek( index23_19 );
                    if ( s >= 0 ) return s;
                    break;
            }
            if ( state.backtracking > 0 ) {
                state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                    new NoViableAltException( getDescription(),
                                              23,
                                              _s,
                                              input );
            error( nvae );
            throw nvae;
        }
    }

    static final String    DFA25_eotS        =
                                                     "\51\uffff";
    static final String    DFA25_eofS        =
                                                     "\51\uffff";
    static final String    DFA25_minS        =
                                                     "\1\10\30\uffff\2\0\16\uffff";
    static final String    DFA25_maxS        =
                                                     "\1\102\30\uffff\2\0\16\uffff";
    static final String    DFA25_acceptS     =
                                                     "\1\uffff\1\2\46\uffff\1\1";
    static final String    DFA25_specialS    =
                                                     "\31\uffff\1\0\1\1\16\uffff}>";
    static final String[]  DFA25_transitionS = {
                                             "\1\1\2\uffff\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\12\1\2\uffff" +
                                                     "\14\1\1\uffff\2\1\1\uffff\10\1\2\uffff\1\32\1\31\5\uffff\1\1",
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
                                             "\1\uffff",
                                             "\1\uffff",
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
                                             ""
                                             };

    static final short[]   DFA25_eot         = DFA.unpackEncodedString( DFA25_eotS );
    static final short[]   DFA25_eof         = DFA.unpackEncodedString( DFA25_eofS );
    static final char[]    DFA25_min         = DFA.unpackEncodedStringToUnsignedChars( DFA25_minS );
    static final char[]    DFA25_max         = DFA.unpackEncodedStringToUnsignedChars( DFA25_maxS );
    static final short[]   DFA25_accept      = DFA.unpackEncodedString( DFA25_acceptS );
    static final short[]   DFA25_special     = DFA.unpackEncodedString( DFA25_specialS );
    static final short[][] DFA25_transition;

    static {
        int numStates = DFA25_transitionS.length;
        DFA25_transition = new short[numStates][];
        for ( int i = 0; i < numStates; i++ ) {
            DFA25_transition[i] = DFA.unpackEncodedString( DFA25_transitionS[i] );
        }
    }

    class DFA25 extends DFA {

        public DFA25(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 25;
            this.eot = DFA25_eot;
            this.eof = DFA25_eof;
            this.min = DFA25_min;
            this.max = DFA25_max;
            this.accept = DFA25_accept;
            this.special = DFA25_special;
            this.transition = DFA25_transition;
        }

        public String getDescription() {
            return "()* loopback of 122:34: ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*";
        }

        public int specialStateTransition( int s,
                                           IntStream _input ) throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch ( s ) {
                case 0 :
                    int LA25_25 = input.LA( 1 );

                    int index25_25 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred9_DRLExpressions()) ) {
                        s = 40;
                    }

                    else if ( (true) ) {
                        s = 1;
                    }

                    input.seek( index25_25 );
                    if ( s >= 0 ) return s;
                    break;
                case 1 :
                    int LA25_26 = input.LA( 1 );

                    int index25_26 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred9_DRLExpressions()) ) {
                        s = 40;
                    }

                    else if ( (true) ) {
                        s = 1;
                    }

                    input.seek( index25_26 );
                    if ( s >= 0 ) return s;
                    break;
            }
            if ( state.backtracking > 0 ) {
                state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                    new NoViableAltException( getDescription(),
                                              25,
                                              _s,
                                              input );
            error( nvae );
            throw nvae;
        }
    }

    static final String    DFA30_eotS        =
                                                     "\16\uffff";
    static final String    DFA30_eofS        =
                                                     "\16\uffff";
    static final String    DFA30_minS        =
                                                     "\1\10\2\uffff\1\0\12\uffff";
    static final String    DFA30_maxS        =
                                                     "\1\102\2\uffff\1\0\12\uffff";
    static final String    DFA30_acceptS     =
                                                     "\1\uffff\1\1\1\2\1\uffff\1\4\10\uffff\1\3";
    static final String    DFA30_specialS    =
                                                     "\3\uffff\1\0\12\uffff}>";
    static final String[]  DFA30_transitionS = {
                                             "\1\4\2\uffff\2\4\1\uffff\1\4\3\uffff\2\4\23\uffff\1\4\1\uffff" +
                                                     "\1\3\1\uffff\1\4\10\uffff\1\2\1\1\14\uffff\1\4",
                                             "",
                                             "",
                                             "\1\uffff",
                                             "",
                                             "",
                                             "",
                                             "",
                                             "",
                                             "",
                                             "",
                                             "",
                                             "",
                                             ""
                                             };

    static final short[]   DFA30_eot         = DFA.unpackEncodedString( DFA30_eotS );
    static final short[]   DFA30_eof         = DFA.unpackEncodedString( DFA30_eofS );
    static final char[]    DFA30_min         = DFA.unpackEncodedStringToUnsignedChars( DFA30_minS );
    static final char[]    DFA30_max         = DFA.unpackEncodedStringToUnsignedChars( DFA30_maxS );
    static final short[]   DFA30_accept      = DFA.unpackEncodedString( DFA30_acceptS );
    static final short[]   DFA30_special     = DFA.unpackEncodedString( DFA30_specialS );
    static final short[][] DFA30_transition;

    static {
        int numStates = DFA30_transitionS.length;
        DFA30_transition = new short[numStates][];
        for ( int i = 0; i < numStates; i++ ) {
            DFA30_transition[i] = DFA.unpackEncodedString( DFA30_transitionS[i] );
        }
    }

    class DFA30 extends DFA {

        public DFA30(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 30;
            this.eot = DFA30_eot;
            this.eof = DFA30_eof;
            this.min = DFA30_min;
            this.max = DFA30_max;
            this.accept = DFA30_accept;
            this.special = DFA30_special;
            this.transition = DFA30_transition;
        }

        public String getDescription() {
            return "137:1: unaryExpressionNotPlusMinus options {backtrack=true; memoize=true; } : ( TILDE unaryExpression | NEGATION unaryExpression | castExpression | primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );";
        }

        public int specialStateTransition( int s,
                                           IntStream _input ) throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch ( s ) {
                case 0 :
                    int LA30_3 = input.LA( 1 );

                    int index30_3 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred12_DRLExpressions()) ) {
                        s = 13;
                    }

                    else if ( (true) ) {
                        s = 4;
                    }

                    input.seek( index30_3 );
                    if ( s >= 0 ) return s;
                    break;
            }
            if ( state.backtracking > 0 ) {
                state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                    new NoViableAltException( getDescription(),
                                              30,
                                              _s,
                                              input );
            error( nvae );
            throw nvae;
        }
    }

    static final String    DFA28_eotS        =
                                                     "\53\uffff";
    static final String    DFA28_eofS        =
                                                     "\1\1\52\uffff";
    static final String    DFA28_minS        =
                                                     "\1\10\46\uffff\1\0\3\uffff";
    static final String    DFA28_maxS        =
                                                     "\1\103\46\uffff\1\0\3\uffff";
    static final String    DFA28_acceptS     =
                                                     "\1\uffff\1\2\50\uffff\1\1";
    static final String    DFA28_specialS    =
                                                     "\1\0\46\uffff\1\1\3\uffff}>";
    static final String[]  DFA28_transitionS = {
                                             "\1\1\2\uffff\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\12\1\2\uffff" +
                                                     "\12\1\1\47\1\1\1\uffff\2\1\1\52\14\1\5\uffff\2\1",
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
                                             "\1\uffff",
                                             "",
                                             "",
                                             ""
                                             };

    static final short[]   DFA28_eot         = DFA.unpackEncodedString( DFA28_eotS );
    static final short[]   DFA28_eof         = DFA.unpackEncodedString( DFA28_eofS );
    static final char[]    DFA28_min         = DFA.unpackEncodedStringToUnsignedChars( DFA28_minS );
    static final char[]    DFA28_max         = DFA.unpackEncodedStringToUnsignedChars( DFA28_maxS );
    static final short[]   DFA28_accept      = DFA.unpackEncodedString( DFA28_acceptS );
    static final short[]   DFA28_special     = DFA.unpackEncodedString( DFA28_specialS );
    static final short[][] DFA28_transition;

    static {
        int numStates = DFA28_transitionS.length;
        DFA28_transition = new short[numStates][];
        for ( int i = 0; i < numStates; i++ ) {
            DFA28_transition[i] = DFA.unpackEncodedString( DFA28_transitionS[i] );
        }
    }

    class DFA28 extends DFA {

        public DFA28(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 28;
            this.eot = DFA28_eot;
            this.eof = DFA28_eof;
            this.min = DFA28_min;
            this.max = DFA28_max;
            this.accept = DFA28_accept;
            this.special = DFA28_special;
            this.transition = DFA28_transition;
        }

        public String getDescription() {
            return "()* loopback of 142:17: ( ( selector )=> selector )*";
        }

        public int specialStateTransition( int s,
                                           IntStream _input ) throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch ( s ) {
                case 0 :
                    int LA28_0 = input.LA( 1 );

                    int index28_0 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (LA28_0 == EOF || LA28_0 == FLOAT || (LA28_0 >= HEX && LA28_0 <= DECIMAL) || LA28_0 == STRING || (LA28_0 >= BOOL && LA28_0 <= NULL) || (LA28_0 >= PLUS_ASSIGN && LA28_0 <= INCR) || (LA28_0 >= COLON && LA28_0 <= RIGHT_PAREN)
                          || LA28_0 == RIGHT_SQUARE || (LA28_0 >= RIGHT_CURLY && LA28_0 <= COMMA) || (LA28_0 >= DOUBLE_AMPER && LA28_0 <= PLUS) || (LA28_0 >= ID && LA28_0 <= DIV)) ) {
                        s = 1;
                    }

                    else if ( (LA28_0 == LEFT_SQUARE) ) {
                        s = 39;
                    }

                    else if ( (LA28_0 == DOT) && (synpred13_DRLExpressions()) ) {
                        s = 42;
                    }

                    input.seek( index28_0 );
                    if ( s >= 0 ) return s;
                    break;
                case 1 :
                    int LA28_39 = input.LA( 1 );

                    int index28_39 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred13_DRLExpressions()) ) {
                        s = 42;
                    }

                    else if ( (true) ) {
                        s = 1;
                    }

                    input.seek( index28_39 );
                    if ( s >= 0 ) return s;
                    break;
            }
            if ( state.backtracking > 0 ) {
                state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                    new NoViableAltException( getDescription(),
                                              28,
                                              _s,
                                              input );
            error( nvae );
            throw nvae;
        }
    }

    static final String    DFA29_eotS        =
                                                     "\53\uffff";
    static final String    DFA29_eofS        =
                                                     "\1\2\52\uffff";
    static final String    DFA29_minS        =
                                                     "\1\10\1\0\33\uffff\1\0\15\uffff";
    static final String    DFA29_maxS        =
                                                     "\1\103\1\0\33\uffff\1\0\15\uffff";
    static final String    DFA29_acceptS     =
                                                     "\2\uffff\1\2\47\uffff\1\1";
    static final String    DFA29_specialS    =
                                                     "\1\uffff\1\0\33\uffff\1\1\15\uffff}>";
    static final String[]  DFA29_transitionS = {
                                             "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\10\2\1\35\1" +
                                                     "\1\2\uffff\14\2\1\uffff\2\2\1\uffff\14\2\5\uffff\2\2",
                                             "\1\uffff",
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
                                             "",
                                             "",
                                             "",
                                             "\1\uffff",
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
                                             ""
                                             };

    static final short[]   DFA29_eot         = DFA.unpackEncodedString( DFA29_eotS );
    static final short[]   DFA29_eof         = DFA.unpackEncodedString( DFA29_eofS );
    static final char[]    DFA29_min         = DFA.unpackEncodedStringToUnsignedChars( DFA29_minS );
    static final char[]    DFA29_max         = DFA.unpackEncodedStringToUnsignedChars( DFA29_maxS );
    static final short[]   DFA29_accept      = DFA.unpackEncodedString( DFA29_acceptS );
    static final short[]   DFA29_special     = DFA.unpackEncodedString( DFA29_specialS );
    static final short[][] DFA29_transition;

    static {
        int numStates = DFA29_transitionS.length;
        DFA29_transition = new short[numStates][];
        for ( int i = 0; i < numStates; i++ ) {
            DFA29_transition[i] = DFA.unpackEncodedString( DFA29_transitionS[i] );
        }
    }

    class DFA29 extends DFA {

        public DFA29(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 29;
            this.eot = DFA29_eot;
            this.eof = DFA29_eof;
            this.min = DFA29_min;
            this.max = DFA29_max;
            this.accept = DFA29_accept;
            this.special = DFA29_special;
            this.transition = DFA29_transition;
        }

        public String getDescription() {
            return "142:41: ( ( INCR | DECR )=> ( INCR | DECR ) )?";
        }

        public int specialStateTransition( int s,
                                           IntStream _input ) throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch ( s ) {
                case 0 :
                    int LA29_1 = input.LA( 1 );

                    int index29_1 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred14_DRLExpressions()) ) {
                        s = 42;
                    }

                    else if ( (true) ) {
                        s = 2;
                    }

                    input.seek( index29_1 );
                    if ( s >= 0 ) return s;
                    break;
                case 1 :
                    int LA29_29 = input.LA( 1 );

                    int index29_29 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred14_DRLExpressions()) ) {
                        s = 42;
                    }

                    else if ( (true) ) {
                        s = 2;
                    }

                    input.seek( index29_29 );
                    if ( s >= 0 ) return s;
                    break;
            }
            if ( state.backtracking > 0 ) {
                state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                    new NoViableAltException( getDescription(),
                                              29,
                                              _s,
                                              input );
            error( nvae );
            throw nvae;
        }
    }

    static final String    DFA32_eotS        =
                                                     "\12\uffff";
    static final String    DFA32_eofS        =
                                                     "\12\uffff";
    static final String    DFA32_minS        =
                                                     "\1\102\1\0\10\uffff";
    static final String    DFA32_maxS        =
                                                     "\1\102\1\0\10\uffff";
    static final String    DFA32_acceptS     =
                                                     "\2\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10";
    static final String    DFA32_specialS    =
                                                     "\1\1\1\0\10\uffff}>";
    static final String[]  DFA32_transitionS = {
                                             "\1\1",
                                             "\1\uffff",
                                             "",
                                             "",
                                             "",
                                             "",
                                             "",
                                             "",
                                             "",
                                             ""
                                             };

    static final short[]   DFA32_eot         = DFA.unpackEncodedString( DFA32_eotS );
    static final short[]   DFA32_eof         = DFA.unpackEncodedString( DFA32_eofS );
    static final char[]    DFA32_min         = DFA.unpackEncodedStringToUnsignedChars( DFA32_minS );
    static final char[]    DFA32_max         = DFA.unpackEncodedStringToUnsignedChars( DFA32_maxS );
    static final short[]   DFA32_accept      = DFA.unpackEncodedString( DFA32_acceptS );
    static final short[]   DFA32_special     = DFA.unpackEncodedString( DFA32_specialS );
    static final short[][] DFA32_transition;

    static {
        int numStates = DFA32_transitionS.length;
        DFA32_transition = new short[numStates][];
        for ( int i = 0; i < numStates; i++ ) {
            DFA32_transition[i] = DFA.unpackEncodedString( DFA32_transitionS[i] );
        }
    }

    class DFA32 extends DFA {

        public DFA32(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 32;
            this.eot = DFA32_eot;
            this.eof = DFA32_eof;
            this.min = DFA32_min;
            this.max = DFA32_max;
            this.accept = DFA32_accept;
            this.special = DFA32_special;
            this.transition = DFA32_transition;
        }

        public String getDescription() {
            return "152:1: primitiveType options {backtrack=true; memoize=true; } : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );";
        }

        public int specialStateTransition( int s,
                                           IntStream _input ) throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch ( s ) {
                case 0 :
                    int LA32_1 = input.LA( 1 );

                    int index32_1 = input.index();
                    input.rewind();
                    s = -1;
                    if ( ((synpred17_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF ))))) ) {
                        s = 2;
                    }

                    else if ( ((synpred18_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR ))))) ) {
                        s = 3;
                    }

                    else if ( ((synpred19_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))))) ) {
                        s = 4;
                    }

                    else if ( ((synpred20_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))))) ) {
                        s = 5;
                    }

                    else if ( ((synpred21_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.INT ))))) ) {
                        s = 6;
                    }

                    else if ( ((synpred22_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG ))))) ) {
                        s = 7;
                    }

                    else if ( ((synpred23_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT ))))) ) {
                        s = 8;
                    }

                    else if ( (((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))) ) {
                        s = 9;
                    }

                    input.seek( index32_1 );
                    if ( s >= 0 ) return s;
                    break;
                case 1 :
                    int LA32_0 = input.LA( 1 );

                    int index32_0 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (LA32_0 == ID) && (((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))) ) {
                        s = 1;
                    }

                    input.seek( index32_0 );
                    if ( s >= 0 ) return s;
                    break;
            }
            if ( state.backtracking > 0 ) {
                state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                    new NoViableAltException( getDescription(),
                                              32,
                                              _s,
                                              input );
            error( nvae );
            throw nvae;
        }
    }

    static final String    DFA37_eotS        =
                                                     "\21\uffff";
    static final String    DFA37_eofS        =
                                                     "\21\uffff";
    static final String    DFA37_minS        =
                                                     "\1\10\10\uffff\2\0\6\uffff";
    static final String    DFA37_maxS        =
                                                     "\1\102\10\uffff\2\0\6\uffff";
    static final String    DFA37_acceptS     =
                                                     "\1\uffff\1\1\1\2\6\3\2\uffff\1\4\1\5\1\6\1\11\1\7\1\10";
    static final String    DFA37_specialS    =
                                                     "\1\0\10\uffff\1\1\1\2\6\uffff}>";
    static final String[]  DFA37_transitionS = {
                                             "\1\6\2\uffff\1\5\1\4\1\uffff\1\3\3\uffff\1\7\1\10\23\uffff\1" +
                                                     "\2\1\uffff\1\1\1\uffff\1\12\26\uffff\1\11",
                                             "",
                                             "",
                                             "",
                                             "",
                                             "",
                                             "",
                                             "",
                                             "",
                                             "\1\uffff",
                                             "\1\uffff",
                                             "",
                                             "",
                                             "",
                                             "",
                                             "",
                                             ""
                                             };

    static final short[]   DFA37_eot         = DFA.unpackEncodedString( DFA37_eotS );
    static final short[]   DFA37_eof         = DFA.unpackEncodedString( DFA37_eofS );
    static final char[]    DFA37_min         = DFA.unpackEncodedStringToUnsignedChars( DFA37_minS );
    static final char[]    DFA37_max         = DFA.unpackEncodedStringToUnsignedChars( DFA37_maxS );
    static final short[]   DFA37_accept      = DFA.unpackEncodedString( DFA37_acceptS );
    static final short[]   DFA37_special     = DFA.unpackEncodedString( DFA37_specialS );
    static final short[][] DFA37_transition;

    static {
        int numStates = DFA37_transitionS.length;
        DFA37_transition = new short[numStates][];
        for ( int i = 0; i < numStates; i++ ) {
            DFA37_transition[i] = DFA.unpackEncodedString( DFA37_transitionS[i] );
        }
    }

    class DFA37 extends DFA {

        public DFA37(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 37;
            this.eot = DFA37_eot;
            this.eof = DFA37_eof;
            this.min = DFA37_min;
            this.max = DFA37_max;
            this.accept = DFA37_accept;
            this.special = DFA37_special;
            this.transition = DFA37_transition;
        }

        public String getDescription() {
            return "164:1: primary : ( ( parExpression )=> parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )? );";
        }

        public int specialStateTransition( int s,
                                           IntStream _input ) throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch ( s ) {
                case 0 :
                    int LA37_0 = input.LA( 1 );

                    int index37_0 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (LA37_0 == LEFT_PAREN) && (synpred24_DRLExpressions()) ) {
                        s = 1;
                    }

                    else if ( (LA37_0 == LESS) && (synpred25_DRLExpressions()) ) {
                        s = 2;
                    }

                    else if ( (LA37_0 == STRING) && (synpred26_DRLExpressions()) ) {
                        s = 3;
                    }

                    else if ( (LA37_0 == DECIMAL) && (synpred26_DRLExpressions()) ) {
                        s = 4;
                    }

                    else if ( (LA37_0 == HEX) && (synpred26_DRLExpressions()) ) {
                        s = 5;
                    }

                    else if ( (LA37_0 == FLOAT) && (synpred26_DRLExpressions()) ) {
                        s = 6;
                    }

                    else if ( (LA37_0 == BOOL) && (synpred26_DRLExpressions()) ) {
                        s = 7;
                    }

                    else if ( (LA37_0 == NULL) && (synpred26_DRLExpressions()) ) {
                        s = 8;
                    }

                    else if ( (LA37_0 == ID) ) {
                        s = 9;
                    }

                    else if ( (LA37_0 == LEFT_SQUARE) ) {
                        s = 10;
                    }

                    input.seek( index37_0 );
                    if ( s >= 0 ) return s;
                    break;
                case 1 :
                    int LA37_9 = input.LA( 1 );

                    int index37_9 = input.index();
                    input.rewind();
                    s = -1;
                    if ( ((synpred27_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.SUPER ))))) ) {
                        s = 11;
                    }

                    else if ( ((synpred28_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.NEW ))))) ) {
                        s = 12;
                    }

                    else if ( (((synpred29_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.DOUBLE )))) || (synpred29_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.INSTANCEOF ))))
                                || (synpred29_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.FLOAT )))) || (synpred29_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.BYTE ))))
                                || (synpred29_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.INT )))) || (synpred29_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.SHORT ))))
                                || (synpred29_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.CHAR )))) || (synpred29_DRLExpressions() && ((helper.validateIdentifierKey( DroolsSoftKeywords.LONG )))))) ) {
                        s = 13;
                    }

                    else if ( (synpred32_DRLExpressions()) ) {
                        s = 14;
                    }

                    input.seek( index37_9 );
                    if ( s >= 0 ) return s;
                    break;
                case 2 :
                    int LA37_10 = input.LA( 1 );

                    int index37_10 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred30_DRLExpressions()) ) {
                        s = 15;
                    }

                    else if ( (synpred31_DRLExpressions()) ) {
                        s = 16;
                    }

                    input.seek( index37_10 );
                    if ( s >= 0 ) return s;
                    break;
            }
            if ( state.backtracking > 0 ) {
                state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                    new NoViableAltException( getDescription(),
                                              37,
                                              _s,
                                              input );
            error( nvae );
            throw nvae;
        }
    }

    static final String    DFA36_eotS        =
                                                     "\54\uffff";
    static final String    DFA36_eofS        =
                                                     "\1\3\53\uffff";
    static final String    DFA36_minS        =
                                                     "\1\10\2\0\51\uffff";
    static final String    DFA36_maxS        =
                                                     "\1\103\2\0\51\uffff";
    static final String    DFA36_acceptS     =
                                                     "\3\uffff\1\2\47\uffff\1\1";
    static final String    DFA36_specialS    =
                                                     "\1\uffff\1\0\1\1\51\uffff}>";
    static final String[]  DFA36_transitionS = {
                                             "\1\3\2\uffff\2\3\1\uffff\1\3\3\uffff\2\3\1\uffff\12\3\2\uffff" +
                                                     "\10\3\1\2\1\3\1\1\1\3\1\uffff\17\3\5\uffff\2\3",
                                             "\1\uffff",
                                             "\1\uffff",
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
                                             ""
                                             };

    static final short[]   DFA36_eot         = DFA.unpackEncodedString( DFA36_eotS );
    static final short[]   DFA36_eof         = DFA.unpackEncodedString( DFA36_eofS );
    static final char[]    DFA36_min         = DFA.unpackEncodedStringToUnsignedChars( DFA36_minS );
    static final char[]    DFA36_max         = DFA.unpackEncodedStringToUnsignedChars( DFA36_maxS );
    static final short[]   DFA36_accept      = DFA.unpackEncodedString( DFA36_acceptS );
    static final short[]   DFA36_special     = DFA.unpackEncodedString( DFA36_specialS );
    static final short[][] DFA36_transition;

    static {
        int numStates = DFA36_transitionS.length;
        DFA36_transition = new short[numStates][];
        for ( int i = 0; i < numStates; i++ ) {
            DFA36_transition[i] = DFA.unpackEncodedString( DFA36_transitionS[i] );
        }
    }

    class DFA36 extends DFA {

        public DFA36(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 36;
            this.eot = DFA36_eot;
            this.eof = DFA36_eof;
            this.min = DFA36_min;
            this.max = DFA36_max;
            this.accept = DFA36_accept;
            this.special = DFA36_special;
            this.transition = DFA36_transition;
        }

        public String getDescription() {
            return "176:38: ( ( identifierSuffix )=> identifierSuffix )?";
        }

        public int specialStateTransition( int s,
                                           IntStream _input ) throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch ( s ) {
                case 0 :
                    int LA36_1 = input.LA( 1 );

                    int index36_1 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred34_DRLExpressions()) ) {
                        s = 43;
                    }

                    else if ( (true) ) {
                        s = 3;
                    }

                    input.seek( index36_1 );
                    if ( s >= 0 ) return s;
                    break;
                case 1 :
                    int LA36_2 = input.LA( 1 );

                    int index36_2 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred34_DRLExpressions()) ) {
                        s = 43;
                    }

                    else if ( (true) ) {
                        s = 3;
                    }

                    input.seek( index36_2 );
                    if ( s >= 0 ) return s;
                    break;
            }
            if ( state.backtracking > 0 ) {
                state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                    new NoViableAltException( getDescription(),
                                              36,
                                              _s,
                                              input );
            error( nvae );
            throw nvae;
        }
    }

    static final String    DFA42_eotS        =
                                                     "\54\uffff";
    static final String    DFA42_eofS        =
                                                     "\1\1\53\uffff";
    static final String    DFA42_minS        =
                                                     "\1\10\46\uffff\1\0\4\uffff";
    static final String    DFA42_maxS        =
                                                     "\1\103\46\uffff\1\0\4\uffff";
    static final String    DFA42_acceptS     =
                                                     "\1\uffff\1\2\51\uffff\1\1";
    static final String    DFA42_specialS    =
                                                     "\47\uffff\1\0\4\uffff}>";
    static final String[]  DFA42_transitionS = {
                                             "\1\1\2\uffff\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\12\1\2\uffff" +
                                                     "\12\1\1\47\1\1\1\uffff\17\1\5\uffff\2\1",
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
                                             "\1\uffff",
                                             "",
                                             "",
                                             "",
                                             ""
                                             };

    static final short[]   DFA42_eot         = DFA.unpackEncodedString( DFA42_eotS );
    static final short[]   DFA42_eof         = DFA.unpackEncodedString( DFA42_eofS );
    static final char[]    DFA42_min         = DFA.unpackEncodedStringToUnsignedChars( DFA42_minS );
    static final char[]    DFA42_max         = DFA.unpackEncodedStringToUnsignedChars( DFA42_maxS );
    static final short[]   DFA42_accept      = DFA.unpackEncodedString( DFA42_acceptS );
    static final short[]   DFA42_special     = DFA.unpackEncodedString( DFA42_specialS );
    static final short[][] DFA42_transition;

    static {
        int numStates = DFA42_transitionS.length;
        DFA42_transition = new short[numStates][];
        for ( int i = 0; i < numStates; i++ ) {
            DFA42_transition[i] = DFA.unpackEncodedString( DFA42_transitionS[i] );
        }
    }

    class DFA42 extends DFA {

        public DFA42(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 42;
            this.eot = DFA42_eot;
            this.eof = DFA42_eof;
            this.min = DFA42_min;
            this.max = DFA42_max;
            this.accept = DFA42_accept;
            this.special = DFA42_special;
            this.transition = DFA42_transition;
        }

        public String getDescription() {
            return "()+ loopback of 202:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+";
        }

        public int specialStateTransition( int s,
                                           IntStream _input ) throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch ( s ) {
                case 0 :
                    int LA42_39 = input.LA( 1 );

                    int index42_39 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred36_DRLExpressions()) ) {
                        s = 43;
                    }

                    else if ( (true) ) {
                        s = 1;
                    }

                    input.seek( index42_39 );
                    if ( s >= 0 ) return s;
                    break;
            }
            if ( state.backtracking > 0 ) {
                state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                    new NoViableAltException( getDescription(),
                                              42,
                                              _s,
                                              input );
            error( nvae );
            throw nvae;
        }
    }

    static final String    DFA51_eotS        =
                                                     "\54\uffff";
    static final String    DFA51_eofS        =
                                                     "\1\2\53\uffff";
    static final String    DFA51_minS        =
                                                     "\1\10\1\0\52\uffff";
    static final String    DFA51_maxS        =
                                                     "\1\103\1\0\52\uffff";
    static final String    DFA51_acceptS     =
                                                     "\2\uffff\1\2\50\uffff\1\1";
    static final String    DFA51_specialS    =
                                                     "\1\uffff\1\0\52\uffff}>";
    static final String[]  DFA51_transitionS = {
                                             "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\12\2\2\uffff" +
                                                     "\12\2\1\1\1\2\1\uffff\17\2\5\uffff\2\2",
                                             "\1\uffff",
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
                                             ""
                                             };

    static final short[]   DFA51_eot         = DFA.unpackEncodedString( DFA51_eotS );
    static final short[]   DFA51_eof         = DFA.unpackEncodedString( DFA51_eofS );
    static final char[]    DFA51_min         = DFA.unpackEncodedStringToUnsignedChars( DFA51_minS );
    static final char[]    DFA51_max         = DFA.unpackEncodedStringToUnsignedChars( DFA51_maxS );
    static final short[]   DFA51_accept      = DFA.unpackEncodedString( DFA51_acceptS );
    static final short[]   DFA51_special     = DFA.unpackEncodedString( DFA51_specialS );
    static final short[][] DFA51_transition;

    static {
        int numStates = DFA51_transitionS.length;
        DFA51_transition = new short[numStates][];
        for ( int i = 0; i < numStates; i++ ) {
            DFA51_transition[i] = DFA.unpackEncodedString( DFA51_transitionS[i] );
        }
    }

    class DFA51 extends DFA {

        public DFA51(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 51;
            this.eot = DFA51_eot;
            this.eof = DFA51_eof;
            this.min = DFA51_min;
            this.max = DFA51_max;
            this.accept = DFA51_accept;
            this.special = DFA51_special;
            this.transition = DFA51_transition;
        }

        public String getDescription() {
            return "()* loopback of 229:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*";
        }

        public int specialStateTransition( int s,
                                           IntStream _input ) throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch ( s ) {
                case 0 :
                    int LA51_1 = input.LA( 1 );

                    int index51_1 = input.index();
                    input.rewind();
                    s = -1;
                    if ( ((!helper.validateLT( 2,
                                               "]" ))) ) {
                        s = 43;
                    }

                    else if ( (true) ) {
                        s = 2;
                    }

                    input.seek( index51_1 );
                    if ( s >= 0 ) return s;
                    break;
            }
            if ( state.backtracking > 0 ) {
                state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                    new NoViableAltException( getDescription(),
                                              51,
                                              _s,
                                              input );
            error( nvae );
            throw nvae;
        }
    }

    static final String    DFA59_eotS        =
                                                     "\54\uffff";
    static final String    DFA59_eofS        =
                                                     "\1\2\53\uffff";
    static final String    DFA59_minS        =
                                                     "\1\10\1\0\52\uffff";
    static final String    DFA59_maxS        =
                                                     "\1\103\1\0\52\uffff";
    static final String    DFA59_acceptS     =
                                                     "\2\uffff\1\2\50\uffff\1\1";
    static final String    DFA59_specialS    =
                                                     "\1\uffff\1\0\52\uffff}>";
    static final String[]  DFA59_transitionS = {
                                             "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\12\2\2\uffff" +
                                                     "\10\2\1\1\3\2\1\uffff\17\2\5\uffff\2\2",
                                             "\1\uffff",
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
                                             ""
                                             };

    static final short[]   DFA59_eot         = DFA.unpackEncodedString( DFA59_eotS );
    static final short[]   DFA59_eof         = DFA.unpackEncodedString( DFA59_eofS );
    static final char[]    DFA59_min         = DFA.unpackEncodedStringToUnsignedChars( DFA59_minS );
    static final char[]    DFA59_max         = DFA.unpackEncodedStringToUnsignedChars( DFA59_maxS );
    static final short[]   DFA59_accept      = DFA.unpackEncodedString( DFA59_acceptS );
    static final short[]   DFA59_special     = DFA.unpackEncodedString( DFA59_specialS );
    static final short[][] DFA59_transition;

    static {
        int numStates = DFA59_transitionS.length;
        DFA59_transition = new short[numStates][];
        for ( int i = 0; i < numStates; i++ ) {
            DFA59_transition[i] = DFA.unpackEncodedString( DFA59_transitionS[i] );
        }
    }

    class DFA59 extends DFA {

        public DFA59(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 59;
            this.eot = DFA59_eot;
            this.eof = DFA59_eof;
            this.min = DFA59_min;
            this.max = DFA59_max;
            this.accept = DFA59_accept;
            this.special = DFA59_special;
            this.transition = DFA59_transition;
        }

        public String getDescription() {
            return "261:13: ( ( LEFT_PAREN )=> arguments )?";
        }

        public int specialStateTransition( int s,
                                           IntStream _input ) throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch ( s ) {
                case 0 :
                    int LA59_1 = input.LA( 1 );

                    int index59_1 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred39_DRLExpressions()) ) {
                        s = 43;
                    }

                    else if ( (true) ) {
                        s = 2;
                    }

                    input.seek( index59_1 );
                    if ( s >= 0 ) return s;
                    break;
            }
            if ( state.backtracking > 0 ) {
                state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                    new NoViableAltException( getDescription(),
                                              59,
                                              _s,
                                              input );
            error( nvae );
            throw nvae;
        }
    }

    static final String    DFA62_eotS        =
                                                     "\54\uffff";
    static final String    DFA62_eofS        =
                                                     "\1\2\53\uffff";
    static final String    DFA62_minS        =
                                                     "\1\10\1\0\52\uffff";
    static final String    DFA62_maxS        =
                                                     "\1\103\1\0\52\uffff";
    static final String    DFA62_acceptS     =
                                                     "\2\uffff\1\2\50\uffff\1\1";
    static final String    DFA62_specialS    =
                                                     "\1\uffff\1\0\52\uffff}>";
    static final String[]  DFA62_transitionS = {
                                             "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\12\2\2\uffff" +
                                                     "\10\2\1\1\3\2\1\uffff\17\2\5\uffff\2\2",
                                             "\1\uffff",
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
                                             ""
                                             };

    static final short[]   DFA62_eot         = DFA.unpackEncodedString( DFA62_eotS );
    static final short[]   DFA62_eof         = DFA.unpackEncodedString( DFA62_eofS );
    static final char[]    DFA62_min         = DFA.unpackEncodedStringToUnsignedChars( DFA62_minS );
    static final char[]    DFA62_max         = DFA.unpackEncodedStringToUnsignedChars( DFA62_maxS );
    static final short[]   DFA62_accept      = DFA.unpackEncodedString( DFA62_acceptS );
    static final short[]   DFA62_special     = DFA.unpackEncodedString( DFA62_specialS );
    static final short[][] DFA62_transition;

    static {
        int numStates = DFA62_transitionS.length;
        DFA62_transition = new short[numStates][];
        for ( int i = 0; i < numStates; i++ ) {
            DFA62_transition[i] = DFA.unpackEncodedString( DFA62_transitionS[i] );
        }
    }

    class DFA62 extends DFA {

        public DFA62(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 62;
            this.eot = DFA62_eot;
            this.eof = DFA62_eof;
            this.min = DFA62_min;
            this.max = DFA62_max;
            this.accept = DFA62_accept;
            this.special = DFA62_special;
            this.transition = DFA62_transition;
        }

        public String getDescription() {
            return "270:14: ( ( LEFT_PAREN )=> arguments )?";
        }

        public int specialStateTransition( int s,
                                           IntStream _input ) throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch ( s ) {
                case 0 :
                    int LA62_1 = input.LA( 1 );

                    int index62_1 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred43_DRLExpressions()) ) {
                        s = 43;
                    }

                    else if ( (true) ) {
                        s = 2;
                    }

                    input.seek( index62_1 );
                    if ( s >= 0 ) return s;
                    break;
            }
            if ( state.backtracking > 0 ) {
                state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                    new NoViableAltException( getDescription(),
                                              62,
                                              _s,
                                              input );
            error( nvae );
            throw nvae;
        }
    }

    static final String    DFA66_eotS        =
                                                     "\16\uffff";
    static final String    DFA66_eofS        =
                                                     "\16\uffff";
    static final String    DFA66_minS        =
                                                     "\1\25\12\uffff\1\0\2\uffff";
    static final String    DFA66_maxS        =
                                                     "\1\50\12\uffff\1\0\2\uffff";
    static final String    DFA66_acceptS     =
                                                     "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\uffff\1\13" +
                                                             "\1\14";
    static final String    DFA66_specialS    =
                                                     "\13\uffff\1\0\2\uffff}>";
    static final String[]  DFA66_transitionS = {
                                             "\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\11\uffff\1\13\1\12\1\1",
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
                                             "\1\uffff",
                                             "",
                                             ""
                                             };

    static final short[]   DFA66_eot         = DFA.unpackEncodedString( DFA66_eotS );
    static final short[]   DFA66_eof         = DFA.unpackEncodedString( DFA66_eofS );
    static final char[]    DFA66_min         = DFA.unpackEncodedStringToUnsignedChars( DFA66_minS );
    static final char[]    DFA66_max         = DFA.unpackEncodedStringToUnsignedChars( DFA66_maxS );
    static final short[]   DFA66_accept      = DFA.unpackEncodedString( DFA66_acceptS );
    static final short[]   DFA66_special     = DFA.unpackEncodedString( DFA66_specialS );
    static final short[][] DFA66_transition;

    static {
        int numStates = DFA66_transitionS.length;
        DFA66_transition = new short[numStates][];
        for ( int i = 0; i < numStates; i++ ) {
            DFA66_transition[i] = DFA.unpackEncodedString( DFA66_transitionS[i] );
        }
    }

    class DFA66 extends DFA {

        public DFA66(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 66;
            this.eot = DFA66_eot;
            this.eof = DFA66_eof;
            this.min = DFA66_min;
            this.max = DFA66_max;
            this.accept = DFA66_accept;
            this.special = DFA66_special;
            this.transition = DFA66_transition;
        }

        public String getDescription() {
            return "282:1: assignmentOperator options {k=1; } : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );";
        }

        public int specialStateTransition( int s,
                                           IntStream _input ) throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch ( s ) {
                case 0 :
                    int LA66_11 = input.LA( 1 );

                    int index66_11 = input.index();
                    input.rewind();
                    s = -1;
                    if ( (synpred44_DRLExpressions()) ) {
                        s = 12;
                    }

                    else if ( (synpred45_DRLExpressions()) ) {
                        s = 13;
                    }

                    input.seek( index66_11 );
                    if ( s >= 0 ) return s;
                    break;
            }
            if ( state.backtracking > 0 ) {
                state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                    new NoViableAltException( getDescription(),
                                              66,
                                              _s,
                                              input );
            error( nvae );
            throw nvae;
        }
    }

    public static final BitSet FOLLOW_STRING_in_literal66                                       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_DECIMAL_in_literal89                                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_HEX_in_literal98                                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_FLOAT_in_literal111                                       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_BOOL_in_literal122                                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_NULL_in_literal147                                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_type_in_typeList178                                       = new BitSet( new long[]{0x0000800000000002L} );
    public static final BitSet FOLLOW_COMMA_in_typeList181                                      = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_type_in_typeList183                                       = new BitSet( new long[]{0x0000800000000002L} );
    public static final BitSet FOLLOW_primitiveType_in_type220                                  = new BitSet( new long[]{0x0000080000000002L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_type230                                    = new BitSet( new long[]{0x0000100000000000L} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_type232                                   = new BitSet( new long[]{0x0000080000000002L} );
    public static final BitSet FOLLOW_ID_in_type243                                             = new BitSet( new long[]{0x0001088000000002L} );
    public static final BitSet FOLLOW_typeArguments_in_type250                                  = new BitSet( new long[]{0x0001080000000002L} );
    public static final BitSet FOLLOW_DOT_in_type255                                            = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_ID_in_type257                                             = new BitSet( new long[]{0x0001088000000002L} );
    public static final BitSet FOLLOW_typeArguments_in_type264                                  = new BitSet( new long[]{0x0001080000000002L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_type279                                    = new BitSet( new long[]{0x0000100000000000L} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_type281                                   = new BitSet( new long[]{0x0000080000000002L} );
    public static final BitSet FOLLOW_LESS_in_typeArguments296                                  = new BitSet( new long[]{0x0008000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_typeArgument_in_typeArguments298                          = new BitSet( new long[]{0x0000804000000000L} );
    public static final BitSet FOLLOW_COMMA_in_typeArguments301                                 = new BitSet( new long[]{0x0008000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_typeArgument_in_typeArguments303                          = new BitSet( new long[]{0x0000804000000000L} );
    public static final BitSet FOLLOW_GREATER_in_typeArguments307                               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_type_in_typeArgument319                                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_QUESTION_in_typeArgument324                               = new BitSet( new long[]{0x0000000000000002L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_extends_key_in_typeArgument328                            = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_super_key_in_typeArgument332                              = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_type_in_typeArgument335                                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_conditionalExpression_in_expression365                    = new BitSet( new long[]{0x000001C01FE00002L} );
    public static final BitSet FOLLOW_assignmentOperator_in_expression374                       = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_expression_in_expression376                               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression402       = new BitSet( new long[]{0x0008000000000002L} );
    public static final BitSet FOLLOW_QUESTION_in_conditionalExpression406                      = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_expression_in_conditionalExpression408                    = new BitSet( new long[]{0x0000000200000000L} );
    public static final BitSet FOLLOW_COLON_in_conditionalExpression410                         = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_expression_in_conditionalExpression412                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression430    = new BitSet( new long[]{0x0004000000000002L} );
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression434                 = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression436    = new BitSet( new long[]{0x0004000000000002L} );
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression455      = new BitSet( new long[]{0x0002000000000002L} );
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression459               = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression461      = new BitSet( new long[]{0x0002000000000002L} );
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression480         = new BitSet( new long[]{0x0040000000000002L} );
    public static final BitSet FOLLOW_PIPE_in_inclusiveOrExpression484                          = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression486         = new BitSet( new long[]{0x0040000000000002L} );
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression505                 = new BitSet( new long[]{0x0100000000000002L} );
    public static final BitSet FOLLOW_XOR_in_exclusiveOrExpression509                           = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression511                 = new BitSet( new long[]{0x0100000000000002L} );
    public static final BitSet FOLLOW_equalityExpression_in_andExpression530                    = new BitSet( new long[]{0x0080000000000002L} );
    public static final BitSet FOLLOW_AMPER_in_andExpression534                                 = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_equalityExpression_in_andExpression536                    = new BitSet( new long[]{0x0080000000000002L} );
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression555             = new BitSet( new long[]{0x0000000C00000002L} );
    public static final BitSet FOLLOW_set_in_equalityExpression559                              = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression569             = new BitSet( new long[]{0x0000000C00000002L} );
    public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression588           = new BitSet( new long[]{0x0000000000000002L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_instanceof_key_in_instanceOfExpression591                 = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_type_in_instanceOfExpression593                           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression611                = new BitSet( new long[]{0x000000F000000002L} );
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression620                   = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression622                = new BitSet( new long[]{0x000000F000000002L} );
    public static final BitSet FOLLOW_set_in_relationalOp640                                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression669                  = new BitSet( new long[]{0x000000C000000002L} );
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression677                             = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression679                  = new BitSet( new long[]{0x000000C000000002L} );
    public static final BitSet FOLLOW_LESS_in_shiftOp694                                        = new BitSet( new long[]{0x0000008000000000L} );
    public static final BitSet FOLLOW_LESS_in_shiftOp696                                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_GREATER_in_shiftOp700                                     = new BitSet( new long[]{0x0000004000000000L} );
    public static final BitSet FOLLOW_GREATER_in_shiftOp702                                     = new BitSet( new long[]{0x0000004000000000L} );
    public static final BitSet FOLLOW_GREATER_in_shiftOp704                                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_GREATER_in_shiftOp708                                     = new BitSet( new long[]{0x0000004000000000L} );
    public static final BitSet FOLLOW_GREATER_in_shiftOp710                                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression728         = new BitSet( new long[]{0x1800000000000002L} );
    public static final BitSet FOLLOW_set_in_additiveExpression739                              = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression747         = new BitSet( new long[]{0x1800000000000002L} );
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression766            = new BitSet( new long[]{0x0600000000000002L, 0x0000000000000008L} );
    public static final BitSet FOLLOW_set_in_multiplicativeExpression770                        = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression784            = new BitSet( new long[]{0x0600000000000002L, 0x0000000000000008L} );
    public static final BitSet FOLLOW_PLUS_in_unaryExpression804                                = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression806                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_MINUS_in_unaryExpression814                               = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression816                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_INCR_in_unaryExpression826                                = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_primary_in_unaryExpression828                             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_DECR_in_unaryExpression838                                = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_primary_in_unaryExpression840                             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression850         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_TILDE_in_unaryExpressionNotPlusMinus883                   = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus885         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus894                = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus896         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus906          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus916                 = new BitSet( new long[]{0x0001080060000002L} );
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus923                = new BitSet( new long[]{0x0001080060000002L} );
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus935                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression985                           = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_primitiveType_in_castExpression987                        = new BitSet( new long[]{0x0000040000000000L} );
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression989                          = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_unaryExpression_in_castExpression991                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression1008                          = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_type_in_castExpression1010                                = new BitSet( new long[]{0x0000040000000000L} );
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression1012                         = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression1014         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression1023                          = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_expression_in_castExpression1025                          = new BitSet( new long[]{0x0000040000000000L} );
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression1027                         = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression1029         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_boolean_key_in_primitiveType1066                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_char_key_in_primitiveType1074                             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_byte_key_in_primitiveType1082                             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_short_key_in_primitiveType1090                            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_int_key_in_primitiveType1098                              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_long_key_in_primitiveType1106                             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_float_key_in_primitiveType1114                            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_double_key_in_primitiveType1122                           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_parExpression_in_primary1145                              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_primary1160                   = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_primary1163            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_this_key_in_primary1167                                   = new BitSet( new long[]{0x0000020000000000L} );
    public static final BitSet FOLLOW_arguments_in_primary1169                                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_literal_in_primary1185                                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_super_key_in_primary1205                                  = new BitSet( new long[]{0x0001020000000000L} );
    public static final BitSet FOLLOW_superSuffix_in_primary1207                                = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_new_key_in_primary1222                                    = new BitSet( new long[]{0x0000008000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_creator_in_primary1224                                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_primitiveType_in_primary1239                              = new BitSet( new long[]{0x0001080000000000L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_primary1242                                = new BitSet( new long[]{0x0000100000000000L} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_primary1244                               = new BitSet( new long[]{0x0001080000000000L} );
    public static final BitSet FOLLOW_DOT_in_primary1248                                        = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_class_key_in_primary1250                                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_inlineMapExpression_in_primary1270                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_inlineListExpression_in_primary1285                       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_primary1299                                         = new BitSet( new long[]{0x00010A0000000002L} );
    public static final BitSet FOLLOW_DOT_in_primary1308                                        = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_ID_in_primary1310                                         = new BitSet( new long[]{0x00010A0000000002L} );
    public static final BitSet FOLLOW_identifierSuffix_in_primary1319                           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineListExpression1340                   = new BitSet( new long[]{0x18301A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_expressionList_in_inlineListExpression1342                = new BitSet( new long[]{0x0000100000000000L} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineListExpression1345                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineMapExpression1367                    = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_mapExpressionList_in_inlineMapExpression1369              = new BitSet( new long[]{0x18301A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineMapExpression1372                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_mapEntry_in_mapExpressionList1389                         = new BitSet( new long[]{0x0000800000000002L} );
    public static final BitSet FOLLOW_COMMA_in_mapExpressionList1392                            = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_mapEntry_in_mapExpressionList1394                         = new BitSet( new long[]{0x0000800000000002L} );
    public static final BitSet FOLLOW_expression_in_mapEntry1417                                = new BitSet( new long[]{0x0000000200000000L} );
    public static final BitSet FOLLOW_COLON_in_mapEntry1419                                     = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_expression_in_mapEntry1421                                = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_parExpression1435                           = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_expression_in_parExpression1437                           = new BitSet( new long[]{0x0000040000000000L} );
    public static final BitSet FOLLOW_RIGHT_PAREN_in_parExpression1439                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix1469                       = new BitSet( new long[]{0x0000100000000000L} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix1471                      = new BitSet( new long[]{0x0001080000000000L} );
    public static final BitSet FOLLOW_DOT_in_identifierSuffix1475                               = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_class_key_in_identifierSuffix1477                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix1492                       = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_expression_in_identifierSuffix1494                        = new BitSet( new long[]{0x0000100000000000L} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix1496                      = new BitSet( new long[]{0x0000080000000002L} );
    public static final BitSet FOLLOW_arguments_in_identifierSuffix1509                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator1527                   = new BitSet( new long[]{0x0000008000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_createdName_in_creator1530                                = new BitSet( new long[]{0x00000A0000000000L} );
    public static final BitSet FOLLOW_arrayCreatorRest_in_creator1541                           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_classCreatorRest_in_creator1545                           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_createdName1557                                     = new BitSet( new long[]{0x0001008000000002L} );
    public static final BitSet FOLLOW_typeArguments_in_createdName1559                          = new BitSet( new long[]{0x0001000000000002L} );
    public static final BitSet FOLLOW_DOT_in_createdName1572                                    = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_ID_in_createdName1574                                     = new BitSet( new long[]{0x0001008000000002L} );
    public static final BitSet FOLLOW_typeArguments_in_createdName1576                          = new BitSet( new long[]{0x0001000000000002L} );
    public static final BitSet FOLLOW_primitiveType_in_createdName1591                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_innerCreator1606                                    = new BitSet( new long[]{0x00000A0000000000L} );
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator1608                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1621                       = new BitSet( new long[]{0x18301A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1629                      = new BitSet( new long[]{0x0000280000000000L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1632                       = new BitSet( new long[]{0x0000100000000000L} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1634                      = new BitSet( new long[]{0x0000280000000000L} );
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest1638                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest1652                        = new BitSet( new long[]{0x0000100000000000L} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1654                      = new BitSet( new long[]{0x0000080000000002L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1659                       = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest1661                        = new BitSet( new long[]{0x0000100000000000L} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1663                      = new BitSet( new long[]{0x0000080000000002L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1675                       = new BitSet( new long[]{0x0000100000000000L} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1677                      = new BitSet( new long[]{0x0000080000000002L} );
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer1700               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_expression_in_variableInitializer1711                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_CURLY_in_arrayInitializer1723                        = new BitSet( new long[]{0x18306A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer1726               = new BitSet( new long[]{0x0000C00000000000L} );
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer1729                             = new BitSet( new long[]{0x18302A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer1731               = new BitSet( new long[]{0x0000C00000000000L} );
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer1736                             = new BitSet( new long[]{0x0000400000000000L} );
    public static final BitSet FOLLOW_RIGHT_CURLY_in_arrayInitializer1743                       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_arguments_in_classCreatorRest1754                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation1767 = new BitSet( new long[]{0x0000020000000000L} );
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocation1769                = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LESS_in_nonWildcardTypeArguments1781                      = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments1783                  = new BitSet( new long[]{0x0000004000000000L} );
    public static final BitSet FOLLOW_GREATER_in_nonWildcardTypeArguments1785                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_super_key_in_explicitGenericInvocationSuffix1797          = new BitSet( new long[]{0x0001020000000000L} );
    public static final BitSet FOLLOW_superSuffix_in_explicitGenericInvocationSuffix1799        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_explicitGenericInvocationSuffix1807                 = new BitSet( new long[]{0x0000020000000000L} );
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocationSuffix1809          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_DOT_in_selector1836                                       = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_ID_in_selector1838                                        = new BitSet( new long[]{0x0000020000000002L} );
    public static final BitSet FOLLOW_arguments_in_selector1847                                 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_DOT_in_selector1858                                       = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_super_key_in_selector1860                                 = new BitSet( new long[]{0x0001020000000000L} );
    public static final BitSet FOLLOW_superSuffix_in_selector1862                               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_DOT_in_selector1869                                       = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_new_key_in_selector1871                                   = new BitSet( new long[]{0x0000008000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_selector1874                  = new BitSet( new long[]{0x0000008000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_innerCreator_in_selector1878                              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_selector1885                               = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_expression_in_selector1887                                = new BitSet( new long[]{0x0000100000000000L} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_selector1889                              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_arguments_in_superSuffix1901                              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_DOT_in_superSuffix1909                                    = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_ID_in_superSuffix1911                                     = new BitSet( new long[]{0x0000020000000002L} );
    public static final BitSet FOLLOW_arguments_in_superSuffix1920                              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_arguments1954                               = new BitSet( new long[]{0x18300E80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_expressionList_in_arguments1956                           = new BitSet( new long[]{0x0000040000000000L} );
    public static final BitSet FOLLOW_RIGHT_PAREN_in_arguments1959                              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_expression_in_expressionList1975                          = new BitSet( new long[]{0x0000800000000002L} );
    public static final BitSet FOLLOW_COMMA_in_expressionList1978                               = new BitSet( new long[]{0x18300A80600C5900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_expression_in_expressionList1980                          = new BitSet( new long[]{0x0000800000000002L} );
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2007                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_assignmentOperator2021                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_MINUS_ASSIGN_in_assignmentOperator2035                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_MULT_ASSIGN_in_assignmentOperator2049                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_DIV_ASSIGN_in_assignmentOperator2063                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_AND_ASSIGN_in_assignmentOperator2077                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_OR_ASSIGN_in_assignmentOperator2091                       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_XOR_ASSIGN_in_assignmentOperator2105                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_MOD_ASSIGN_in_assignmentOperator2119                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LESS_in_assignmentOperator2133                            = new BitSet( new long[]{0x0000008000000000L} );
    public static final BitSet FOLLOW_LESS_in_assignmentOperator2135                            = new BitSet( new long[]{0x0000010000000000L} );
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2137                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2160                         = new BitSet( new long[]{0x0000004000000000L} );
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2162                         = new BitSet( new long[]{0x0000004000000000L} );
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2164                         = new BitSet( new long[]{0x0000010000000000L} );
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2166                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2187                         = new BitSet( new long[]{0x0000004000000000L} );
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2189                         = new BitSet( new long[]{0x0000010000000000L} );
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2191                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_annotation_in_annotations2206                             = new BitSet( new long[]{0x0000000000100002L} );
    public static final BitSet FOLLOW_AT_in_annotation2218                                      = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_annotationName_in_annotation2226                          = new BitSet( new long[]{0x0000020000000002L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_annotation2238                              = new BitSet( new long[]{0x0000040000000000L} );
    public static final BitSet FOLLOW_RIGHT_PAREN_in_annotation2240                             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_annotation2248                              = new BitSet( new long[]{0x18302A80601CD900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_elementValuePairs_in_annotation2250                       = new BitSet( new long[]{0x0000040000000000L} );
    public static final BitSet FOLLOW_RIGHT_PAREN_in_annotation2252                             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_annotationName2287                                  = new BitSet( new long[]{0x0001000000000002L} );
    public static final BitSet FOLLOW_DOT_in_annotationName2295                                 = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_ID_in_annotationName2299                                  = new BitSet( new long[]{0x0001000000000002L} );
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs2316                 = new BitSet( new long[]{0x0000800000000002L} );
    public static final BitSet FOLLOW_COMMA_in_elementValuePairs2319                            = new BitSet( new long[]{0x18302A80601CD900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs2321                 = new BitSet( new long[]{0x0000800000000002L} );
    public static final BitSet FOLLOW_ID_in_elementValuePair2344                                = new BitSet( new long[]{0x0000010000000000L} );
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_elementValuePair2346                     = new BitSet( new long[]{0x18302A80601CD900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_elementValue_in_elementValuePair2350                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_elementValue_in_elementValuePair2358                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_TimePeriod_in_elementValue2371                            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_conditionalExpression_in_elementValue2376                 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_annotation_in_elementValue2383                            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_elementValueArrayInitializer_in_elementValue2390          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_CURLY_in_elementValueArrayInitializer2401            = new BitSet( new long[]{0x18306A80601CD900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer2404          = new BitSet( new long[]{0x0000C00000000000L} );
    public static final BitSet FOLLOW_COMMA_in_elementValueArrayInitializer2407                 = new BitSet( new long[]{0x18302A80601CD900L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer2409          = new BitSet( new long[]{0x0000C00000000000L} );
    public static final BitSet FOLLOW_RIGHT_CURLY_in_elementValueArrayInitializer2416           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_extends_key2440                                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_super_key2461                                       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_instanceof_key2482                                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_boolean_key2503                                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_char_key2524                                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_byte_key2545                                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_short_key2566                                       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_int_key2587                                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_float_key2608                                       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_long_key2629                                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_double_key2650                                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_this_key2671                                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_class_key2692                                       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_new_key2713                                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_primitiveType_in_synpred1_DRLExpressions213               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred2_DRLExpressions224                 = new BitSet( new long[]{0x0000100000000000L} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred2_DRLExpressions226                = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_typeArguments_in_synpred3_DRLExpressions247               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_typeArguments_in_synpred4_DRLExpressions261               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred5_DRLExpressions273                 = new BitSet( new long[]{0x0000100000000000L} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred5_DRLExpressions275                = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_assignmentOperator_in_synpred6_DRLExpressions369          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LESS_in_synpred7_DRLExpressions616                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_shiftOp_in_synpred8_DRLExpressions674                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_set_in_synpred9_DRLExpressions732                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_castExpression_in_synpred12_DRLExpressions906             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_selector_in_synpred13_DRLExpressions920                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_set_in_synpred14_DRLExpressions928                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred15_DRLExpressions978                 = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_primitiveType_in_synpred15_DRLExpressions980              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred16_DRLExpressions1001                = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_type_in_synpred16_DRLExpressions1003                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_boolean_key_in_synpred17_DRLExpressions1066               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_char_key_in_synpred18_DRLExpressions1074                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_byte_key_in_synpred19_DRLExpressions1082                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_short_key_in_synpred20_DRLExpressions1090                 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_int_key_in_synpred21_DRLExpressions1098                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_long_key_in_synpred22_DRLExpressions1106                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_float_key_in_synpred23_DRLExpressions1114                 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_parExpression_in_synpred24_DRLExpressions1141             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred25_DRLExpressions1156  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_literal_in_synpred26_DRLExpressions1181                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_super_key_in_synpred27_DRLExpressions1201                 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_new_key_in_synpred28_DRLExpressions1218                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_primitiveType_in_synpred29_DRLExpressions1235             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_inlineMapExpression_in_synpred30_DRLExpressions1266       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_inlineListExpression_in_synpred31_DRLExpressions1281      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_synpred32_DRLExpressions1296                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_DOT_in_synpred33_DRLExpressions1303                       = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_ID_in_synpred33_DRLExpressions1305                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_identifierSuffix_in_synpred34_DRLExpressions1316          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred36_DRLExpressions1487               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred38_DRLExpressions1669               = new BitSet( new long[]{0x0000100000000000L} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred38_DRLExpressions1671              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred39_DRLExpressions1842                = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_DOT_in_synpred40_DRLExpressions1836                       = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_ID_in_synpred40_DRLExpressions1838                        = new BitSet( new long[]{0x0000020000000002L} );
    public static final BitSet FOLLOW_arguments_in_synpred40_DRLExpressions1847                 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_DOT_in_synpred41_DRLExpressions1858                       = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_super_key_in_synpred41_DRLExpressions1860                 = new BitSet( new long[]{0x0001020000000000L} );
    public static final BitSet FOLLOW_superSuffix_in_synpred41_DRLExpressions1862               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_DOT_in_synpred42_DRLExpressions1869                       = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_new_key_in_synpred42_DRLExpressions1871                   = new BitSet( new long[]{0x0000008000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred42_DRLExpressions1874  = new BitSet( new long[]{0x0000008000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_innerCreator_in_synpred42_DRLExpressions1878              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred43_DRLExpressions1915                = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_GREATER_in_synpred44_DRLExpressions2152                   = new BitSet( new long[]{0x0000004000000000L} );
    public static final BitSet FOLLOW_GREATER_in_synpred44_DRLExpressions2154                   = new BitSet( new long[]{0x0000004000000000L} );
    public static final BitSet FOLLOW_GREATER_in_synpred44_DRLExpressions2156                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_GREATER_in_synpred45_DRLExpressions2181                   = new BitSet( new long[]{0x0000004000000000L} );
    public static final BitSet FOLLOW_GREATER_in_synpred45_DRLExpressions2183                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_synpred46_DRLExpressions2336                        = new BitSet( new long[]{0x0000010000000000L} );
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_synpred46_DRLExpressions2338             = new BitSet( new long[]{0x0000000000000002L} );

}