// $ANTLR 3.0b5 D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g 2006-11-22 14:58:22

package org.drools.rule.builder.dialect.java.parser;

import java.util.Iterator;

import org.antlr.runtime.*;
import java.util.List;
import java.util.ArrayList;

public class JavaParserParser extends Parser {
    public static final String[] tokenNames        = new String[]{"<invalid>", "<EOR>", "<DOWN>", "<UP>", "LBRACK", "RBRACK", "IDENT", "DOT", "STAR", "LCURLY", "SEMI", "RCURLY", "COMMA", "LPAREN", "RPAREN", "ASSIGN", "COLON", "PLUS_ASSIGN",
            "MINUS_ASSIGN", "STAR_ASSIGN", "DIV_ASSIGN", "MOD_ASSIGN", "SR_ASSIGN", "BSR_ASSIGN", "SL_ASSIGN", "BAND_ASSIGN", "BXOR_ASSIGN", "BOR_ASSIGN", "QUESTION", "LOR", "LAND", "BOR", "BXOR", "BAND", "NOT_EQUAL", "EQUAL", "LT", "GT", "LE",
            "GE", "SL", "SR", "BSR", "PLUS", "MINUS", "DIV", "MOD", "INC", "DEC", "BNOT", "LNOT", "NUM_INT", "CHAR_LITERAL", "STRING_LITERAL", "NUM_FLOAT", "WS", "SL_COMMENT", "ML_COMMENT", "DECIMAL_LITERAL", "HEX_LITERAL", "OCTAL_LITERAL",
            "DIGITS", "EXPONENT_PART", "FLOAT_TYPE_SUFFIX", "ESCAPE_SEQUENCE", "OCTAL_DIGIT", "UNICODE_CHAR", "HEX_DIGIT", "'void'", "'boolean'", "'byte'", "'char'", "'short'", "'int'", "'float'", "'long'", "'double'", "'private'", "'public'",
            "'protected'", "'static'", "'transient'", "'final'", "'abstract'", "'native'", "'threadsafe'", "'synchronized'", "'volatile'", "'strictfp'", "'class'", "'extends'", "'interface'", "'implements'", "'this'", "'super'", "'throws'", "'if'",
            "'else'", "'for'", "'while'", "'do'", "'break'", "'continue'", "'return'", "'switch'", "'throw'", "'case'", "'default'", "'try'", "'finally'", "'catch'", "'instanceof'", "'true'", "'false'", "'null'", "'new'"};
    public static final int      COMMA             = 12;
    public static final int      SR_ASSIGN         = 22;
    public static final int      MINUS             = 44;
    public static final int      LOR               = 29;
    public static final int      BNOT              = 49;
    public static final int      INC               = 47;
    public static final int      MOD               = 46;
    public static final int      OCTAL_LITERAL     = 60;
    public static final int      PLUS_ASSIGN       = 17;
    public static final int      QUESTION          = 28;
    public static final int      HEX_LITERAL       = 59;
    public static final int      BOR               = 31;
    public static final int      DOT               = 7;
    public static final int      SR                = 41;
    public static final int      FLOAT_TYPE_SUFFIX = 63;
    public static final int      RCURLY            = 11;
    public static final int      LCURLY            = 9;
    public static final int      CHAR_LITERAL      = 52;
    public static final int      BOR_ASSIGN        = 27;
    public static final int      STRING_LITERAL    = 53;
    public static final int      ASSIGN            = 15;
    public static final int      LE                = 38;
    public static final int      RPAREN            = 14;
    public static final int      STAR_ASSIGN       = 19;
    public static final int      NUM_INT           = 51;
    public static final int      LPAREN            = 13;
    public static final int      HEX_DIGIT         = 67;
    public static final int      ML_COMMENT        = 57;
    public static final int      PLUS              = 43;
    public static final int      SL_COMMENT        = 56;
    public static final int      BAND              = 33;
    public static final int      MINUS_ASSIGN      = 18;
    public static final int      NOT_EQUAL         = 34;
    public static final int      BAND_ASSIGN       = 25;
    public static final int      DIGITS            = 61;
    public static final int      DECIMAL_LITERAL   = 58;
    public static final int      IDENT             = 6;
    public static final int      MOD_ASSIGN        = 21;
    public static final int      WS                = 55;
    public static final int      BXOR_ASSIGN       = 26;
    public static final int      LT                = 36;
    public static final int      BSR               = 42;
    public static final int      GT                = 37;
    public static final int      BSR_ASSIGN        = 23;
    public static final int      SL_ASSIGN         = 24;
    public static final int      LAND              = 30;
    public static final int      LBRACK            = 4;
    public static final int      NUM_FLOAT         = 54;
    public static final int      SEMI              = 10;
    public static final int      GE                = 39;
    public static final int      LNOT              = 50;
    public static final int      DIV_ASSIGN        = 20;
    public static final int      UNICODE_CHAR      = 66;
    public static final int      DEC               = 48;
    public static final int      EQUAL             = 35;
    public static final int      ESCAPE_SEQUENCE   = 64;
    public static final int      EOF               = -1;
    public static final int      OCTAL_DIGIT       = 65;
    public static final int      RBRACK            = 5;
    public static final int      COLON             = 16;
    public static final int      SL                = 40;
    public static final int      DIV               = 45;
    public static final int      STAR              = 8;
    public static final int      BXOR              = 32;
    public static final int      EXPONENT_PART     = 62;

    public JavaParserParser(final TokenStream input) {
        super( input );
    }

    public String[] getTokenNames() {
        return tokenNames;
    }

    public String getGrammarFileName() {
        return "D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g";
    }

    private final List identifiers = new ArrayList();

    public List getIdentifiers() {
        return this.identifiers;
    }

    public static final CommonToken IGNORE_TOKEN = new CommonToken( null,
                                                                    0,
                                                                    99,
                                                                    0,
                                                                    0 );
    private final List                    errors       = new ArrayList();

    private String                  source       = "unknown";

    public void setSource(final String source) {
        this.source = source;
    }

    public String getSource() {
        return this.source;
    }

    public void reportError(final RecognitionException ex) {
        // if we've already reported an error and have not matched a token
        // yet successfully, don't report any errors.
        if ( this.errorRecovery ) {
            //System.err.print("[SPURIOUS] ");
            return;
        }
        this.errorRecovery = true;

        this.errors.add( ex );
    }

    /** return the raw RecognitionException errors */
    public List getErrors() {
        return this.errors;
    }

    /** Return a list of pretty strings summarising the errors */
    public List getErrorMessages() {
        final List messages = new ArrayList();
        for ( final Iterator errorIter = this.errors.iterator(); errorIter.hasNext(); ) {
            messages.add( createErrorMessage( (RecognitionException) errorIter.next() ) );
        }
        return messages;
    }

    /** return true if any parser errors were accumulated */
    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    /** This will take a RecognitionException, and create a sensible error message out of it */
    public String createErrorMessage(final RecognitionException e) {
        final StringBuffer message = new StringBuffer();
        message.append( this.source + ":" + e.line + ":" + e.charPositionInLine + " " );
        if ( e instanceof MismatchedTokenException ) {
            final MismatchedTokenException mte = (MismatchedTokenException) e;
            message.append( "mismatched token: " + e.token + "; expecting type " + tokenNames[mte.expecting] );
        } else if ( e instanceof MismatchedTreeNodeException ) {
            final MismatchedTreeNodeException mtne = (MismatchedTreeNodeException) e;
            message.append( "mismatched tree node: " + mtne.foundNode + "; expecting type " + tokenNames[mtne.expecting] );
        } else if ( e instanceof NoViableAltException ) {
            final NoViableAltException nvae = (NoViableAltException) e;
            message.append( "Unexpected token '" + e.token.getText() + "'" );
            /*
             message.append("decision=<<"+nvae.grammarDecisionDescription+">>"+
             " state "+nvae.stateNumber+
             " (decision="+nvae.decisionNumber+
             ") no viable alt; token="+
             e.token);
             */
        } else if ( e instanceof EarlyExitException ) {
            final EarlyExitException eee = (EarlyExitException) e;
            message.append( "required (...)+ loop (decision=" + eee.decisionNumber + ") did not match anything; token=" + e.token );
        } else if ( e instanceof MismatchedSetException ) {
            final MismatchedSetException mse = (MismatchedSetException) e;
            message.append( "mismatched token '" + e.token + "' expecting set " + mse.expecting );
        } else if ( e instanceof MismatchedNotSetException ) {
            final MismatchedNotSetException mse = (MismatchedNotSetException) e;
            message.append( "mismatched token '" + e.token + "' expecting set " + mse.expecting );
        } else if ( e instanceof FailedPredicateException ) {
            final FailedPredicateException fpe = (FailedPredicateException) e;
            message.append( "rule " + fpe.ruleName + " failed predicate: {" + fpe.predicateText + "}?" );
        }
        return message.toString();
    }

    // $ANTLR start declaration
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:139:1: declaration : modifiers typeSpec variableDefinitions ;
    public void declaration() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:143:4: ( modifiers typeSpec variableDefinitions )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:143:4: modifiers typeSpec variableDefinitions
            {
                pushFollow( FOLLOW_modifiers_in_declaration59 );
                modifiers();
                this._fsp--;

                pushFollow( FOLLOW_typeSpec_in_declaration61 );
                typeSpec();
                this._fsp--;

                pushFollow( FOLLOW_variableDefinitions_in_declaration63 );
                variableDefinitions();
                this._fsp--;

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end declaration

    // $ANTLR start typeSpec
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:149:1: typeSpec : ( classTypeSpec | builtInTypeSpec );
    public void typeSpec() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:150:4: ( classTypeSpec | builtInTypeSpec )
            int alt1 = 2;
            final int LA1_0 = this.input.LA( 1 );
            if ( (LA1_0 == IDENT) ) {
                alt1 = 1;
            } else if ( ((LA1_0 >= 68 && LA1_0 <= 76)) ) {
                alt1 = 2;
            } else {
                final NoViableAltException nvae = new NoViableAltException( "149:1: typeSpec : ( classTypeSpec | builtInTypeSpec );",
                                                                      1,
                                                                      0,
                                                                      this.input );

                throw nvae;
            }
            switch ( alt1 ) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:150:4: classTypeSpec
                {
                    pushFollow( FOLLOW_classTypeSpec_in_typeSpec79 );
                    classTypeSpec();
                    this._fsp--;

                }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:151:4: builtInTypeSpec
                {
                    pushFollow( FOLLOW_builtInTypeSpec_in_typeSpec84 );
                    builtInTypeSpec();
                    this._fsp--;

                }
                    break;

            }
        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end typeSpec

    // $ANTLR start classTypeSpec
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:156:1: classTypeSpec : identifier ( LBRACK RBRACK )* ;
    public void classTypeSpec() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:157:4: ( identifier ( LBRACK RBRACK )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:157:4: identifier ( LBRACK RBRACK )*
            {
                pushFollow( FOLLOW_identifier_in_classTypeSpec97 );
                identifier();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:157:15: ( LBRACK RBRACK )*
                loop2 : do {
                    int alt2 = 2;
                    final int LA2_0 = this.input.LA( 1 );
                    if ( (LA2_0 == LBRACK) ) {
                        alt2 = 1;
                    }

                    switch ( alt2 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:157:16: LBRACK RBRACK
                        {
                            match( this.input,
                                   LBRACK,
                                   FOLLOW_LBRACK_in_classTypeSpec100 );
                            match( this.input,
                                   RBRACK,
                                   FOLLOW_RBRACK_in_classTypeSpec103 );

                        }
                            break;

                        default :
                            break loop2;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end classTypeSpec

    // $ANTLR start builtInTypeSpec
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:162:1: builtInTypeSpec : builtInType ( LBRACK RBRACK )* ;
    public void builtInTypeSpec() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:163:4: ( builtInType ( LBRACK RBRACK )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:163:4: builtInType ( LBRACK RBRACK )*
            {
                pushFollow( FOLLOW_builtInType_in_builtInTypeSpec118 );
                builtInType();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:163:16: ( LBRACK RBRACK )*
                loop3 : do {
                    int alt3 = 2;
                    final int LA3_0 = this.input.LA( 1 );
                    if ( (LA3_0 == LBRACK) ) {
                        alt3 = 1;
                    }

                    switch ( alt3 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:163:17: LBRACK RBRACK
                        {
                            match( this.input,
                                   LBRACK,
                                   FOLLOW_LBRACK_in_builtInTypeSpec121 );
                            match( this.input,
                                   RBRACK,
                                   FOLLOW_RBRACK_in_builtInTypeSpec124 );

                        }
                            break;

                        default :
                            break loop3;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end builtInTypeSpec

    // $ANTLR start type
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:168:1: type : ( identifier | builtInType );
    public void type() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:169:4: ( identifier | builtInType )
            int alt4 = 2;
            final int LA4_0 = this.input.LA( 1 );
            if ( (LA4_0 == IDENT) ) {
                alt4 = 1;
            } else if ( ((LA4_0 >= 68 && LA4_0 <= 76)) ) {
                alt4 = 2;
            } else {
                final NoViableAltException nvae = new NoViableAltException( "168:1: type : ( identifier | builtInType );",
                                                                      4,
                                                                      0,
                                                                      this.input );

                throw nvae;
            }
            switch ( alt4 ) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:169:4: identifier
                {
                    pushFollow( FOLLOW_identifier_in_type139 );
                    identifier();
                    this._fsp--;

                }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:170:4: builtInType
                {
                    pushFollow( FOLLOW_builtInType_in_type144 );
                    builtInType();
                    this._fsp--;

                }
                    break;

            }
        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end type

    // $ANTLR start builtInType
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:174:1: builtInType : ('void'|'boolean'|'byte'|'char'|'short'|'int'|'float'|'long'|'double');
    public void builtInType() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:175:2: ( ('void'|'boolean'|'byte'|'char'|'short'|'int'|'float'|'long'|'double'))
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:175:4: ('void'|'boolean'|'byte'|'char'|'short'|'int'|'float'|'long'|'double')
            {
                if ( (this.input.LA( 1 ) >= 68 && this.input.LA( 1 ) <= 76) ) {
                    this.input.consume();
                    this.errorRecovery = false;
                } else {
                    final MismatchedSetException mse = new MismatchedSetException( null,
                                                                             this.input );
                    recoverFromMismatchedSet( this.input,
                                              mse,
                                              FOLLOW_set_in_builtInType156 );
                    throw mse;
                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end builtInType

    // $ANTLR start identifier
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:188:1: identifier : IDENT ( DOT IDENT )* ;
    public void identifier() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:189:4: ( IDENT ( DOT IDENT )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:189:4: IDENT ( DOT IDENT )*
            {
                match( this.input,
                       IDENT,
                       FOLLOW_IDENT_in_identifier209 );
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:189:11: ( DOT IDENT )*
                loop5 : do {
                    int alt5 = 2;
                    final int LA5_0 = this.input.LA( 1 );
                    if ( (LA5_0 == DOT) ) {
                        alt5 = 1;
                    }

                    switch ( alt5 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:189:13: DOT IDENT
                        {
                            match( this.input,
                                   DOT,
                                   FOLLOW_DOT_in_identifier214 );
                            match( this.input,
                                   IDENT,
                                   FOLLOW_IDENT_in_identifier216 );

                        }
                            break;

                        default :
                            break loop5;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end identifier

    // $ANTLR start identifierStar
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:192:1: identifierStar : IDENT ( DOT IDENT )* ( DOT STAR )? ;
    public void identifierStar() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:193:4: ( IDENT ( DOT IDENT )* ( DOT STAR )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:193:4: IDENT ( DOT IDENT )* ( DOT STAR )?
            {
                match( this.input,
                       IDENT,
                       FOLLOW_IDENT_in_identifierStar230 );
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:194:3: ( DOT IDENT )*
                loop6 : do {
                    int alt6 = 2;
                    final int LA6_0 = this.input.LA( 1 );
                    if ( (LA6_0 == DOT) ) {
                        final int LA6_1 = this.input.LA( 2 );
                        if ( (LA6_1 == IDENT) ) {
                            alt6 = 1;
                        }

                    }

                    switch ( alt6 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:194:5: DOT IDENT
                        {
                            match( this.input,
                                   DOT,
                                   FOLLOW_DOT_in_identifierStar236 );
                            match( this.input,
                                   IDENT,
                                   FOLLOW_IDENT_in_identifierStar238 );

                        }
                            break;

                        default :
                            break loop6;
                    }
                } while ( true );

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:195:3: ( DOT STAR )?
                int alt7 = 2;
                final int LA7_0 = this.input.LA( 1 );
                if ( (LA7_0 == DOT) ) {
                    alt7 = 1;
                }
                switch ( alt7 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:195:5: DOT STAR
                    {
                        match( this.input,
                               DOT,
                               FOLLOW_DOT_in_identifierStar247 );
                        match( this.input,
                               STAR,
                               FOLLOW_STAR_in_identifierStar249 );

                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end identifierStar

    // $ANTLR start modifiers
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:202:1: modifiers : ( modifier )* ;
    public void modifiers() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:203:4: ( ( modifier )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:203:4: ( modifier )*
            {
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:203:4: ( modifier )*
                loop8 : do {
                    int alt8 = 2;
                    final int LA8_0 = this.input.LA( 1 );
                    if ( ((LA8_0 >= 77 && LA8_0 <= 88)) ) {
                        alt8 = 1;
                    }

                    switch ( alt8 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:203:6: modifier
                        {
                            pushFollow( FOLLOW_modifier_in_modifiers270 );
                            modifier();
                            this._fsp--;

                        }
                            break;

                        default :
                            break loop8;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end modifiers

    // $ANTLR start modifier
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:208:1: modifier : ('private'|'public'|'protected'|'static'|'transient'|'final'|'abstract'|'native'|'threadsafe'|'synchronized'|'volatile'|'strictfp');
    public void modifier() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:209:2: ( ('private'|'public'|'protected'|'static'|'transient'|'final'|'abstract'|'native'|'threadsafe'|'synchronized'|'volatile'|'strictfp'))
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:209:4: ('private'|'public'|'protected'|'static'|'transient'|'final'|'abstract'|'native'|'threadsafe'|'synchronized'|'volatile'|'strictfp')
            {
                if ( (this.input.LA( 1 ) >= 77 && this.input.LA( 1 ) <= 88) ) {
                    this.input.consume();
                    this.errorRecovery = false;
                } else {
                    final MismatchedSetException mse = new MismatchedSetException( null,
                                                                             this.input );
                    recoverFromMismatchedSet( this.input,
                                              mse,
                                              FOLLOW_set_in_modifier288 );
                    throw mse;
                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end modifier

    // $ANTLR start classDefinition
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:225:1: classDefinition : 'class' IDENT superClassClause implementsClause classBlock ;
    public void classDefinition() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:226:4: ( 'class' IDENT superClassClause implementsClause classBlock )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:226:4: 'class' IDENT superClassClause implementsClause classBlock
            {
                match( this.input,
                       89,
                       FOLLOW_89_in_classDefinition356 );
                match( this.input,
                       IDENT,
                       FOLLOW_IDENT_in_classDefinition358 );
                pushFollow( FOLLOW_superClassClause_in_classDefinition365 );
                superClassClause();
                this._fsp--;

                pushFollow( FOLLOW_implementsClause_in_classDefinition372 );
                implementsClause();
                this._fsp--;

                pushFollow( FOLLOW_classBlock_in_classDefinition379 );
                classBlock();
                this._fsp--;

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end classDefinition

    // $ANTLR start superClassClause
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:235:1: superClassClause : ( 'extends' identifier )? ;
    public void superClassClause() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:236:4: ( ( 'extends' identifier )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:236:4: ( 'extends' identifier )?
            {
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:236:4: ( 'extends' identifier )?
                int alt9 = 2;
                final int LA9_0 = this.input.LA( 1 );
                if ( (LA9_0 == 90) ) {
                    alt9 = 1;
                }
                switch ( alt9 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:236:6: 'extends' identifier
                    {
                        match( this.input,
                               90,
                               FOLLOW_90_in_superClassClause392 );
                        pushFollow( FOLLOW_identifier_in_superClassClause394 );
                        identifier();
                        this._fsp--;

                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end superClassClause

    // $ANTLR start interfaceDefinition
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:241:1: interfaceDefinition : 'interface' IDENT interfaceExtends classBlock ;
    public void interfaceDefinition() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:242:4: ( 'interface' IDENT interfaceExtends classBlock )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:242:4: 'interface' IDENT interfaceExtends classBlock
            {
                match( this.input,
                       91,
                       FOLLOW_91_in_interfaceDefinition412 );
                match( this.input,
                       IDENT,
                       FOLLOW_IDENT_in_interfaceDefinition414 );
                pushFollow( FOLLOW_interfaceExtends_in_interfaceDefinition421 );
                interfaceExtends();
                this._fsp--;

                pushFollow( FOLLOW_classBlock_in_interfaceDefinition428 );
                classBlock();
                this._fsp--;

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end interfaceDefinition

    // $ANTLR start classBlock
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:252:1: classBlock : LCURLY ( field | SEMI )* RCURLY ;
    public void classBlock() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:253:4: ( LCURLY ( field | SEMI )* RCURLY )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:253:4: LCURLY ( field | SEMI )* RCURLY
            {
                match( this.input,
                       LCURLY,
                       FOLLOW_LCURLY_in_classBlock442 );
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:254:4: ( field | SEMI )*
                loop10 : do {
                    int alt10 = 3;
                    final int LA10_0 = this.input.LA( 1 );
                    if ( (LA10_0 == IDENT || LA10_0 == LCURLY || (LA10_0 >= 68 && LA10_0 <= 89) || LA10_0 == 91) ) {
                        alt10 = 1;
                    } else if ( (LA10_0 == SEMI) ) {
                        alt10 = 2;
                    }

                    switch ( alt10 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:254:6: field
                        {
                            pushFollow( FOLLOW_field_in_classBlock449 );
                            field();
                            this._fsp--;

                        }
                            break;
                        case 2 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:254:14: SEMI
                        {
                            match( this.input,
                                   SEMI,
                                   FOLLOW_SEMI_in_classBlock453 );

                        }
                            break;

                        default :
                            break loop10;
                    }
                } while ( true );

                match( this.input,
                       RCURLY,
                       FOLLOW_RCURLY_in_classBlock460 );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end classBlock

    // $ANTLR start interfaceExtends
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:260:1: interfaceExtends : ( 'extends' identifier ( COMMA identifier )* )? ;
    public void interfaceExtends() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:261:4: ( ( 'extends' identifier ( COMMA identifier )* )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:261:4: ( 'extends' identifier ( COMMA identifier )* )?
            {
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:261:4: ( 'extends' identifier ( COMMA identifier )* )?
                int alt12 = 2;
                final int LA12_0 = this.input.LA( 1 );
                if ( (LA12_0 == 90) ) {
                    alt12 = 1;
                }
                switch ( alt12 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:262:3: 'extends' identifier ( COMMA identifier )*
                    {
                        match( this.input,
                               90,
                               FOLLOW_90_in_interfaceExtends479 );
                        pushFollow( FOLLOW_identifier_in_interfaceExtends483 );
                        identifier();
                        this._fsp--;

                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:263:14: ( COMMA identifier )*
                        loop11 : do {
                            int alt11 = 2;
                            final int LA11_0 = this.input.LA( 1 );
                            if ( (LA11_0 == COMMA) ) {
                                alt11 = 1;
                            }

                            switch ( alt11 ) {
                                case 1 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:263:16: COMMA identifier
                                {
                                    match( this.input,
                                           COMMA,
                                           FOLLOW_COMMA_in_interfaceExtends487 );
                                    pushFollow( FOLLOW_identifier_in_interfaceExtends489 );
                                    identifier();
                                    this._fsp--;

                                }
                                    break;

                                default :
                                    break loop11;
                            }
                        } while ( true );

                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end interfaceExtends

    // $ANTLR start implementsClause
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:268:1: implementsClause : ( 'implements' identifier ( COMMA identifier )* )? ;
    public void implementsClause() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:269:4: ( ( 'implements' identifier ( COMMA identifier )* )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:269:4: ( 'implements' identifier ( COMMA identifier )* )?
            {
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:269:4: ( 'implements' identifier ( COMMA identifier )* )?
                int alt14 = 2;
                final int LA14_0 = this.input.LA( 1 );
                if ( (LA14_0 == 92) ) {
                    alt14 = 1;
                }
                switch ( alt14 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:270:4: 'implements' identifier ( COMMA identifier )*
                    {
                        match( this.input,
                               92,
                               FOLLOW_92_in_implementsClause514 );
                        pushFollow( FOLLOW_identifier_in_implementsClause516 );
                        identifier();
                        this._fsp--;

                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:270:28: ( COMMA identifier )*
                        loop13 : do {
                            int alt13 = 2;
                            final int LA13_0 = this.input.LA( 1 );
                            if ( (LA13_0 == COMMA) ) {
                                alt13 = 1;
                            }

                            switch ( alt13 ) {
                                case 1 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:270:30: COMMA identifier
                                {
                                    match( this.input,
                                           COMMA,
                                           FOLLOW_COMMA_in_implementsClause520 );
                                    pushFollow( FOLLOW_identifier_in_implementsClause522 );
                                    identifier();
                                    this._fsp--;

                                }
                                    break;

                                default :
                                    break loop13;
                            }
                        } while ( true );

                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end implementsClause

    // $ANTLR start field
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:278:1: field : ( modifiers ( ctorHead constructorBody | classDefinition | interfaceDefinition | typeSpec ( IDENT LPAREN parameterDeclarationList RPAREN declaratorBrackets ( throwsClause )? ( compoundStatement | SEMI ) | variableDefinitions SEMI ) ) | 'static' compoundStatement | compoundStatement );
    public void field() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:280:3: ( modifiers ( ctorHead constructorBody | classDefinition | interfaceDefinition | typeSpec ( IDENT LPAREN parameterDeclarationList RPAREN declaratorBrackets ( throwsClause )? ( compoundStatement | SEMI ) | variableDefinitions SEMI ) ) | 'static' compoundStatement | compoundStatement )
            int alt19 = 3;
            switch ( this.input.LA( 1 ) ) {
                case 80 :
                    final int LA19_1 = this.input.LA( 2 );
                    if ( (LA19_1 == LCURLY) ) {
                        alt19 = 2;
                    } else if ( (LA19_1 == IDENT || (LA19_1 >= 68 && LA19_1 <= 89) || LA19_1 == 91) ) {
                        alt19 = 1;
                    } else {
                        final NoViableAltException nvae = new NoViableAltException( "278:1: field : ( modifiers ( ctorHead constructorBody | classDefinition | interfaceDefinition | typeSpec ( IDENT LPAREN parameterDeclarationList RPAREN declaratorBrackets ( throwsClause )? ( compoundStatement | SEMI ) | variableDefinitions SEMI ) ) | 'static' compoundStatement | compoundStatement );",
                                                                              19,
                                                                              1,
                                                                              this.input );

                        throw nvae;
                    }
                    break;
                case IDENT :
                case 68 :
                case 69 :
                case 70 :
                case 71 :
                case 72 :
                case 73 :
                case 74 :
                case 75 :
                case 76 :
                case 77 :
                case 78 :
                case 79 :
                case 81 :
                case 82 :
                case 83 :
                case 84 :
                case 85 :
                case 86 :
                case 87 :
                case 88 :
                case 89 :
                case 91 :
                    alt19 = 1;
                    break;
                case LCURLY :
                    alt19 = 3;
                    break;
                default :
                    final NoViableAltException nvae = new NoViableAltException( "278:1: field : ( modifiers ( ctorHead constructorBody | classDefinition | interfaceDefinition | typeSpec ( IDENT LPAREN parameterDeclarationList RPAREN declaratorBrackets ( throwsClause )? ( compoundStatement | SEMI ) | variableDefinitions SEMI ) ) | 'static' compoundStatement | compoundStatement );",
                                                                          19,
                                                                          0,
                                                                          this.input );

                    throw nvae;
            }

            switch ( alt19 ) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:280:3: modifiers ( ctorHead constructorBody | classDefinition | interfaceDefinition | typeSpec ( IDENT LPAREN parameterDeclarationList RPAREN declaratorBrackets ( throwsClause )? ( compoundStatement | SEMI ) | variableDefinitions SEMI ) )
                {
                    pushFollow( FOLLOW_modifiers_in_field548 );
                    modifiers();
                    this._fsp--;

                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:281:3: ( ctorHead constructorBody | classDefinition | interfaceDefinition | typeSpec ( IDENT LPAREN parameterDeclarationList RPAREN declaratorBrackets ( throwsClause )? ( compoundStatement | SEMI ) | variableDefinitions SEMI ) )
                    int alt18 = 4;
                    switch ( this.input.LA( 1 ) ) {
                        case IDENT :
                            final int LA18_1 = this.input.LA( 2 );
                            if ( (LA18_1 == LPAREN) ) {
                                alt18 = 1;
                            } else if ( (LA18_1 == LBRACK || (LA18_1 >= IDENT && LA18_1 <= DOT)) ) {
                                alt18 = 4;
                            } else {
                                final NoViableAltException nvae = new NoViableAltException( "281:3: ( ctorHead constructorBody | classDefinition | interfaceDefinition | typeSpec ( IDENT LPAREN parameterDeclarationList RPAREN declaratorBrackets ( throwsClause )? ( compoundStatement | SEMI ) | variableDefinitions SEMI ) )",
                                                                                      18,
                                                                                      1,
                                                                                      this.input );

                                throw nvae;
                            }
                            break;
                        case 89 :
                            alt18 = 2;
                            break;
                        case 91 :
                            alt18 = 3;
                            break;
                        case 68 :
                        case 69 :
                        case 70 :
                        case 71 :
                        case 72 :
                        case 73 :
                        case 74 :
                        case 75 :
                        case 76 :
                            alt18 = 4;
                            break;
                        default :
                            final NoViableAltException nvae = new NoViableAltException( "281:3: ( ctorHead constructorBody | classDefinition | interfaceDefinition | typeSpec ( IDENT LPAREN parameterDeclarationList RPAREN declaratorBrackets ( throwsClause )? ( compoundStatement | SEMI ) | variableDefinitions SEMI ) )",
                                                                                  18,
                                                                                  0,
                                                                                  this.input );

                            throw nvae;
                    }

                    switch ( alt18 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:281:5: ctorHead constructorBody
                        {
                            pushFollow( FOLLOW_ctorHead_in_field554 );
                            ctorHead();
                            this._fsp--;

                            pushFollow( FOLLOW_constructorBody_in_field556 );
                            constructorBody();
                            this._fsp--;

                        }
                            break;
                        case 2 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:284:5: classDefinition
                        {
                            pushFollow( FOLLOW_classDefinition_in_field568 );
                            classDefinition();
                            this._fsp--;

                        }
                            break;
                        case 3 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:287:5: interfaceDefinition
                        {
                            pushFollow( FOLLOW_interfaceDefinition_in_field586 );
                            interfaceDefinition();
                            this._fsp--;

                        }
                            break;
                        case 4 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:290:5: typeSpec ( IDENT LPAREN parameterDeclarationList RPAREN declaratorBrackets ( throwsClause )? ( compoundStatement | SEMI ) | variableDefinitions SEMI )
                        {
                            pushFollow( FOLLOW_typeSpec_in_field600 );
                            typeSpec();
                            this._fsp--;

                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:291:4: ( IDENT LPAREN parameterDeclarationList RPAREN declaratorBrackets ( throwsClause )? ( compoundStatement | SEMI ) | variableDefinitions SEMI )
                            int alt17 = 2;
                            final int LA17_0 = this.input.LA( 1 );
                            if ( (LA17_0 == IDENT) ) {
                                final int LA17_1 = this.input.LA( 2 );
                                if ( (LA17_1 == LPAREN) ) {
                                    alt17 = 1;
                                } else if ( (LA17_1 == LBRACK || LA17_1 == SEMI || LA17_1 == COMMA || LA17_1 == ASSIGN) ) {
                                    alt17 = 2;
                                } else {
                                    final NoViableAltException nvae = new NoViableAltException( "291:4: ( IDENT LPAREN parameterDeclarationList RPAREN declaratorBrackets ( throwsClause )? ( compoundStatement | SEMI ) | variableDefinitions SEMI )",
                                                                                          17,
                                                                                          1,
                                                                                          this.input );

                                    throw nvae;
                                }
                            } else {
                                final NoViableAltException nvae = new NoViableAltException( "291:4: ( IDENT LPAREN parameterDeclarationList RPAREN declaratorBrackets ( throwsClause )? ( compoundStatement | SEMI ) | variableDefinitions SEMI )",
                                                                                      17,
                                                                                      0,
                                                                                      this.input );

                                throw nvae;
                            }
                            switch ( alt17 ) {
                                case 1 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:291:6: IDENT LPAREN parameterDeclarationList RPAREN declaratorBrackets ( throwsClause )? ( compoundStatement | SEMI )
                                {
                                    match( this.input,
                                           IDENT,
                                           FOLLOW_IDENT_in_field609 );
                                    match( this.input,
                                           LPAREN,
                                           FOLLOW_LPAREN_in_field623 );
                                    pushFollow( FOLLOW_parameterDeclarationList_in_field625 );
                                    parameterDeclarationList();
                                    this._fsp--;

                                    match( this.input,
                                           RPAREN,
                                           FOLLOW_RPAREN_in_field627 );
                                    pushFollow( FOLLOW_declaratorBrackets_in_field634 );
                                    declaratorBrackets();
                                    this._fsp--;

                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:300:5: ( throwsClause )?
                                    int alt15 = 2;
                                    final int LA15_0 = this.input.LA( 1 );
                                    if ( (LA15_0 == 95) ) {
                                        alt15 = 1;
                                    }
                                    switch ( alt15 ) {
                                        case 1 :
                                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:300:6: throwsClause
                                        {
                                            pushFollow( FOLLOW_throwsClause_in_field652 );
                                            throwsClause();
                                            this._fsp--;

                                        }
                                            break;

                                    }

                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:302:5: ( compoundStatement | SEMI )
                                    int alt16 = 2;
                                    final int LA16_0 = this.input.LA( 1 );
                                    if ( (LA16_0 == LCURLY) ) {
                                        alt16 = 1;
                                    } else if ( (LA16_0 == SEMI) ) {
                                        alt16 = 2;
                                    } else {
                                        final NoViableAltException nvae = new NoViableAltException( "302:5: ( compoundStatement | SEMI )",
                                                                                              16,
                                                                                              0,
                                                                                              this.input );

                                        throw nvae;
                                    }
                                    switch ( alt16 ) {
                                        case 1 :
                                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:302:7: compoundStatement
                                        {
                                            pushFollow( FOLLOW_compoundStatement_in_field663 );
                                            compoundStatement();
                                            this._fsp--;

                                        }
                                            break;
                                        case 2 :
                                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:302:27: SEMI
                                        {
                                            match( this.input,
                                                   SEMI,
                                                   FOLLOW_SEMI_in_field667 );

                                        }
                                            break;

                                    }

                                }
                                    break;
                                case 2 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:303:6: variableDefinitions SEMI
                                {
                                    pushFollow( FOLLOW_variableDefinitions_in_field676 );
                                    variableDefinitions();
                                    this._fsp--;

                                    match( this.input,
                                           SEMI,
                                           FOLLOW_SEMI_in_field678 );

                                }
                                    break;

                            }

                        }
                            break;

                    }

                }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:310:4: 'static' compoundStatement
                {
                    match( this.input,
                           80,
                           FOLLOW_80_in_field704 );
                    pushFollow( FOLLOW_compoundStatement_in_field706 );
                    compoundStatement();
                    this._fsp--;

                }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:314:4: compoundStatement
                {
                    pushFollow( FOLLOW_compoundStatement_in_field720 );
                    compoundStatement();
                    this._fsp--;

                }
                    break;

            }
        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end field

    // $ANTLR start constructorBody
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:318:1: constructorBody : LCURLY ( options {greedy=true; } : explicitConstructorInvocation )? ( statement )* RCURLY ;
    public void constructorBody() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:319:9: ( LCURLY ( options {greedy=true; } : explicitConstructorInvocation )? ( statement )* RCURLY )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:319:9: LCURLY ( options {greedy=true; } : explicitConstructorInvocation )? ( statement )* RCURLY
            {
                match( this.input,
                       LCURLY,
                       FOLLOW_LCURLY_in_constructorBody739 );
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:320:13: ( options {greedy=true; } : explicitConstructorInvocation )?
                int alt20 = 2;
                final int LA20_0 = this.input.LA( 1 );
                if ( (LA20_0 == 93) ) {
                    final int LA20_1 = this.input.LA( 2 );
                    if ( (LA20_1 == LPAREN) ) {
                        alt20 = 1;
                    }
                } else if ( (LA20_0 == 94) ) {
                    final int LA20_2 = this.input.LA( 2 );
                    if ( (LA20_2 == LPAREN) ) {
                        alt20 = 1;
                    }
                }
                switch ( alt20 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:320:40: explicitConstructorInvocation
                    {
                        pushFollow( FOLLOW_explicitConstructorInvocation_in_constructorBody765 );
                        explicitConstructorInvocation();
                        this._fsp--;

                    }
                        break;

                }

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:321:13: ( statement )*
                loop21 : do {
                    int alt21 = 2;
                    final int LA21_0 = this.input.LA( 1 );
                    if ( (LA21_0 == IDENT || (LA21_0 >= LCURLY && LA21_0 <= SEMI) || LA21_0 == LPAREN || (LA21_0 >= PLUS && LA21_0 <= MINUS) || (LA21_0 >= INC && LA21_0 <= NUM_FLOAT) || (LA21_0 >= 68 && LA21_0 <= 89)
                          || (LA21_0 >= 93 && LA21_0 <= 94) || LA21_0 == 96 || (LA21_0 >= 98 && LA21_0 <= 105) || LA21_0 == 108 || (LA21_0 >= 112 && LA21_0 <= 115)) ) {
                        alt21 = 1;
                    }

                    switch ( alt21 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:321:14: statement
                        {
                            pushFollow( FOLLOW_statement_in_constructorBody782 );
                            statement();
                            this._fsp--;

                        }
                            break;

                        default :
                            break loop21;
                    }
                } while ( true );

                match( this.input,
                       RCURLY,
                       FOLLOW_RCURLY_in_constructorBody794 );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end constructorBody

    // $ANTLR start explicitConstructorInvocation
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:325:1: explicitConstructorInvocation : ( 'this' LPAREN argList RPAREN SEMI | 'super' LPAREN argList RPAREN SEMI );
    public void explicitConstructorInvocation() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:327:9: ( 'this' LPAREN argList RPAREN SEMI | 'super' LPAREN argList RPAREN SEMI )
            int alt22 = 2;
            final int LA22_0 = this.input.LA( 1 );
            if ( (LA22_0 == 93) ) {
                alt22 = 1;
            } else if ( (LA22_0 == 94) ) {
                alt22 = 2;
            } else {
                final NoViableAltException nvae = new NoViableAltException( "325:1: explicitConstructorInvocation : ( 'this' LPAREN argList RPAREN SEMI | 'super' LPAREN argList RPAREN SEMI );",
                                                                      22,
                                                                      0,
                                                                      this.input );

                throw nvae;
            }
            switch ( alt22 ) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:327:9: 'this' LPAREN argList RPAREN SEMI
                {
                    match( this.input,
                           93,
                           FOLLOW_93_in_explicitConstructorInvocation815 );
                    match( this.input,
                           LPAREN,
                           FOLLOW_LPAREN_in_explicitConstructorInvocation817 );
                    pushFollow( FOLLOW_argList_in_explicitConstructorInvocation819 );
                    argList();
                    this._fsp--;

                    match( this.input,
                           RPAREN,
                           FOLLOW_RPAREN_in_explicitConstructorInvocation821 );
                    match( this.input,
                           SEMI,
                           FOLLOW_SEMI_in_explicitConstructorInvocation823 );

                }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:329:9: 'super' LPAREN argList RPAREN SEMI
                {
                    match( this.input,
                           94,
                           FOLLOW_94_in_explicitConstructorInvocation836 );
                    match( this.input,
                           LPAREN,
                           FOLLOW_LPAREN_in_explicitConstructorInvocation838 );
                    pushFollow( FOLLOW_argList_in_explicitConstructorInvocation840 );
                    argList();
                    this._fsp--;

                    match( this.input,
                           RPAREN,
                           FOLLOW_RPAREN_in_explicitConstructorInvocation842 );
                    match( this.input,
                           SEMI,
                           FOLLOW_SEMI_in_explicitConstructorInvocation844 );

                }
                    break;

            }
        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end explicitConstructorInvocation

    // $ANTLR start variableDefinitions
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:333:1: variableDefinitions : variableDeclarator ( COMMA variableDeclarator )* ;
    public void variableDefinitions() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:334:4: ( variableDeclarator ( COMMA variableDeclarator )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:334:4: variableDeclarator ( COMMA variableDeclarator )*
            {
                pushFollow( FOLLOW_variableDeclarator_in_variableDefinitions861 );
                variableDeclarator();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:335:3: ( COMMA variableDeclarator )*
                loop23 : do {
                    int alt23 = 2;
                    final int LA23_0 = this.input.LA( 1 );
                    if ( (LA23_0 == COMMA) ) {
                        alt23 = 1;
                    }

                    switch ( alt23 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:335:5: COMMA variableDeclarator
                        {
                            match( this.input,
                                   COMMA,
                                   FOLLOW_COMMA_in_variableDefinitions867 );
                            pushFollow( FOLLOW_variableDeclarator_in_variableDefinitions872 );
                            variableDeclarator();
                            this._fsp--;

                        }
                            break;

                        default :
                            break loop23;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end variableDefinitions

    // $ANTLR start variableDeclarator
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:340:1: variableDeclarator : IDENT declaratorBrackets varInitializer ;
    public void variableDeclarator() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:345:4: ( IDENT declaratorBrackets varInitializer )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:345:4: IDENT declaratorBrackets varInitializer
            {
                match( this.input,
                       IDENT,
                       FOLLOW_IDENT_in_variableDeclarator890 );
                pushFollow( FOLLOW_declaratorBrackets_in_variableDeclarator892 );
                declaratorBrackets();
                this._fsp--;

                pushFollow( FOLLOW_varInitializer_in_variableDeclarator894 );
                varInitializer();
                this._fsp--;

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end variableDeclarator

    // $ANTLR start declaratorBrackets
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:349:1: declaratorBrackets : ( LBRACK RBRACK )* ;
    public void declaratorBrackets() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:351:3: ( ( LBRACK RBRACK )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:351:3: ( LBRACK RBRACK )*
            {
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:351:3: ( LBRACK RBRACK )*
                loop24 : do {
                    int alt24 = 2;
                    final int LA24_0 = this.input.LA( 1 );
                    if ( (LA24_0 == LBRACK) ) {
                        alt24 = 1;
                    }

                    switch ( alt24 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:351:4: LBRACK RBRACK
                        {
                            match( this.input,
                                   LBRACK,
                                   FOLLOW_LBRACK_in_declaratorBrackets912 );
                            match( this.input,
                                   RBRACK,
                                   FOLLOW_RBRACK_in_declaratorBrackets915 );

                        }
                            break;

                        default :
                            break loop24;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end declaratorBrackets

    // $ANTLR start varInitializer
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:354:1: varInitializer : ( ASSIGN initializer )? ;
    public void varInitializer() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:355:4: ( ( ASSIGN initializer )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:355:4: ( ASSIGN initializer )?
            {
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:355:4: ( ASSIGN initializer )?
                int alt25 = 2;
                final int LA25_0 = this.input.LA( 1 );
                if ( (LA25_0 == ASSIGN) ) {
                    alt25 = 1;
                }
                switch ( alt25 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:355:6: ASSIGN initializer
                    {
                        match( this.input,
                               ASSIGN,
                               FOLLOW_ASSIGN_in_varInitializer930 );
                        pushFollow( FOLLOW_initializer_in_varInitializer932 );
                        initializer();
                        this._fsp--;

                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end varInitializer

    // $ANTLR start arrayInitializer
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:359:1: arrayInitializer : LCURLY ( initializer ( COMMA initializer )* ( COMMA )? )? RCURLY ;
    public void arrayInitializer() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:360:4: ( LCURLY ( initializer ( COMMA initializer )* ( COMMA )? )? RCURLY )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:360:4: LCURLY ( initializer ( COMMA initializer )* ( COMMA )? )? RCURLY
            {
                match( this.input,
                       LCURLY,
                       FOLLOW_LCURLY_in_arrayInitializer947 );
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:361:4: ( initializer ( COMMA initializer )* ( COMMA )? )?
                int alt28 = 2;
                final int LA28_0 = this.input.LA( 1 );
                if ( (LA28_0 == IDENT || LA28_0 == LCURLY || LA28_0 == LPAREN || (LA28_0 >= PLUS && LA28_0 <= MINUS) || (LA28_0 >= INC && LA28_0 <= NUM_FLOAT) || (LA28_0 >= 68 && LA28_0 <= 76) || (LA28_0 >= 93 && LA28_0 <= 94) || (LA28_0 >= 112 && LA28_0 <= 115)) ) {
                    alt28 = 1;
                }
                switch ( alt28 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:361:6: initializer ( COMMA initializer )* ( COMMA )?
                    {
                        pushFollow( FOLLOW_initializer_in_arrayInitializer955 );
                        initializer();
                        this._fsp--;

                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:362:5: ( COMMA initializer )*
                        loop26 : do {
                            int alt26 = 2;
                            final int LA26_0 = this.input.LA( 1 );
                            if ( (LA26_0 == COMMA) ) {
                                final int LA26_1 = this.input.LA( 2 );
                                if ( (LA26_1 == IDENT || LA26_1 == LCURLY || LA26_1 == LPAREN || (LA26_1 >= PLUS && LA26_1 <= MINUS) || (LA26_1 >= INC && LA26_1 <= NUM_FLOAT) || (LA26_1 >= 68 && LA26_1 <= 76) || (LA26_1 >= 93 && LA26_1 <= 94) || (LA26_1 >= 112 && LA26_1 <= 115)) ) {
                                    alt26 = 1;
                                }

                            }

                            switch ( alt26 ) {
                                case 1 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:367:6: COMMA initializer
                                {
                                    match( this.input,
                                           COMMA,
                                           FOLLOW_COMMA_in_arrayInitializer992 );
                                    pushFollow( FOLLOW_initializer_in_arrayInitializer994 );
                                    initializer();
                                    this._fsp--;

                                }
                                    break;

                                default :
                                    break loop26;
                            }
                        } while ( true );

                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:369:5: ( COMMA )?
                        int alt27 = 2;
                        final int LA27_0 = this.input.LA( 1 );
                        if ( (LA27_0 == COMMA) ) {
                            alt27 = 1;
                        }
                        switch ( alt27 ) {
                            case 1 :
                                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:369:6: COMMA
                            {
                                match( this.input,
                                       COMMA,
                                       FOLLOW_COMMA_in_arrayInitializer1008 );

                            }
                                break;

                        }

                    }
                        break;

                }

                match( this.input,
                       RCURLY,
                       FOLLOW_RCURLY_in_arrayInitializer1020 );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end arrayInitializer

    // $ANTLR start initializer
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:377:1: initializer : ( expression | arrayInitializer );
    public void initializer() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:378:4: ( expression | arrayInitializer )
            int alt29 = 2;
            final int LA29_0 = this.input.LA( 1 );
            if ( (LA29_0 == IDENT || LA29_0 == LPAREN || (LA29_0 >= PLUS && LA29_0 <= MINUS) || (LA29_0 >= INC && LA29_0 <= NUM_FLOAT) || (LA29_0 >= 68 && LA29_0 <= 76) || (LA29_0 >= 93 && LA29_0 <= 94) || (LA29_0 >= 112 && LA29_0 <= 115)) ) {
                alt29 = 1;
            } else if ( (LA29_0 == LCURLY) ) {
                alt29 = 2;
            } else {
                final NoViableAltException nvae = new NoViableAltException( "377:1: initializer : ( expression | arrayInitializer );",
                                                                      29,
                                                                      0,
                                                                      this.input );

                throw nvae;
            }
            switch ( alt29 ) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:378:4: expression
                {
                    pushFollow( FOLLOW_expression_in_initializer1034 );
                    expression();
                    this._fsp--;

                }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:379:4: arrayInitializer
                {
                    pushFollow( FOLLOW_arrayInitializer_in_initializer1039 );
                    arrayInitializer();
                    this._fsp--;

                }
                    break;

            }
        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end initializer

    // $ANTLR start ctorHead
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:385:1: ctorHead : IDENT LPAREN parameterDeclarationList RPAREN ( throwsClause )? ;
    public void ctorHead() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:386:4: ( IDENT LPAREN parameterDeclarationList RPAREN ( throwsClause )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:386:4: IDENT LPAREN parameterDeclarationList RPAREN ( throwsClause )?
            {
                match( this.input,
                       IDENT,
                       FOLLOW_IDENT_in_ctorHead1053 );
                match( this.input,
                       LPAREN,
                       FOLLOW_LPAREN_in_ctorHead1063 );
                pushFollow( FOLLOW_parameterDeclarationList_in_ctorHead1065 );
                parameterDeclarationList();
                this._fsp--;

                match( this.input,
                       RPAREN,
                       FOLLOW_RPAREN_in_ctorHead1067 );
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:392:3: ( throwsClause )?
                int alt30 = 2;
                final int LA30_0 = this.input.LA( 1 );
                if ( (LA30_0 == 95) ) {
                    alt30 = 1;
                }
                switch ( alt30 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:392:4: throwsClause
                    {
                        pushFollow( FOLLOW_throwsClause_in_ctorHead1076 );
                        throwsClause();
                        this._fsp--;

                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end ctorHead

    // $ANTLR start throwsClause
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:396:1: throwsClause : 'throws' identifier ( COMMA identifier )* ;
    public void throwsClause() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:397:4: ( 'throws' identifier ( COMMA identifier )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:397:4: 'throws' identifier ( COMMA identifier )*
            {
                match( this.input,
                       95,
                       FOLLOW_95_in_throwsClause1090 );
                pushFollow( FOLLOW_identifier_in_throwsClause1092 );
                identifier();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:397:24: ( COMMA identifier )*
                loop31 : do {
                    int alt31 = 2;
                    final int LA31_0 = this.input.LA( 1 );
                    if ( (LA31_0 == COMMA) ) {
                        alt31 = 1;
                    }

                    switch ( alt31 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:397:26: COMMA identifier
                        {
                            match( this.input,
                                   COMMA,
                                   FOLLOW_COMMA_in_throwsClause1096 );
                            pushFollow( FOLLOW_identifier_in_throwsClause1098 );
                            identifier();
                            this._fsp--;

                        }
                            break;

                        default :
                            break loop31;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end throwsClause

    // $ANTLR start parameterDeclarationList
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:402:1: parameterDeclarationList : ( parameterDeclaration ( COMMA parameterDeclaration )* )? ;
    public void parameterDeclarationList() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:403:4: ( ( parameterDeclaration ( COMMA parameterDeclaration )* )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:403:4: ( parameterDeclaration ( COMMA parameterDeclaration )* )?
            {
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:403:4: ( parameterDeclaration ( COMMA parameterDeclaration )* )?
                int alt33 = 2;
                final int LA33_0 = this.input.LA( 1 );
                if ( (LA33_0 == IDENT || (LA33_0 >= 68 && LA33_0 <= 76) || LA33_0 == 82) ) {
                    alt33 = 1;
                }
                switch ( alt33 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:403:6: parameterDeclaration ( COMMA parameterDeclaration )*
                    {
                        pushFollow( FOLLOW_parameterDeclaration_in_parameterDeclarationList1116 );
                        parameterDeclaration();
                        this._fsp--;

                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:403:27: ( COMMA parameterDeclaration )*
                        loop32 : do {
                            int alt32 = 2;
                            final int LA32_0 = this.input.LA( 1 );
                            if ( (LA32_0 == COMMA) ) {
                                alt32 = 1;
                            }

                            switch ( alt32 ) {
                                case 1 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:403:29: COMMA parameterDeclaration
                                {
                                    match( this.input,
                                           COMMA,
                                           FOLLOW_COMMA_in_parameterDeclarationList1120 );
                                    pushFollow( FOLLOW_parameterDeclaration_in_parameterDeclarationList1122 );
                                    parameterDeclaration();
                                    this._fsp--;

                                }
                                    break;

                                default :
                                    break loop32;
                            }
                        } while ( true );

                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end parameterDeclarationList

    // $ANTLR start parameterDeclaration
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:407:1: parameterDeclaration : parameterModifier typeSpec IDENT declaratorBrackets ;
    public void parameterDeclaration() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:408:4: ( parameterModifier typeSpec IDENT declaratorBrackets )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:408:4: parameterModifier typeSpec IDENT declaratorBrackets
            {
                pushFollow( FOLLOW_parameterModifier_in_parameterDeclaration1140 );
                parameterModifier();
                this._fsp--;

                pushFollow( FOLLOW_typeSpec_in_parameterDeclaration1142 );
                typeSpec();
                this._fsp--;

                match( this.input,
                       IDENT,
                       FOLLOW_IDENT_in_parameterDeclaration1144 );
                pushFollow( FOLLOW_declaratorBrackets_in_parameterDeclaration1148 );
                declaratorBrackets();
                this._fsp--;

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end parameterDeclaration

    // $ANTLR start parameterModifier
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:412:1: parameterModifier : ( 'final' )? ;
    public void parameterModifier() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:413:4: ( ( 'final' )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:413:4: ( 'final' )?
            {
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:413:4: ( 'final' )?
                int alt34 = 2;
                final int LA34_0 = this.input.LA( 1 );
                if ( (LA34_0 == 82) ) {
                    alt34 = 1;
                }
                switch ( alt34 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:413:5: 'final'
                    {
                        match( this.input,
                               82,
                               FOLLOW_82_in_parameterModifier1160 );

                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end parameterModifier

    // $ANTLR start compoundStatement
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:426:1: compoundStatement : LCURLY ( statement )* RCURLY ;
    public void compoundStatement() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:427:4: ( LCURLY ( statement )* RCURLY )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:427:4: LCURLY ( statement )* RCURLY
            {
                match( this.input,
                       LCURLY,
                       FOLLOW_LCURLY_in_compoundStatement1185 );
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:429:4: ( statement )*
                loop35 : do {
                    int alt35 = 2;
                    final int LA35_0 = this.input.LA( 1 );
                    if ( (LA35_0 == IDENT || (LA35_0 >= LCURLY && LA35_0 <= SEMI) || LA35_0 == LPAREN || (LA35_0 >= PLUS && LA35_0 <= MINUS) || (LA35_0 >= INC && LA35_0 <= NUM_FLOAT) || (LA35_0 >= 68 && LA35_0 <= 89)
                          || (LA35_0 >= 93 && LA35_0 <= 94) || LA35_0 == 96 || (LA35_0 >= 98 && LA35_0 <= 105) || LA35_0 == 108 || (LA35_0 >= 112 && LA35_0 <= 115)) ) {
                        alt35 = 1;
                    }

                    switch ( alt35 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:429:5: statement
                        {
                            pushFollow( FOLLOW_statement_in_compoundStatement1196 );
                            statement();
                            this._fsp--;

                        }
                            break;

                        default :
                            break loop35;
                    }
                } while ( true );

                match( this.input,
                       RCURLY,
                       FOLLOW_RCURLY_in_compoundStatement1202 );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end compoundStatement

    // $ANTLR start statement
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:434:1: statement : ( compoundStatement | declaration SEMI | expression SEMI | modifiers classDefinition | IDENT COLON statement | 'if' LPAREN expression RPAREN statement ( 'else' statement )? | 'for' LPAREN forInit SEMI forCond SEMI forIter RPAREN statement | 'while' LPAREN expression RPAREN statement | 'do' statement 'while' LPAREN expression RPAREN SEMI | 'break' ( IDENT )? SEMI | 'continue' ( IDENT )? SEMI | 'return' ( expression )? SEMI | 'switch' LPAREN expression RPAREN LCURLY ( casesGroup )* RCURLY | tryBlock | 'throw' expression SEMI | 'synchronized' LPAREN expression RPAREN compoundStatement | SEMI );
    public void statement() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:436:4: ( compoundStatement | declaration SEMI | expression SEMI | modifiers classDefinition | IDENT COLON statement | 'if' LPAREN expression RPAREN statement ( 'else' statement )? | 'for' LPAREN forInit SEMI forCond SEMI forIter RPAREN statement | 'while' LPAREN expression RPAREN statement | 'do' statement 'while' LPAREN expression RPAREN SEMI | 'break' ( IDENT )? SEMI | 'continue' ( IDENT )? SEMI | 'return' ( expression )? SEMI | 'switch' LPAREN expression RPAREN LCURLY ( casesGroup )* RCURLY | tryBlock | 'throw' expression SEMI | 'synchronized' LPAREN expression RPAREN compoundStatement | SEMI )
            int alt41 = 17;
            alt41 = this.dfa41.predict( this.input );
            switch ( alt41 ) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:436:4: compoundStatement
                {
                    pushFollow( FOLLOW_compoundStatement_in_statement1216 );
                    compoundStatement();
                    this._fsp--;

                }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:443:4: declaration SEMI
                {
                    pushFollow( FOLLOW_declaration_in_statement1232 );
                    declaration();
                    this._fsp--;

                    match( this.input,
                           SEMI,
                           FOLLOW_SEMI_in_statement1234 );

                }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:448:4: expression SEMI
                {
                    pushFollow( FOLLOW_expression_in_statement1246 );
                    expression();
                    this._fsp--;

                    match( this.input,
                           SEMI,
                           FOLLOW_SEMI_in_statement1248 );

                }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:451:4: modifiers classDefinition
                {
                    pushFollow( FOLLOW_modifiers_in_statement1256 );
                    modifiers();
                    this._fsp--;

                    pushFollow( FOLLOW_classDefinition_in_statement1258 );
                    classDefinition();
                    this._fsp--;

                }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:454:4: IDENT COLON statement
                {
                    match( this.input,
                           IDENT,
                           FOLLOW_IDENT_in_statement1266 );
                    match( this.input,
                           COLON,
                           FOLLOW_COLON_in_statement1268 );
                    pushFollow( FOLLOW_statement_in_statement1271 );
                    statement();
                    this._fsp--;

                }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:457:4: 'if' LPAREN expression RPAREN statement ( 'else' statement )?
                {
                    match( this.input,
                           96,
                           FOLLOW_96_in_statement1279 );
                    match( this.input,
                           LPAREN,
                           FOLLOW_LPAREN_in_statement1281 );
                    pushFollow( FOLLOW_expression_in_statement1283 );
                    expression();
                    this._fsp--;

                    match( this.input,
                           RPAREN,
                           FOLLOW_RPAREN_in_statement1285 );
                    pushFollow( FOLLOW_statement_in_statement1287 );
                    statement();
                    this._fsp--;

                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:458:3: ( 'else' statement )?
                    int alt36 = 2;
                    final int LA36_0 = this.input.LA( 1 );
                    if ( (LA36_0 == 97) ) {
                        alt36 = 1;
                    }
                    switch ( alt36 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:462:4: 'else' statement
                        {
                            match( this.input,
                                   97,
                                   FOLLOW_97_in_statement1308 );
                            pushFollow( FOLLOW_statement_in_statement1310 );
                            statement();
                            this._fsp--;

                        }
                            break;

                    }

                }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:466:4: 'for' LPAREN forInit SEMI forCond SEMI forIter RPAREN statement
                {
                    match( this.input,
                           98,
                           FOLLOW_98_in_statement1323 );
                    match( this.input,
                           LPAREN,
                           FOLLOW_LPAREN_in_statement1328 );
                    pushFollow( FOLLOW_forInit_in_statement1334 );
                    forInit();
                    this._fsp--;

                    match( this.input,
                           SEMI,
                           FOLLOW_SEMI_in_statement1336 );
                    pushFollow( FOLLOW_forCond_in_statement1345 );
                    forCond();
                    this._fsp--;

                    match( this.input,
                           SEMI,
                           FOLLOW_SEMI_in_statement1347 );
                    pushFollow( FOLLOW_forIter_in_statement1356 );
                    forIter();
                    this._fsp--;

                    match( this.input,
                           RPAREN,
                           FOLLOW_RPAREN_in_statement1370 );
                    pushFollow( FOLLOW_statement_in_statement1375 );
                    statement();
                    this._fsp--;

                }
                    break;
                case 8 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:475:4: 'while' LPAREN expression RPAREN statement
                {
                    match( this.input,
                           99,
                           FOLLOW_99_in_statement1404 );
                    match( this.input,
                           LPAREN,
                           FOLLOW_LPAREN_in_statement1406 );
                    pushFollow( FOLLOW_expression_in_statement1408 );
                    expression();
                    this._fsp--;

                    match( this.input,
                           RPAREN,
                           FOLLOW_RPAREN_in_statement1410 );
                    pushFollow( FOLLOW_statement_in_statement1412 );
                    statement();
                    this._fsp--;

                }
                    break;
                case 9 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:478:4: 'do' statement 'while' LPAREN expression RPAREN SEMI
                {
                    match( this.input,
                           100,
                           FOLLOW_100_in_statement1420 );
                    pushFollow( FOLLOW_statement_in_statement1422 );
                    statement();
                    this._fsp--;

                    match( this.input,
                           99,
                           FOLLOW_99_in_statement1424 );
                    match( this.input,
                           LPAREN,
                           FOLLOW_LPAREN_in_statement1426 );
                    pushFollow( FOLLOW_expression_in_statement1428 );
                    expression();
                    this._fsp--;

                    match( this.input,
                           RPAREN,
                           FOLLOW_RPAREN_in_statement1430 );
                    match( this.input,
                           SEMI,
                           FOLLOW_SEMI_in_statement1432 );

                }
                    break;
                case 10 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:481:4: 'break' ( IDENT )? SEMI
                {
                    match( this.input,
                           101,
                           FOLLOW_101_in_statement1440 );
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:481:12: ( IDENT )?
                    int alt37 = 2;
                    final int LA37_0 = this.input.LA( 1 );
                    if ( (LA37_0 == IDENT) ) {
                        alt37 = 1;
                    }
                    switch ( alt37 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:481:13: IDENT
                        {
                            match( this.input,
                                   IDENT,
                                   FOLLOW_IDENT_in_statement1443 );

                        }
                            break;

                    }

                    match( this.input,
                           SEMI,
                           FOLLOW_SEMI_in_statement1447 );

                }
                    break;
                case 11 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:484:4: 'continue' ( IDENT )? SEMI
                {
                    match( this.input,
                           102,
                           FOLLOW_102_in_statement1455 );
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:484:15: ( IDENT )?
                    int alt38 = 2;
                    final int LA38_0 = this.input.LA( 1 );
                    if ( (LA38_0 == IDENT) ) {
                        alt38 = 1;
                    }
                    switch ( alt38 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:484:16: IDENT
                        {
                            match( this.input,
                                   IDENT,
                                   FOLLOW_IDENT_in_statement1458 );

                        }
                            break;

                    }

                    match( this.input,
                           SEMI,
                           FOLLOW_SEMI_in_statement1462 );

                }
                    break;
                case 12 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:487:4: 'return' ( expression )? SEMI
                {
                    match( this.input,
                           103,
                           FOLLOW_103_in_statement1470 );
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:487:13: ( expression )?
                    int alt39 = 2;
                    final int LA39_0 = this.input.LA( 1 );
                    if ( (LA39_0 == IDENT || LA39_0 == LPAREN || (LA39_0 >= PLUS && LA39_0 <= MINUS) || (LA39_0 >= INC && LA39_0 <= NUM_FLOAT) || (LA39_0 >= 68 && LA39_0 <= 76) || (LA39_0 >= 93 && LA39_0 <= 94) || (LA39_0 >= 112 && LA39_0 <= 115)) ) {
                        alt39 = 1;
                    }
                    switch ( alt39 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:487:14: expression
                        {
                            pushFollow( FOLLOW_expression_in_statement1473 );
                            expression();
                            this._fsp--;

                        }
                            break;

                    }

                    match( this.input,
                           SEMI,
                           FOLLOW_SEMI_in_statement1477 );

                }
                    break;
                case 13 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:490:4: 'switch' LPAREN expression RPAREN LCURLY ( casesGroup )* RCURLY
                {
                    match( this.input,
                           104,
                           FOLLOW_104_in_statement1485 );
                    match( this.input,
                           LPAREN,
                           FOLLOW_LPAREN_in_statement1487 );
                    pushFollow( FOLLOW_expression_in_statement1489 );
                    expression();
                    this._fsp--;

                    match( this.input,
                           RPAREN,
                           FOLLOW_RPAREN_in_statement1491 );
                    match( this.input,
                           LCURLY,
                           FOLLOW_LCURLY_in_statement1493 );
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:491:4: ( casesGroup )*
                    loop40 : do {
                        int alt40 = 2;
                        final int LA40_0 = this.input.LA( 1 );
                        if ( ((LA40_0 >= 106 && LA40_0 <= 107)) ) {
                            alt40 = 1;
                        }

                        switch ( alt40 ) {
                            case 1 :
                                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:491:6: casesGroup
                            {
                                pushFollow( FOLLOW_casesGroup_in_statement1500 );
                                casesGroup();
                                this._fsp--;

                            }
                                break;

                            default :
                                break loop40;
                        }
                    } while ( true );

                    match( this.input,
                           RCURLY,
                           FOLLOW_RCURLY_in_statement1507 );

                }
                    break;
                case 14 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:495:4: tryBlock
                {
                    pushFollow( FOLLOW_tryBlock_in_statement1515 );
                    tryBlock();
                    this._fsp--;

                }
                    break;
                case 15 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:498:4: 'throw' expression SEMI
                {
                    match( this.input,
                           105,
                           FOLLOW_105_in_statement1523 );
                    pushFollow( FOLLOW_expression_in_statement1525 );
                    expression();
                    this._fsp--;

                    match( this.input,
                           SEMI,
                           FOLLOW_SEMI_in_statement1527 );

                }
                    break;
                case 16 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:501:4: 'synchronized' LPAREN expression RPAREN compoundStatement
                {
                    match( this.input,
                           86,
                           FOLLOW_86_in_statement1535 );
                    match( this.input,
                           LPAREN,
                           FOLLOW_LPAREN_in_statement1537 );
                    pushFollow( FOLLOW_expression_in_statement1539 );
                    expression();
                    this._fsp--;

                    match( this.input,
                           RPAREN,
                           FOLLOW_RPAREN_in_statement1541 );
                    pushFollow( FOLLOW_compoundStatement_in_statement1543 );
                    compoundStatement();
                    this._fsp--;

                }
                    break;
                case 17 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:507:4: SEMI
                {
                    match( this.input,
                           SEMI,
                           FOLLOW_SEMI_in_statement1556 );

                }
                    break;

            }
        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end statement

    // $ANTLR start casesGroup
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:510:1: casesGroup : ( options {greedy=true; } : aCase )+ caseSList ;
    public void casesGroup() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:511:4: ( ( options {greedy=true; } : aCase )+ caseSList )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:511:4: ( options {greedy=true; } : aCase )+ caseSList
            {
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:511:4: ( options {greedy=true; } : aCase )+
                int cnt42 = 0;
                loop42 : do {
                    int alt42 = 2;
                    final int LA42_0 = this.input.LA( 1 );
                    if ( (LA42_0 == 106) ) {
                        alt42 = 1;
                    } else if ( (LA42_0 == 107) ) {
                        alt42 = 1;
                    }

                    switch ( alt42 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:517:4: aCase
                        {
                            pushFollow( FOLLOW_aCase_in_casesGroup1602 );
                            aCase();
                            this._fsp--;

                        }
                            break;

                        default :
                            if ( cnt42 >= 1 ) {
                                break loop42;
                            }
                            final EarlyExitException eee = new EarlyExitException( 42,
                                                                             this.input );
                            throw eee;
                    }
                    cnt42++;
                } while ( true );

                pushFollow( FOLLOW_caseSList_in_casesGroup1611 );
                caseSList();
                this._fsp--;

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end casesGroup

    // $ANTLR start aCase
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:523:1: aCase : ( 'case' expression | 'default' ) COLON ;
    public void aCase() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:524:4: ( ( 'case' expression | 'default' ) COLON )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:524:4: ( 'case' expression | 'default' ) COLON
            {
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:524:4: ( 'case' expression | 'default' )
                int alt43 = 2;
                final int LA43_0 = this.input.LA( 1 );
                if ( (LA43_0 == 106) ) {
                    alt43 = 1;
                } else if ( (LA43_0 == 107) ) {
                    alt43 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "524:4: ( 'case' expression | 'default' )",
                                                                          43,
                                                                          0,
                                                                          this.input );

                    throw nvae;
                }
                switch ( alt43 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:524:5: 'case' expression
                    {
                        match( this.input,
                               106,
                               FOLLOW_106_in_aCase1626 );
                        pushFollow( FOLLOW_expression_in_aCase1628 );
                        expression();
                        this._fsp--;

                    }
                        break;
                    case 2 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:524:25: 'default'
                    {
                        match( this.input,
                               107,
                               FOLLOW_107_in_aCase1632 );

                    }
                        break;

                }

                match( this.input,
                       COLON,
                       FOLLOW_COLON_in_aCase1635 );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end aCase

    // $ANTLR start caseSList
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:527:1: caseSList : ( statement )* ;
    public void caseSList() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:528:4: ( ( statement )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:528:4: ( statement )*
            {
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:528:4: ( statement )*
                loop44 : do {
                    int alt44 = 2;
                    final int LA44_0 = this.input.LA( 1 );
                    if ( (LA44_0 == IDENT || (LA44_0 >= LCURLY && LA44_0 <= SEMI) || LA44_0 == LPAREN || (LA44_0 >= PLUS && LA44_0 <= MINUS) || (LA44_0 >= INC && LA44_0 <= NUM_FLOAT) || (LA44_0 >= 68 && LA44_0 <= 89)
                          || (LA44_0 >= 93 && LA44_0 <= 94) || LA44_0 == 96 || (LA44_0 >= 98 && LA44_0 <= 105) || LA44_0 == 108 || (LA44_0 >= 112 && LA44_0 <= 115)) ) {
                        alt44 = 1;
                    }

                    switch ( alt44 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:528:5: statement
                        {
                            pushFollow( FOLLOW_statement_in_caseSList1647 );
                            statement();
                            this._fsp--;

                        }
                            break;

                        default :
                            break loop44;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end caseSList

    // $ANTLR start forInit
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:533:1: forInit : ( declaration | expressionList )? ;
    public void forInit() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:536:4: ( ( declaration | expressionList )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:536:4: ( declaration | expressionList )?
            {
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:536:4: ( declaration | expressionList )?
                int alt45 = 3;
                alt45 = this.dfa45.predict( this.input );
                switch ( alt45 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:536:6: declaration
                    {
                        pushFollow( FOLLOW_declaration_in_forInit1678 );
                        declaration();
                        this._fsp--;

                    }
                        break;
                    case 2 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:538:5: expressionList
                    {
                        pushFollow( FOLLOW_expressionList_in_forInit1687 );
                        expressionList();
                        this._fsp--;

                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end forInit

    // $ANTLR start forCond
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:543:1: forCond : ( expression )? ;
    public void forCond() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:544:4: ( ( expression )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:544:4: ( expression )?
            {
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:544:4: ( expression )?
                int alt46 = 2;
                final int LA46_0 = this.input.LA( 1 );
                if ( (LA46_0 == IDENT || LA46_0 == LPAREN || (LA46_0 >= PLUS && LA46_0 <= MINUS) || (LA46_0 >= INC && LA46_0 <= NUM_FLOAT) || (LA46_0 >= 68 && LA46_0 <= 76) || (LA46_0 >= 93 && LA46_0 <= 94) || (LA46_0 >= 112 && LA46_0 <= 115)) ) {
                    alt46 = 1;
                }
                switch ( alt46 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:544:5: expression
                    {
                        pushFollow( FOLLOW_expression_in_forCond1707 );
                        expression();
                        this._fsp--;

                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end forCond

    // $ANTLR start forIter
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:548:1: forIter : ( expressionList )? ;
    public void forIter() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:549:4: ( ( expressionList )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:549:4: ( expressionList )?
            {
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:549:4: ( expressionList )?
                int alt47 = 2;
                final int LA47_0 = this.input.LA( 1 );
                if ( (LA47_0 == IDENT || LA47_0 == LPAREN || (LA47_0 >= PLUS && LA47_0 <= MINUS) || (LA47_0 >= INC && LA47_0 <= NUM_FLOAT) || (LA47_0 >= 68 && LA47_0 <= 76) || (LA47_0 >= 93 && LA47_0 <= 94) || (LA47_0 >= 112 && LA47_0 <= 115)) ) {
                    alt47 = 1;
                }
                switch ( alt47 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:549:5: expressionList
                    {
                        pushFollow( FOLLOW_expressionList_in_forIter1724 );
                        expressionList();
                        this._fsp--;

                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end forIter

    // $ANTLR start tryBlock
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:554:1: tryBlock : 'try' compoundStatement ( handler )* ( finallyClause )? ;
    public void tryBlock() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:555:4: ( 'try' compoundStatement ( handler )* ( finallyClause )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:555:4: 'try' compoundStatement ( handler )* ( finallyClause )?
            {
                match( this.input,
                       108,
                       FOLLOW_108_in_tryBlock1741 );
                pushFollow( FOLLOW_compoundStatement_in_tryBlock1743 );
                compoundStatement();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:556:3: ( handler )*
                loop48 : do {
                    int alt48 = 2;
                    final int LA48_0 = this.input.LA( 1 );
                    if ( (LA48_0 == 110) ) {
                        alt48 = 1;
                    }

                    switch ( alt48 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:556:4: handler
                        {
                            pushFollow( FOLLOW_handler_in_tryBlock1748 );
                            handler();
                            this._fsp--;

                        }
                            break;

                        default :
                            break loop48;
                    }
                } while ( true );

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:557:3: ( finallyClause )?
                int alt49 = 2;
                final int LA49_0 = this.input.LA( 1 );
                if ( (LA49_0 == 109) ) {
                    alt49 = 1;
                }
                switch ( alt49 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:557:5: finallyClause
                    {
                        pushFollow( FOLLOW_finallyClause_in_tryBlock1756 );
                        finallyClause();
                        this._fsp--;

                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end tryBlock

    // $ANTLR start finallyClause
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:560:1: finallyClause : 'finally' compoundStatement ;
    public void finallyClause() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:561:4: ( 'finally' compoundStatement )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:561:4: 'finally' compoundStatement
            {
                match( this.input,
                       109,
                       FOLLOW_109_in_finallyClause1770 );
                pushFollow( FOLLOW_compoundStatement_in_finallyClause1772 );
                compoundStatement();
                this._fsp--;

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end finallyClause

    // $ANTLR start handler
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:565:1: handler : 'catch' LPAREN parameterDeclaration RPAREN compoundStatement ;
    public void handler() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:566:4: ( 'catch' LPAREN parameterDeclaration RPAREN compoundStatement )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:566:4: 'catch' LPAREN parameterDeclaration RPAREN compoundStatement
            {
                match( this.input,
                       110,
                       FOLLOW_110_in_handler1784 );
                match( this.input,
                       LPAREN,
                       FOLLOW_LPAREN_in_handler1786 );
                pushFollow( FOLLOW_parameterDeclaration_in_handler1788 );
                parameterDeclaration();
                this._fsp--;

                match( this.input,
                       RPAREN,
                       FOLLOW_RPAREN_in_handler1790 );
                pushFollow( FOLLOW_compoundStatement_in_handler1792 );
                compoundStatement();
                this._fsp--;

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end handler

    // $ANTLR start expression
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:605:1: expression : assignmentExpression ;
    public void expression() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:606:4: ( assignmentExpression )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:606:4: assignmentExpression
            {
                pushFollow( FOLLOW_assignmentExpression_in_expression1839 );
                assignmentExpression();
                this._fsp--;

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end expression

    // $ANTLR start expressionList
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:612:1: expressionList : expression ( COMMA expression )* ;
    public void expressionList() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:613:4: ( expression ( COMMA expression )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:613:4: expression ( COMMA expression )*
            {
                pushFollow( FOLLOW_expression_in_expressionList1855 );
                expression();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:613:15: ( COMMA expression )*
                loop50 : do {
                    int alt50 = 2;
                    final int LA50_0 = this.input.LA( 1 );
                    if ( (LA50_0 == COMMA) ) {
                        alt50 = 1;
                    }

                    switch ( alt50 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:613:16: COMMA expression
                        {
                            match( this.input,
                                   COMMA,
                                   FOLLOW_COMMA_in_expressionList1858 );
                            pushFollow( FOLLOW_expression_in_expressionList1860 );
                            expression();
                            this._fsp--;

                        }
                            break;

                        default :
                            break loop50;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end expressionList

    // $ANTLR start assignmentExpression
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:619:1: assignmentExpression : conditionalExpression ( (ASSIGN|PLUS_ASSIGN|MINUS_ASSIGN|STAR_ASSIGN|DIV_ASSIGN|MOD_ASSIGN|SR_ASSIGN|BSR_ASSIGN|SL_ASSIGN|BAND_ASSIGN|BXOR_ASSIGN|BOR_ASSIGN) assignmentExpression )? ;
    public void assignmentExpression() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:620:4: ( conditionalExpression ( (ASSIGN|PLUS_ASSIGN|MINUS_ASSIGN|STAR_ASSIGN|DIV_ASSIGN|MOD_ASSIGN|SR_ASSIGN|BSR_ASSIGN|SL_ASSIGN|BAND_ASSIGN|BXOR_ASSIGN|BOR_ASSIGN) assignmentExpression )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:620:4: conditionalExpression ( (ASSIGN|PLUS_ASSIGN|MINUS_ASSIGN|STAR_ASSIGN|DIV_ASSIGN|MOD_ASSIGN|SR_ASSIGN|BSR_ASSIGN|SL_ASSIGN|BAND_ASSIGN|BXOR_ASSIGN|BOR_ASSIGN) assignmentExpression )?
            {
                pushFollow( FOLLOW_conditionalExpression_in_assignmentExpression1878 );
                conditionalExpression();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:621:3: ( (ASSIGN|PLUS_ASSIGN|MINUS_ASSIGN|STAR_ASSIGN|DIV_ASSIGN|MOD_ASSIGN|SR_ASSIGN|BSR_ASSIGN|SL_ASSIGN|BAND_ASSIGN|BXOR_ASSIGN|BOR_ASSIGN) assignmentExpression )?
                int alt51 = 2;
                final int LA51_0 = this.input.LA( 1 );
                if ( (LA51_0 == ASSIGN || (LA51_0 >= PLUS_ASSIGN && LA51_0 <= BOR_ASSIGN)) ) {
                    alt51 = 1;
                }
                switch ( alt51 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:621:5: (ASSIGN|PLUS_ASSIGN|MINUS_ASSIGN|STAR_ASSIGN|DIV_ASSIGN|MOD_ASSIGN|SR_ASSIGN|BSR_ASSIGN|SL_ASSIGN|BAND_ASSIGN|BXOR_ASSIGN|BOR_ASSIGN) assignmentExpression
                    {
                        if ( this.input.LA( 1 ) == ASSIGN || (this.input.LA( 1 ) >= PLUS_ASSIGN && this.input.LA( 1 ) <= BOR_ASSIGN) ) {
                            this.input.consume();
                            this.errorRecovery = false;
                        } else {
                            final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                     this.input );
                            recoverFromMismatchedSet( this.input,
                                                      mse,
                                                      FOLLOW_set_in_assignmentExpression1886 );
                            throw mse;
                        }

                        pushFollow( FOLLOW_assignmentExpression_in_assignmentExpression2103 );
                        assignmentExpression();
                        this._fsp--;

                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end assignmentExpression

    // $ANTLR start conditionalExpression
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:640:1: conditionalExpression : logicalOrExpression ( QUESTION assignmentExpression COLON conditionalExpression )? ;
    public void conditionalExpression() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:641:4: ( logicalOrExpression ( QUESTION assignmentExpression COLON conditionalExpression )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:641:4: logicalOrExpression ( QUESTION assignmentExpression COLON conditionalExpression )?
            {
                pushFollow( FOLLOW_logicalOrExpression_in_conditionalExpression2121 );
                logicalOrExpression();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:642:3: ( QUESTION assignmentExpression COLON conditionalExpression )?
                int alt52 = 2;
                final int LA52_0 = this.input.LA( 1 );
                if ( (LA52_0 == QUESTION) ) {
                    alt52 = 1;
                }
                switch ( alt52 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:642:5: QUESTION assignmentExpression COLON conditionalExpression
                    {
                        match( this.input,
                               QUESTION,
                               FOLLOW_QUESTION_in_conditionalExpression2127 );
                        pushFollow( FOLLOW_assignmentExpression_in_conditionalExpression2129 );
                        assignmentExpression();
                        this._fsp--;

                        match( this.input,
                               COLON,
                               FOLLOW_COLON_in_conditionalExpression2131 );
                        pushFollow( FOLLOW_conditionalExpression_in_conditionalExpression2133 );
                        conditionalExpression();
                        this._fsp--;

                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end conditionalExpression

    // $ANTLR start logicalOrExpression
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:647:1: logicalOrExpression : logicalAndExpression ( LOR logicalAndExpression )* ;
    public void logicalOrExpression() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:648:4: ( logicalAndExpression ( LOR logicalAndExpression )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:648:4: logicalAndExpression ( LOR logicalAndExpression )*
            {
                pushFollow( FOLLOW_logicalAndExpression_in_logicalOrExpression2149 );
                logicalAndExpression();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:648:25: ( LOR logicalAndExpression )*
                loop53 : do {
                    int alt53 = 2;
                    final int LA53_0 = this.input.LA( 1 );
                    if ( (LA53_0 == LOR) ) {
                        alt53 = 1;
                    }

                    switch ( alt53 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:648:26: LOR logicalAndExpression
                        {
                            match( this.input,
                                   LOR,
                                   FOLLOW_LOR_in_logicalOrExpression2152 );
                            pushFollow( FOLLOW_logicalAndExpression_in_logicalOrExpression2154 );
                            logicalAndExpression();
                            this._fsp--;

                        }
                            break;

                        default :
                            break loop53;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end logicalOrExpression

    // $ANTLR start logicalAndExpression
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:653:1: logicalAndExpression : inclusiveOrExpression ( LAND inclusiveOrExpression )* ;
    public void logicalAndExpression() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:654:4: ( inclusiveOrExpression ( LAND inclusiveOrExpression )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:654:4: inclusiveOrExpression ( LAND inclusiveOrExpression )*
            {
                pushFollow( FOLLOW_inclusiveOrExpression_in_logicalAndExpression2169 );
                inclusiveOrExpression();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:654:26: ( LAND inclusiveOrExpression )*
                loop54 : do {
                    int alt54 = 2;
                    final int LA54_0 = this.input.LA( 1 );
                    if ( (LA54_0 == LAND) ) {
                        alt54 = 1;
                    }

                    switch ( alt54 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:654:27: LAND inclusiveOrExpression
                        {
                            match( this.input,
                                   LAND,
                                   FOLLOW_LAND_in_logicalAndExpression2172 );
                            pushFollow( FOLLOW_inclusiveOrExpression_in_logicalAndExpression2174 );
                            inclusiveOrExpression();
                            this._fsp--;

                        }
                            break;

                        default :
                            break loop54;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end logicalAndExpression

    // $ANTLR start inclusiveOrExpression
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:659:1: inclusiveOrExpression : exclusiveOrExpression ( BOR exclusiveOrExpression )* ;
    public void inclusiveOrExpression() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:660:4: ( exclusiveOrExpression ( BOR exclusiveOrExpression )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:660:4: exclusiveOrExpression ( BOR exclusiveOrExpression )*
            {
                pushFollow( FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression2189 );
                exclusiveOrExpression();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:660:26: ( BOR exclusiveOrExpression )*
                loop55 : do {
                    int alt55 = 2;
                    final int LA55_0 = this.input.LA( 1 );
                    if ( (LA55_0 == BOR) ) {
                        alt55 = 1;
                    }

                    switch ( alt55 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:660:27: BOR exclusiveOrExpression
                        {
                            match( this.input,
                                   BOR,
                                   FOLLOW_BOR_in_inclusiveOrExpression2192 );
                            pushFollow( FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression2194 );
                            exclusiveOrExpression();
                            this._fsp--;

                        }
                            break;

                        default :
                            break loop55;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end inclusiveOrExpression

    // $ANTLR start exclusiveOrExpression
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:665:1: exclusiveOrExpression : andExpression ( BXOR andExpression )* ;
    public void exclusiveOrExpression() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:666:4: ( andExpression ( BXOR andExpression )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:666:4: andExpression ( BXOR andExpression )*
            {
                pushFollow( FOLLOW_andExpression_in_exclusiveOrExpression2209 );
                andExpression();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:666:18: ( BXOR andExpression )*
                loop56 : do {
                    int alt56 = 2;
                    final int LA56_0 = this.input.LA( 1 );
                    if ( (LA56_0 == BXOR) ) {
                        alt56 = 1;
                    }

                    switch ( alt56 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:666:19: BXOR andExpression
                        {
                            match( this.input,
                                   BXOR,
                                   FOLLOW_BXOR_in_exclusiveOrExpression2212 );
                            pushFollow( FOLLOW_andExpression_in_exclusiveOrExpression2214 );
                            andExpression();
                            this._fsp--;

                        }
                            break;

                        default :
                            break loop56;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end exclusiveOrExpression

    // $ANTLR start andExpression
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:671:1: andExpression : equalityExpression ( BAND equalityExpression )* ;
    public void andExpression() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:672:4: ( equalityExpression ( BAND equalityExpression )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:672:4: equalityExpression ( BAND equalityExpression )*
            {
                pushFollow( FOLLOW_equalityExpression_in_andExpression2229 );
                equalityExpression();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:672:23: ( BAND equalityExpression )*
                loop57 : do {
                    int alt57 = 2;
                    final int LA57_0 = this.input.LA( 1 );
                    if ( (LA57_0 == BAND) ) {
                        alt57 = 1;
                    }

                    switch ( alt57 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:672:24: BAND equalityExpression
                        {
                            match( this.input,
                                   BAND,
                                   FOLLOW_BAND_in_andExpression2232 );
                            pushFollow( FOLLOW_equalityExpression_in_andExpression2234 );
                            equalityExpression();
                            this._fsp--;

                        }
                            break;

                        default :
                            break loop57;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end andExpression

    // $ANTLR start equalityExpression
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:677:1: equalityExpression : relationalExpression ( (NOT_EQUAL|EQUAL) relationalExpression )* ;
    public void equalityExpression() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:678:4: ( relationalExpression ( (NOT_EQUAL|EQUAL) relationalExpression )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:678:4: relationalExpression ( (NOT_EQUAL|EQUAL) relationalExpression )*
            {
                pushFollow( FOLLOW_relationalExpression_in_equalityExpression2249 );
                relationalExpression();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:678:25: ( (NOT_EQUAL|EQUAL) relationalExpression )*
                loop58 : do {
                    int alt58 = 2;
                    final int LA58_0 = this.input.LA( 1 );
                    if ( ((LA58_0 >= NOT_EQUAL && LA58_0 <= EQUAL)) ) {
                        alt58 = 1;
                    }

                    switch ( alt58 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:678:26: (NOT_EQUAL|EQUAL) relationalExpression
                        {
                            if ( (this.input.LA( 1 ) >= NOT_EQUAL && this.input.LA( 1 ) <= EQUAL) ) {
                                this.input.consume();
                                this.errorRecovery = false;
                            } else {
                                final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                         this.input );
                                recoverFromMismatchedSet( this.input,
                                                          mse,
                                                          FOLLOW_set_in_equalityExpression2253 );
                                throw mse;
                            }

                            pushFollow( FOLLOW_relationalExpression_in_equalityExpression2260 );
                            relationalExpression();
                            this._fsp--;

                        }
                            break;

                        default :
                            break loop58;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end equalityExpression

    // $ANTLR start relationalExpression
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:683:1: relationalExpression : shiftExpression ( ( (LT|GT|LE|GE) shiftExpression )* | 'instanceof' typeSpec ) ;
    public void relationalExpression() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:684:4: ( shiftExpression ( ( (LT|GT|LE|GE) shiftExpression )* | 'instanceof' typeSpec ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:684:4: shiftExpression ( ( (LT|GT|LE|GE) shiftExpression )* | 'instanceof' typeSpec )
            {
                pushFollow( FOLLOW_shiftExpression_in_relationalExpression2275 );
                shiftExpression();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:685:3: ( ( (LT|GT|LE|GE) shiftExpression )* | 'instanceof' typeSpec )
                int alt60 = 2;
                final int LA60_0 = this.input.LA( 1 );
                if ( (LA60_0 == RBRACK || (LA60_0 >= SEMI && LA60_0 <= COMMA) || (LA60_0 >= RPAREN && LA60_0 <= GE)) ) {
                    alt60 = 1;
                } else if ( (LA60_0 == 111) ) {
                    alt60 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "685:3: ( ( (LT|GT|LE|GE) shiftExpression )* | 'instanceof' typeSpec )",
                                                                          60,
                                                                          0,
                                                                          this.input );

                    throw nvae;
                }
                switch ( alt60 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:685:5: ( (LT|GT|LE|GE) shiftExpression )*
                    {
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:685:5: ( (LT|GT|LE|GE) shiftExpression )*
                        loop59 : do {
                            int alt59 = 2;
                            final int LA59_0 = this.input.LA( 1 );
                            if ( ((LA59_0 >= LT && LA59_0 <= GE)) ) {
                                alt59 = 1;
                            }

                            switch ( alt59 ) {
                                case 1 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:685:7: (LT|GT|LE|GE) shiftExpression
                                {
                                    if ( (this.input.LA( 1 ) >= LT && this.input.LA( 1 ) <= GE) ) {
                                        this.input.consume();
                                        this.errorRecovery = false;
                                    } else {
                                        final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                                 this.input );
                                        recoverFromMismatchedSet( this.input,
                                                                  mse,
                                                                  FOLLOW_set_in_relationalExpression2285 );
                                        throw mse;
                                    }

                                    pushFollow( FOLLOW_shiftExpression_in_relationalExpression2321 );
                                    shiftExpression();
                                    this._fsp--;

                                }
                                    break;

                                default :
                                    break loop59;
                            }
                        } while ( true );

                    }
                        break;
                    case 2 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:692:5: 'instanceof' typeSpec
                    {
                        match( this.input,
                               111,
                               FOLLOW_111_in_relationalExpression2333 );
                        pushFollow( FOLLOW_typeSpec_in_relationalExpression2335 );
                        typeSpec();
                        this._fsp--;

                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end relationalExpression

    // $ANTLR start shiftExpression
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:698:1: shiftExpression : additiveExpression ( (SL|SR|BSR) additiveExpression )* ;
    public void shiftExpression() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:699:4: ( additiveExpression ( (SL|SR|BSR) additiveExpression )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:699:4: additiveExpression ( (SL|SR|BSR) additiveExpression )*
            {
                pushFollow( FOLLOW_additiveExpression_in_shiftExpression2352 );
                additiveExpression();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:699:23: ( (SL|SR|BSR) additiveExpression )*
                loop61 : do {
                    int alt61 = 2;
                    final int LA61_0 = this.input.LA( 1 );
                    if ( ((LA61_0 >= SL && LA61_0 <= BSR)) ) {
                        alt61 = 1;
                    }

                    switch ( alt61 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:699:24: (SL|SR|BSR) additiveExpression
                        {
                            if ( (this.input.LA( 1 ) >= SL && this.input.LA( 1 ) <= BSR) ) {
                                this.input.consume();
                                this.errorRecovery = false;
                            } else {
                                final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                         this.input );
                                recoverFromMismatchedSet( this.input,
                                                          mse,
                                                          FOLLOW_set_in_shiftExpression2356 );
                                throw mse;
                            }

                            pushFollow( FOLLOW_additiveExpression_in_shiftExpression2367 );
                            additiveExpression();
                            this._fsp--;

                        }
                            break;

                        default :
                            break loop61;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end shiftExpression

    // $ANTLR start additiveExpression
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:704:1: additiveExpression : multiplicativeExpression ( (PLUS|MINUS) multiplicativeExpression )* ;
    public void additiveExpression() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:705:4: ( multiplicativeExpression ( (PLUS|MINUS) multiplicativeExpression )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:705:4: multiplicativeExpression ( (PLUS|MINUS) multiplicativeExpression )*
            {
                pushFollow( FOLLOW_multiplicativeExpression_in_additiveExpression2382 );
                multiplicativeExpression();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:705:29: ( (PLUS|MINUS) multiplicativeExpression )*
                loop62 : do {
                    int alt62 = 2;
                    final int LA62_0 = this.input.LA( 1 );
                    if ( ((LA62_0 >= PLUS && LA62_0 <= MINUS)) ) {
                        alt62 = 1;
                    }

                    switch ( alt62 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:705:30: (PLUS|MINUS) multiplicativeExpression
                        {
                            if ( (this.input.LA( 1 ) >= PLUS && this.input.LA( 1 ) <= MINUS) ) {
                                this.input.consume();
                                this.errorRecovery = false;
                            } else {
                                final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                         this.input );
                                recoverFromMismatchedSet( this.input,
                                                          mse,
                                                          FOLLOW_set_in_additiveExpression2386 );
                                throw mse;
                            }

                            pushFollow( FOLLOW_multiplicativeExpression_in_additiveExpression2393 );
                            multiplicativeExpression();
                            this._fsp--;

                        }
                            break;

                        default :
                            break loop62;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end additiveExpression

    // $ANTLR start multiplicativeExpression
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:710:1: multiplicativeExpression : unaryExpression ( (STAR|DIV|MOD) unaryExpression )* ;
    public void multiplicativeExpression() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:711:4: ( unaryExpression ( (STAR|DIV|MOD) unaryExpression )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:711:4: unaryExpression ( (STAR|DIV|MOD) unaryExpression )*
            {
                pushFollow( FOLLOW_unaryExpression_in_multiplicativeExpression2408 );
                unaryExpression();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:711:20: ( (STAR|DIV|MOD) unaryExpression )*
                loop63 : do {
                    int alt63 = 2;
                    final int LA63_0 = this.input.LA( 1 );
                    if ( (LA63_0 == STAR || (LA63_0 >= DIV && LA63_0 <= MOD)) ) {
                        alt63 = 1;
                    }

                    switch ( alt63 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:711:21: (STAR|DIV|MOD) unaryExpression
                        {
                            if ( this.input.LA( 1 ) == STAR || (this.input.LA( 1 ) >= DIV && this.input.LA( 1 ) <= MOD) ) {
                                this.input.consume();
                                this.errorRecovery = false;
                            } else {
                                final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                         this.input );
                                recoverFromMismatchedSet( this.input,
                                                          mse,
                                                          FOLLOW_set_in_multiplicativeExpression2412 );
                                throw mse;
                            }

                            pushFollow( FOLLOW_unaryExpression_in_multiplicativeExpression2424 );
                            unaryExpression();
                            this._fsp--;

                        }
                            break;

                        default :
                            break loop63;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end multiplicativeExpression

    // $ANTLR start unaryExpression
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:714:1: unaryExpression : ( INC unaryExpression | DEC unaryExpression | MINUS unaryExpression | PLUS unaryExpression | unaryExpressionNotPlusMinus );
    public void unaryExpression() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:715:4: ( INC unaryExpression | DEC unaryExpression | MINUS unaryExpression | PLUS unaryExpression | unaryExpressionNotPlusMinus )
            int alt64 = 5;
            switch ( this.input.LA( 1 ) ) {
                case INC :
                    alt64 = 1;
                    break;
                case DEC :
                    alt64 = 2;
                    break;
                case MINUS :
                    alt64 = 3;
                    break;
                case PLUS :
                    alt64 = 4;
                    break;
                case IDENT :
                case LPAREN :
                case BNOT :
                case LNOT :
                case NUM_INT :
                case CHAR_LITERAL :
                case STRING_LITERAL :
                case NUM_FLOAT :
                case 68 :
                case 69 :
                case 70 :
                case 71 :
                case 72 :
                case 73 :
                case 74 :
                case 75 :
                case 76 :
                case 93 :
                case 94 :
                case 112 :
                case 113 :
                case 114 :
                case 115 :
                    alt64 = 5;
                    break;
                default :
                    final NoViableAltException nvae = new NoViableAltException( "714:1: unaryExpression : ( INC unaryExpression | DEC unaryExpression | MINUS unaryExpression | PLUS unaryExpression | unaryExpressionNotPlusMinus );",
                                                                          64,
                                                                          0,
                                                                          this.input );

                    throw nvae;
            }

            switch ( alt64 ) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:715:4: INC unaryExpression
                {
                    match( this.input,
                           INC,
                           FOLLOW_INC_in_unaryExpression2437 );
                    pushFollow( FOLLOW_unaryExpression_in_unaryExpression2439 );
                    unaryExpression();
                    this._fsp--;

                }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:716:4: DEC unaryExpression
                {
                    match( this.input,
                           DEC,
                           FOLLOW_DEC_in_unaryExpression2444 );
                    pushFollow( FOLLOW_unaryExpression_in_unaryExpression2446 );
                    unaryExpression();
                    this._fsp--;

                }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:717:4: MINUS unaryExpression
                {
                    match( this.input,
                           MINUS,
                           FOLLOW_MINUS_in_unaryExpression2451 );
                    pushFollow( FOLLOW_unaryExpression_in_unaryExpression2454 );
                    unaryExpression();
                    this._fsp--;

                }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:718:4: PLUS unaryExpression
                {
                    match( this.input,
                           PLUS,
                           FOLLOW_PLUS_in_unaryExpression2459 );
                    pushFollow( FOLLOW_unaryExpression_in_unaryExpression2463 );
                    unaryExpression();
                    this._fsp--;

                }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:719:4: unaryExpressionNotPlusMinus
                {
                    pushFollow( FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression2468 );
                    unaryExpressionNotPlusMinus();
                    this._fsp--;

                }
                    break;

            }
        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end unaryExpression

    // $ANTLR start unaryExpressionNotPlusMinus
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:722:1: unaryExpressionNotPlusMinus : ( BNOT unaryExpression | LNOT unaryExpression | LPAREN builtInTypeSpec RPAREN unaryExpression | LPAREN classTypeSpec RPAREN unaryExpressionNotPlusMinus | postfixExpression );
    public void unaryExpressionNotPlusMinus() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:723:4: ( BNOT unaryExpression | LNOT unaryExpression | LPAREN builtInTypeSpec RPAREN unaryExpression | LPAREN classTypeSpec RPAREN unaryExpressionNotPlusMinus | postfixExpression )
            int alt65 = 5;
            alt65 = this.dfa65.predict( this.input );
            switch ( alt65 ) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:723:4: BNOT unaryExpression
                {
                    match( this.input,
                           BNOT,
                           FOLLOW_BNOT_in_unaryExpressionNotPlusMinus2479 );
                    pushFollow( FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2481 );
                    unaryExpression();
                    this._fsp--;

                }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:724:4: LNOT unaryExpression
                {
                    match( this.input,
                           LNOT,
                           FOLLOW_LNOT_in_unaryExpressionNotPlusMinus2486 );
                    pushFollow( FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2488 );
                    unaryExpression();
                    this._fsp--;

                }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:726:9: LPAREN builtInTypeSpec RPAREN unaryExpression
                {
                    match( this.input,
                           LPAREN,
                           FOLLOW_LPAREN_in_unaryExpressionNotPlusMinus2499 );
                    pushFollow( FOLLOW_builtInTypeSpec_in_unaryExpressionNotPlusMinus2501 );
                    builtInTypeSpec();
                    this._fsp--;

                    match( this.input,
                           RPAREN,
                           FOLLOW_RPAREN_in_unaryExpressionNotPlusMinus2503 );
                    pushFollow( FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2513 );
                    unaryExpression();
                    this._fsp--;

                }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:732:7: LPAREN classTypeSpec RPAREN unaryExpressionNotPlusMinus
                {
                    match( this.input,
                           LPAREN,
                           FOLLOW_LPAREN_in_unaryExpressionNotPlusMinus2549 );
                    pushFollow( FOLLOW_classTypeSpec_in_unaryExpressionNotPlusMinus2551 );
                    classTypeSpec();
                    this._fsp--;

                    match( this.input,
                           RPAREN,
                           FOLLOW_RPAREN_in_unaryExpressionNotPlusMinus2553 );
                    pushFollow( FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpressionNotPlusMinus2563 );
                    unaryExpressionNotPlusMinus();
                    this._fsp--;

                }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:735:7: postfixExpression
                {
                    pushFollow( FOLLOW_postfixExpression_in_unaryExpressionNotPlusMinus2572 );
                    postfixExpression();
                    this._fsp--;

                }
                    break;

            }
        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end unaryExpressionNotPlusMinus

    // $ANTLR start postfixExpression
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:739:1: postfixExpression : primaryExpression ( DOT IDENT ( LPAREN argList RPAREN )? | DOT 'this' | DOT 'super' ( LPAREN argList RPAREN | DOT IDENT ( LPAREN argList RPAREN )? ) | DOT newExpression | LBRACK expression RBRACK )* ( (INC|DEC))? ;
    public void postfixExpression() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:740:6: ( primaryExpression ( DOT IDENT ( LPAREN argList RPAREN )? | DOT 'this' | DOT 'super' ( LPAREN argList RPAREN | DOT IDENT ( LPAREN argList RPAREN )? ) | DOT newExpression | LBRACK expression RBRACK )* ( (INC|DEC))? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:740:6: primaryExpression ( DOT IDENT ( LPAREN argList RPAREN )? | DOT 'this' | DOT 'super' ( LPAREN argList RPAREN | DOT IDENT ( LPAREN argList RPAREN )? ) | DOT newExpression | LBRACK expression RBRACK )* ( (INC|DEC))?
            {
                pushFollow( FOLLOW_primaryExpression_in_postfixExpression2586 );
                primaryExpression();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:741:3: ( DOT IDENT ( LPAREN argList RPAREN )? | DOT 'this' | DOT 'super' ( LPAREN argList RPAREN | DOT IDENT ( LPAREN argList RPAREN )? ) | DOT newExpression | LBRACK expression RBRACK )*
                loop69 : do {
                    int alt69 = 6;
                    final int LA69_0 = this.input.LA( 1 );
                    if ( (LA69_0 == DOT) ) {
                        switch ( this.input.LA( 2 ) ) {
                            case IDENT :
                                alt69 = 1;
                                break;
                            case 94 :
                                alt69 = 3;
                                break;
                            case 93 :
                                alt69 = 2;
                                break;
                            case 115 :
                                alt69 = 4;
                                break;

                        }

                    } else if ( (LA69_0 == LBRACK) ) {
                        alt69 = 5;
                    }

                    switch ( alt69 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:741:7: DOT IDENT ( LPAREN argList RPAREN )?
                        {
                            match( this.input,
                                   DOT,
                                   FOLLOW_DOT_in_postfixExpression2594 );
                            match( this.input,
                                   IDENT,
                                   FOLLOW_IDENT_in_postfixExpression2596 );
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:742:4: ( LPAREN argList RPAREN )?
                            int alt66 = 2;
                            final int LA66_0 = this.input.LA( 1 );
                            if ( (LA66_0 == LPAREN) ) {
                                alt66 = 1;
                            }
                            switch ( alt66 ) {
                                case 1 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:742:6: LPAREN argList RPAREN
                                {
                                    match( this.input,
                                           LPAREN,
                                           FOLLOW_LPAREN_in_postfixExpression2603 );
                                    pushFollow( FOLLOW_argList_in_postfixExpression2610 );
                                    argList();
                                    this._fsp--;

                                    match( this.input,
                                           RPAREN,
                                           FOLLOW_RPAREN_in_postfixExpression2616 );

                                }
                                    break;

                            }

                        }
                            break;
                        case 2 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:746:5: DOT 'this'
                        {
                            match( this.input,
                                   DOT,
                                   FOLLOW_DOT_in_postfixExpression2628 );
                            match( this.input,
                                   93,
                                   FOLLOW_93_in_postfixExpression2630 );

                        }
                            break;
                        case 3 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:748:5: DOT 'super' ( LPAREN argList RPAREN | DOT IDENT ( LPAREN argList RPAREN )? )
                        {
                            match( this.input,
                                   DOT,
                                   FOLLOW_DOT_in_postfixExpression2637 );
                            match( this.input,
                                   94,
                                   FOLLOW_94_in_postfixExpression2639 );
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:749:13: ( LPAREN argList RPAREN | DOT IDENT ( LPAREN argList RPAREN )? )
                            int alt68 = 2;
                            final int LA68_0 = this.input.LA( 1 );
                            if ( (LA68_0 == LPAREN) ) {
                                alt68 = 1;
                            } else if ( (LA68_0 == DOT) ) {
                                alt68 = 2;
                            } else {
                                final NoViableAltException nvae = new NoViableAltException( "749:13: ( LPAREN argList RPAREN | DOT IDENT ( LPAREN argList RPAREN )? )",
                                                                                      68,
                                                                                      0,
                                                                                      this.input );

                                throw nvae;
                            }
                            switch ( alt68 ) {
                                case 1 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:750:17: LPAREN argList RPAREN
                                {
                                    match( this.input,
                                           LPAREN,
                                           FOLLOW_LPAREN_in_postfixExpression2674 );
                                    pushFollow( FOLLOW_argList_in_postfixExpression2676 );
                                    argList();
                                    this._fsp--;

                                    match( this.input,
                                           RPAREN,
                                           FOLLOW_RPAREN_in_postfixExpression2678 );

                                }
                                    break;
                                case 2 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:752:8: DOT IDENT ( LPAREN argList RPAREN )?
                                {
                                    match( this.input,
                                           DOT,
                                           FOLLOW_DOT_in_postfixExpression2704 );
                                    match( this.input,
                                           IDENT,
                                           FOLLOW_IDENT_in_postfixExpression2706 );
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:753:17: ( LPAREN argList RPAREN )?
                                    int alt67 = 2;
                                    final int LA67_0 = this.input.LA( 1 );
                                    if ( (LA67_0 == LPAREN) ) {
                                        alt67 = 1;
                                    }
                                    switch ( alt67 ) {
                                        case 1 :
                                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:753:19: LPAREN argList RPAREN
                                        {
                                            match( this.input,
                                                   LPAREN,
                                                   FOLLOW_LPAREN_in_postfixExpression2726 );
                                            pushFollow( FOLLOW_argList_in_postfixExpression2749 );
                                            argList();
                                            this._fsp--;

                                            match( this.input,
                                                   RPAREN,
                                                   FOLLOW_RPAREN_in_postfixExpression2771 );

                                        }
                                            break;

                                    }

                                }
                                    break;

                            }

                        }
                            break;
                        case 4 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:758:5: DOT newExpression
                        {
                            match( this.input,
                                   DOT,
                                   FOLLOW_DOT_in_postfixExpression2810 );
                            pushFollow( FOLLOW_newExpression_in_postfixExpression2812 );
                            newExpression();
                            this._fsp--;

                        }
                            break;
                        case 5 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:759:5: LBRACK expression RBRACK
                        {
                            match( this.input,
                                   LBRACK,
                                   FOLLOW_LBRACK_in_postfixExpression2818 );
                            pushFollow( FOLLOW_expression_in_postfixExpression2821 );
                            expression();
                            this._fsp--;

                            match( this.input,
                                   RBRACK,
                                   FOLLOW_RBRACK_in_postfixExpression2823 );

                        }
                            break;

                        default :
                            break loop69;
                    }
                } while ( true );

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:762:3: ( (INC|DEC))?
                int alt70 = 2;
                final int LA70_0 = this.input.LA( 1 );
                if ( ((LA70_0 >= INC && LA70_0 <= DEC)) ) {
                    alt70 = 1;
                }
                switch ( alt70 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:764:4: (INC|DEC)
                    {
                        if ( (this.input.LA( 1 ) >= INC && this.input.LA( 1 ) <= DEC) ) {
                            this.input.consume();
                            this.errorRecovery = false;
                        } else {
                            final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                     this.input );
                            recoverFromMismatchedSet( this.input,
                                                      mse,
                                                      FOLLOW_set_in_postfixExpression2854 );
                            throw mse;
                        }

                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end postfixExpression

    // $ANTLR start primaryExpression
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:770:1: primaryExpression : ( identPrimary ( options {greedy=true; } : DOT 'class' )? | constant | 'true' | 'false' | 'null' | newExpression | 'this' | 'super' | LPAREN assignmentExpression RPAREN | builtInType ( LBRACK RBRACK )* DOT 'class' );
    public void primaryExpression() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:771:4: ( identPrimary ( options {greedy=true; } : DOT 'class' )? | constant | 'true' | 'false' | 'null' | newExpression | 'this' | 'super' | LPAREN assignmentExpression RPAREN | builtInType ( LBRACK RBRACK )* DOT 'class' )
            int alt73 = 10;
            switch ( this.input.LA( 1 ) ) {
                case IDENT :
                    alt73 = 1;
                    break;
                case NUM_INT :
                case CHAR_LITERAL :
                case STRING_LITERAL :
                case NUM_FLOAT :
                    alt73 = 2;
                    break;
                case 112 :
                    alt73 = 3;
                    break;
                case 113 :
                    alt73 = 4;
                    break;
                case 114 :
                    alt73 = 5;
                    break;
                case 115 :
                    alt73 = 6;
                    break;
                case 93 :
                    alt73 = 7;
                    break;
                case 94 :
                    alt73 = 8;
                    break;
                case LPAREN :
                    alt73 = 9;
                    break;
                case 68 :
                case 69 :
                case 70 :
                case 71 :
                case 72 :
                case 73 :
                case 74 :
                case 75 :
                case 76 :
                    alt73 = 10;
                    break;
                default :
                    final NoViableAltException nvae = new NoViableAltException( "770:1: primaryExpression : ( identPrimary ( options {greedy=true; } : DOT 'class' )? | constant | 'true' | 'false' | 'null' | newExpression | 'this' | 'super' | LPAREN assignmentExpression RPAREN | builtInType ( LBRACK RBRACK )* DOT 'class' );",
                                                                          73,
                                                                          0,
                                                                          this.input );

                    throw nvae;
            }

            switch ( alt73 ) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:771:4: identPrimary ( options {greedy=true; } : DOT 'class' )?
                {
                    pushFollow( FOLLOW_identPrimary_in_primaryExpression2881 );
                    identPrimary();
                    this._fsp--;

                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:771:17: ( options {greedy=true; } : DOT 'class' )?
                    int alt71 = 2;
                    final int LA71_0 = this.input.LA( 1 );
                    if ( (LA71_0 == DOT) ) {
                        final int LA71_1 = this.input.LA( 2 );
                        if ( (LA71_1 == 89) ) {
                            alt71 = 1;
                        }
                    }
                    switch ( alt71 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:771:43: DOT 'class'
                        {
                            match( this.input,
                                   DOT,
                                   FOLLOW_DOT_in_primaryExpression2893 );
                            match( this.input,
                                   89,
                                   FOLLOW_89_in_primaryExpression2895 );

                        }
                            break;

                    }

                }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:772:9: constant
                {
                    pushFollow( FOLLOW_constant_in_primaryExpression2908 );
                    constant();
                    this._fsp--;

                }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:773:4: 'true'
                {
                    match( this.input,
                           112,
                           FOLLOW_112_in_primaryExpression2913 );

                }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:774:4: 'false'
                {
                    match( this.input,
                           113,
                           FOLLOW_113_in_primaryExpression2918 );

                }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:775:4: 'null'
                {
                    match( this.input,
                           114,
                           FOLLOW_114_in_primaryExpression2923 );

                }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:776:9: newExpression
                {
                    pushFollow( FOLLOW_newExpression_in_primaryExpression2933 );
                    newExpression();
                    this._fsp--;

                }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:777:4: 'this'
                {
                    match( this.input,
                           93,
                           FOLLOW_93_in_primaryExpression2938 );

                }
                    break;
                case 8 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:778:4: 'super'
                {
                    match( this.input,
                           94,
                           FOLLOW_94_in_primaryExpression2943 );

                }
                    break;
                case 9 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:779:4: LPAREN assignmentExpression RPAREN
                {
                    match( this.input,
                           LPAREN,
                           FOLLOW_LPAREN_in_primaryExpression2948 );
                    pushFollow( FOLLOW_assignmentExpression_in_primaryExpression2950 );
                    assignmentExpression();
                    this._fsp--;

                    match( this.input,
                           RPAREN,
                           FOLLOW_RPAREN_in_primaryExpression2952 );

                }
                    break;
                case 10 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:781:4: builtInType ( LBRACK RBRACK )* DOT 'class'
                {
                    pushFollow( FOLLOW_builtInType_in_primaryExpression2960 );
                    builtInType();
                    this._fsp--;

                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:782:3: ( LBRACK RBRACK )*
                    loop72 : do {
                        int alt72 = 2;
                        final int LA72_0 = this.input.LA( 1 );
                        if ( (LA72_0 == LBRACK) ) {
                            alt72 = 1;
                        }

                        switch ( alt72 ) {
                            case 1 :
                                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:782:5: LBRACK RBRACK
                            {
                                match( this.input,
                                       LBRACK,
                                       FOLLOW_LBRACK_in_primaryExpression2966 );
                                match( this.input,
                                       RBRACK,
                                       FOLLOW_RBRACK_in_primaryExpression2969 );

                            }
                                break;

                            default :
                                break loop72;
                        }
                    } while ( true );

                    match( this.input,
                           DOT,
                           FOLLOW_DOT_in_primaryExpression2976 );
                    match( this.input,
                           89,
                           FOLLOW_89_in_primaryExpression2978 );

                }
                    break;

            }
        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end primaryExpression

    // $ANTLR start identPrimary
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:786:1: identPrimary : i= IDENT ( options {greedy=true; k=2; } : DOT IDENT )* ( options {greedy=true; } : ( LPAREN argList RPAREN ) | ( options {greedy=true; } : LBRACK RBRACK )+ )? ;
    public void identPrimary() throws RecognitionException {
        Token i = null;

        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:791:4: (i= IDENT ( options {greedy=true; k=2; } : DOT IDENT )* ( options {greedy=true; } : ( LPAREN argList RPAREN ) | ( options {greedy=true; } : LBRACK RBRACK )+ )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:791:4: i= IDENT ( options {greedy=true; k=2; } : DOT IDENT )* ( options {greedy=true; } : ( LPAREN argList RPAREN ) | ( options {greedy=true; } : LBRACK RBRACK )+ )?
            {
                i = (Token) this.input.LT( 1 );
                match( this.input,
                       IDENT,
                       FOLLOW_IDENT_in_identPrimary2993 );
                this.identifiers.add( i.getText() );
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:792:3: ( options {greedy=true; k=2; } : DOT IDENT )*
                loop74 : do {
                    int alt74 = 2;
                    final int LA74_0 = this.input.LA( 1 );
                    if ( (LA74_0 == DOT) ) {
                        final int LA74_3 = this.input.LA( 2 );
                        if ( (LA74_3 == IDENT) ) {
                            alt74 = 1;
                        }

                    }

                    switch ( alt74 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:796:5: DOT IDENT
                        {
                            match( this.input,
                                   DOT,
                                   FOLLOW_DOT_in_identPrimary3031 );
                            match( this.input,
                                   IDENT,
                                   FOLLOW_IDENT_in_identPrimary3033 );

                        }
                            break;

                        default :
                            break loop74;
                    }
                } while ( true );

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:798:3: ( options {greedy=true; } : ( LPAREN argList RPAREN ) | ( options {greedy=true; } : LBRACK RBRACK )+ )?
                int alt76 = 3;
                final int LA76_0 = this.input.LA( 1 );
                if ( (LA76_0 == LPAREN) ) {
                    alt76 = 1;
                } else if ( (LA76_0 == LBRACK) ) {
                    final int LA76_2 = this.input.LA( 2 );
                    if ( (LA76_2 == RBRACK) ) {
                        alt76 = 2;
                    }
                }
                switch ( alt76 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:804:7: ( LPAREN argList RPAREN )
                    {
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:804:7: ( LPAREN argList RPAREN )
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:804:9: LPAREN argList RPAREN
                        {
                            match( this.input,
                                   LPAREN,
                                   FOLLOW_LPAREN_in_identPrimary3095 );
                            pushFollow( FOLLOW_argList_in_identPrimary3098 );
                            argList();
                            this._fsp--;

                            match( this.input,
                                   RPAREN,
                                   FOLLOW_RPAREN_in_identPrimary3100 );

                        }

                    }
                        break;
                    case 2 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:805:5: ( options {greedy=true; } : LBRACK RBRACK )+
                    {
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:805:5: ( options {greedy=true; } : LBRACK RBRACK )+
                        int cnt75 = 0;
                        loop75 : do {
                            int alt75 = 2;
                            final int LA75_0 = this.input.LA( 1 );
                            if ( (LA75_0 == LBRACK) ) {
                                final int LA75_2 = this.input.LA( 2 );
                                if ( (LA75_2 == RBRACK) ) {
                                    alt75 = 1;
                                }

                            }

                            switch ( alt75 ) {
                                case 1 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:806:15: LBRACK RBRACK
                                {
                                    match( this.input,
                                           LBRACK,
                                           FOLLOW_LBRACK_in_identPrimary3133 );
                                    match( this.input,
                                           RBRACK,
                                           FOLLOW_RBRACK_in_identPrimary3136 );

                                }
                                    break;

                                default :
                                    if ( cnt75 >= 1 ) {
                                        break loop75;
                                    }
                                    final EarlyExitException eee = new EarlyExitException( 75,
                                                                                     this.input );
                                    throw eee;
                            }
                            cnt75++;
                        } while ( true );

                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end identPrimary

    // $ANTLR start newExpression
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:811:1: newExpression : 'new' type ( LPAREN argList RPAREN ( classBlock )? | newArrayDeclarator ( arrayInitializer )? ) ;
    public void newExpression() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:861:4: ( 'new' type ( LPAREN argList RPAREN ( classBlock )? | newArrayDeclarator ( arrayInitializer )? ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:861:4: 'new' type ( LPAREN argList RPAREN ( classBlock )? | newArrayDeclarator ( arrayInitializer )? )
            {
                match( this.input,
                       115,
                       FOLLOW_115_in_newExpression3172 );
                pushFollow( FOLLOW_type_in_newExpression3174 );
                type();
                this._fsp--;

                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:862:3: ( LPAREN argList RPAREN ( classBlock )? | newArrayDeclarator ( arrayInitializer )? )
                int alt79 = 2;
                final int LA79_0 = this.input.LA( 1 );
                if ( (LA79_0 == LPAREN) ) {
                    alt79 = 1;
                } else if ( (LA79_0 == LBRACK) ) {
                    alt79 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "862:3: ( LPAREN argList RPAREN ( classBlock )? | newArrayDeclarator ( arrayInitializer )? )",
                                                                          79,
                                                                          0,
                                                                          this.input );

                    throw nvae;
                }
                switch ( alt79 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:862:5: LPAREN argList RPAREN ( classBlock )?
                    {
                        match( this.input,
                               LPAREN,
                               FOLLOW_LPAREN_in_newExpression3180 );
                        pushFollow( FOLLOW_argList_in_newExpression3182 );
                        argList();
                        this._fsp--;

                        match( this.input,
                               RPAREN,
                               FOLLOW_RPAREN_in_newExpression3184 );
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:862:27: ( classBlock )?
                        int alt77 = 2;
                        final int LA77_0 = this.input.LA( 1 );
                        if ( (LA77_0 == LCURLY) ) {
                            alt77 = 1;
                        }
                        switch ( alt77 ) {
                            case 1 :
                                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:862:28: classBlock
                            {
                                pushFollow( FOLLOW_classBlock_in_newExpression3187 );
                                classBlock();
                                this._fsp--;

                            }
                                break;

                        }

                    }
                        break;
                    case 2 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:872:5: newArrayDeclarator ( arrayInitializer )?
                    {
                        pushFollow( FOLLOW_newArrayDeclarator_in_newExpression3225 );
                        newArrayDeclarator();
                        this._fsp--;

                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:872:24: ( arrayInitializer )?
                        int alt78 = 2;
                        final int LA78_0 = this.input.LA( 1 );
                        if ( (LA78_0 == LCURLY) ) {
                            alt78 = 1;
                        }
                        switch ( alt78 ) {
                            case 1 :
                                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:872:25: arrayInitializer
                            {
                                pushFollow( FOLLOW_arrayInitializer_in_newExpression3228 );
                                arrayInitializer();
                                this._fsp--;

                            }
                                break;

                        }

                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end newExpression

    // $ANTLR start argList
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:876:1: argList : ( expressionList | ) ;
    public void argList() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:877:4: ( ( expressionList | ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:877:4: ( expressionList | )
            {
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:877:4: ( expressionList | )
                int alt80 = 2;
                final int LA80_0 = this.input.LA( 1 );
                if ( (LA80_0 == IDENT || LA80_0 == LPAREN || (LA80_0 >= PLUS && LA80_0 <= MINUS) || (LA80_0 >= INC && LA80_0 <= NUM_FLOAT) || (LA80_0 >= 68 && LA80_0 <= 76) || (LA80_0 >= 93 && LA80_0 <= 94) || (LA80_0 >= 112 && LA80_0 <= 115)) ) {
                    alt80 = 1;
                } else if ( (LA80_0 == RPAREN) ) {
                    alt80 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "877:4: ( expressionList | )",
                                                                          80,
                                                                          0,
                                                                          this.input );

                    throw nvae;
                }
                switch ( alt80 ) {
                    case 1 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:877:6: expressionList
                    {
                        pushFollow( FOLLOW_expressionList_in_argList3247 );
                        expressionList();
                        this._fsp--;

                    }
                        break;
                    case 2 :
                        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:880:3: 
                    {
                    }
                        break;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end argList

    // $ANTLR start newArrayDeclarator
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:883:1: newArrayDeclarator : ( options {k=1; } : LBRACK ( expression )? RBRACK )+ ;
    public void newArrayDeclarator() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:884:4: ( ( options {k=1; } : LBRACK ( expression )? RBRACK )+ )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:884:4: ( options {k=1; } : LBRACK ( expression )? RBRACK )+
            {
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:884:4: ( options {k=1; } : LBRACK ( expression )? RBRACK )+
                int cnt82 = 0;
                loop82 : do {
                    int alt82 = 2;
                    final int LA82_0 = this.input.LA( 1 );
                    if ( (LA82_0 == LBRACK) ) {
                        alt82 = 1;
                    }

                    switch ( alt82 ) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:893:4: LBRACK ( expression )? RBRACK
                        {
                            match( this.input,
                                   LBRACK,
                                   FOLLOW_LBRACK_in_newArrayDeclarator3317 );
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:894:5: ( expression )?
                            int alt81 = 2;
                            final int LA81_0 = this.input.LA( 1 );
                            if ( (LA81_0 == IDENT || LA81_0 == LPAREN || (LA81_0 >= PLUS && LA81_0 <= MINUS) || (LA81_0 >= INC && LA81_0 <= NUM_FLOAT) || (LA81_0 >= 68 && LA81_0 <= 76) || (LA81_0 >= 93 && LA81_0 <= 94) || (LA81_0 >= 112 && LA81_0 <= 115)) ) {
                                alt81 = 1;
                            }
                            switch ( alt81 ) {
                                case 1 :
                                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:894:6: expression
                                {
                                    pushFollow( FOLLOW_expression_in_newArrayDeclarator3325 );
                                    expression();
                                    this._fsp--;

                                }
                                    break;

                            }

                            match( this.input,
                                   RBRACK,
                                   FOLLOW_RBRACK_in_newArrayDeclarator3332 );

                        }
                            break;

                        default :
                            if ( cnt82 >= 1 ) {
                                break loop82;
                            }
                            final EarlyExitException eee = new EarlyExitException( 82,
                                                                             this.input );
                            throw eee;
                    }
                    cnt82++;
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end newArrayDeclarator

    // $ANTLR start constant
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:899:1: constant : (NUM_INT|CHAR_LITERAL|STRING_LITERAL|NUM_FLOAT);
    public void constant() throws RecognitionException {
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:900:2: ( (NUM_INT|CHAR_LITERAL|STRING_LITERAL|NUM_FLOAT))
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\semantics\\java\\parser\\JavaParser.g:900:4: (NUM_INT|CHAR_LITERAL|STRING_LITERAL|NUM_FLOAT)
            {
                if ( (this.input.LA( 1 ) >= NUM_INT && this.input.LA( 1 ) <= NUM_FLOAT) ) {
                    this.input.consume();
                    this.errorRecovery = false;
                } else {
                    final MismatchedSetException mse = new MismatchedSetException( null,
                                                                             this.input );
                    recoverFromMismatchedSet( this.input,
                                              mse,
                                              FOLLOW_set_in_constant3348 );
                    throw mse;
                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end constant

    protected DFA41              dfa41            = new DFA41( this );
    protected DFA45              dfa45            = new DFA45( this );
    protected DFA65              dfa65            = new DFA65( this );
    public static final String   DFA41_eotS       = "\34\uffff";
    public static final String   DFA41_eofS       = "\34\uffff";
    public static final String   DFA41_minS       = "\1\6\1\uffff\1\6\2\4\14\uffff\1\6\4\uffff\1\6\2\5\3\4";
    public static final String   DFA41_maxS       = "\1\163\1\uffff\1\131\1\157\1\7\14\uffff\1\131\4\uffff\2\163\1\5" + "\2\157\1\7";
    public static final String   DFA41_acceptS    = "\1\uffff\1\1\3\uffff\1\3\1\4\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1" + "\15\1\16\1\17\1\uffff\1\21\1\20\1\2\1\5\6\uffff";
    public static final String   DFA41_specialS   = "\34\uffff}>";
    public static final String[] DFA41_transition = {
            "\1\3\2\uffff\1\1\1\22\2\uffff\1\5\35\uffff\2\5\2\uffff\10\5\15\uffff" + "\11\4\11\21\1\2\2\21\1\6\3\uffff\2\5\1\uffff\1\7\1\uffff\1\10\1" + "\11\1\12\1\13\1\14\1\15\1\16\1\20\2\uffff\1\17\3\uffff\4\5", "",
            "\1\24\6\uffff\1\23\66\uffff\11\24\14\21\1\6", "\1\27\1\uffff\1\24\1\26\1\5\1\uffff\1\5\2\uffff\1\5\1\uffff\1\5" + "\1\25\40\5\76\uffff\1\5", "\1\30\1\uffff\1\24\1\5", "", "", "", "", "", "", "", "", "", "", "", "",
            "\1\24\75\uffff\11\24\14\21\1\6", "", "", "", "", "\1\31\122\uffff\1\5\3\uffff\2\5\24\uffff\1\5", "\1\32\1\5\6\uffff\1\5\35\uffff\2\5\2\uffff\10\5\15\uffff\11\5\20" + "\uffff\2\5\21\uffff\4\5", "\1\33",
            "\1\27\1\uffff\1\24\1\26\1\5\1\uffff\1\5\2\uffff\1\5\1\uffff\1\5" + "\1\uffff\40\5\76\uffff\1\5", "\1\27\1\uffff\1\24\2\5\1\uffff\1\5\4\uffff\1\5\1\uffff\40\5\76\uffff" + "\1\5", "\1\30\1\uffff\1\24\1\5"};

    class DFA41 extends DFA {
        public DFA41(final BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 41;
            this.eot = DFA.unpackEncodedString( DFA41_eotS );
            this.eof = DFA.unpackEncodedString( DFA41_eofS );
            this.min = DFA.unpackEncodedStringToUnsignedChars( DFA41_minS );
            this.max = DFA.unpackEncodedStringToUnsignedChars( DFA41_maxS );
            this.accept = DFA.unpackEncodedString( DFA41_acceptS );
            this.special = DFA.unpackEncodedString( DFA41_specialS );
            final int numStates = DFA41_transition.length;
            this.transition = new short[numStates][];
            for ( int i = 0; i < numStates; i++ ) {
                this.transition[i] = DFA.unpackEncodedString( DFA41_transition[i] );
            }
        }

        public String getDescription() {
            return "434:1: statement : ( compoundStatement | declaration SEMI | expression SEMI | modifiers classDefinition | IDENT COLON statement | 'if' LPAREN expression RPAREN statement ( 'else' statement )? | 'for' LPAREN forInit SEMI forCond SEMI forIter RPAREN statement | 'while' LPAREN expression RPAREN statement | 'do' statement 'while' LPAREN expression RPAREN SEMI | 'break' ( IDENT )? SEMI | 'continue' ( IDENT )? SEMI | 'return' ( expression )? SEMI | 'switch' LPAREN expression RPAREN LCURLY ( casesGroup )* RCURLY | tryBlock | 'throw' expression SEMI | 'synchronized' LPAREN expression RPAREN compoundStatement | SEMI );";
        }
    }

    public static final String   DFA45_eotS       = "\14\uffff";
    public static final String   DFA45_eofS       = "\14\uffff";
    public static final String   DFA45_minS       = "\1\6\1\uffff\2\4\2\uffff\1\6\2\5\3\4";
    public static final String   DFA45_maxS       = "\1\163\1\uffff\1\157\1\7\2\uffff\2\163\1\5\2\157\1\7";
    public static final String   DFA45_acceptS    = "\1\uffff\1\1\2\uffff\1\2\1\3\6\uffff";
    public static final String   DFA45_specialS   = "\14\uffff}>";
    public static final String[] DFA45_transition = {"\1\2\3\uffff\1\5\2\uffff\1\4\35\uffff\2\4\2\uffff\10\4\15\uffff" + "\11\3\14\1\4\uffff\2\4\21\uffff\4\4", "",
            "\1\7\1\uffff\1\1\1\6\1\4\1\uffff\1\4\1\uffff\2\4\1\uffff\1\4\1\uffff" + "\40\4\76\uffff\1\4", "\1\10\1\uffff\1\1\1\4", "", "", "\1\11\122\uffff\1\4\3\uffff\2\4\24\uffff\1\4",
            "\1\12\1\4\6\uffff\1\4\35\uffff\2\4\2\uffff\10\4\15\uffff\11\4\20" + "\uffff\2\4\21\uffff\4\4", "\1\13", "\1\7\1\uffff\1\1\1\6\1\4\1\uffff\1\4\1\uffff\2\4\1\uffff\1\4\1\uffff" + "\40\4\76\uffff\1\4",
            "\1\7\1\uffff\1\1\2\4\1\uffff\1\4\1\uffff\1\4\2\uffff\1\4\1\uffff" + "\40\4\76\uffff\1\4", "\1\10\1\uffff\1\1\1\4"};

    class DFA45 extends DFA {
        public DFA45(final BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 45;
            this.eot = DFA.unpackEncodedString( DFA45_eotS );
            this.eof = DFA.unpackEncodedString( DFA45_eofS );
            this.min = DFA.unpackEncodedStringToUnsignedChars( DFA45_minS );
            this.max = DFA.unpackEncodedStringToUnsignedChars( DFA45_maxS );
            this.accept = DFA.unpackEncodedString( DFA45_acceptS );
            this.special = DFA.unpackEncodedString( DFA45_specialS );
            final int numStates = DFA45_transition.length;
            this.transition = new short[numStates][];
            for ( int i = 0; i < numStates; i++ ) {
                this.transition[i] = DFA.unpackEncodedString( DFA45_transition[i] );
            }
        }

        public String getDescription() {
            return "536:4: ( declaration | expressionList )?";
        }
    }

    public static final String   DFA65_eotS       = "\20\uffff";
    public static final String   DFA65_eofS       = "\20\uffff";
    public static final String   DFA65_minS       = "\1\6\2\uffff\1\6\1\uffff\2\4\1\6\1\5\1\4\1\5\1\uffff\2\4\1\uffff" + "\1\4";
    public static final String   DFA65_maxS       = "\1\163\2\uffff\1\163\1\uffff\1\157\1\16\3\163\1\5\1\uffff\2\157" + "\1\uffff\1\16";
    public static final String   DFA65_acceptS    = "\1\uffff\1\1\1\2\1\uffff\1\5\6\uffff\1\3\2\uffff\1\4\1\uffff";
    public static final String   DFA65_specialS   = "\20\uffff}>";
    public static final String[] DFA65_transition = {"\1\4\6\uffff\1\3\43\uffff\1\1\1\2\4\4\15\uffff\11\4\20\uffff\2\4" + "\21\uffff\4\4", "", "", "\1\5\6\uffff\1\4\35\uffff\2\4\2\uffff\10\4\15\uffff\11\6\20\uffff" + "\2\4\21\uffff\4\4", "",
            "\1\10\2\uffff\1\7\1\4\4\uffff\1\4\1\11\1\4\1\uffff\40\4\76\uffff" + "\1\4", "\1\12\2\uffff\1\4\6\uffff\1\13", "\1\14\122\uffff\1\4\3\uffff\2\4\24\uffff\1\4",
            "\1\15\1\4\6\uffff\1\4\35\uffff\2\4\2\uffff\10\4\15\uffff\11\4\20" + "\uffff\2\4\21\uffff\4\4", "\2\4\1\16\2\4\1\uffff\3\4\1\16\43\4\6\16\15\uffff\11\16\20\uffff" + "\2\16\20\uffff\1\4\4\16", "\1\17", "",
            "\1\10\2\uffff\1\7\1\4\4\uffff\1\4\1\11\1\4\1\uffff\40\4\76\uffff" + "\1\4", "\1\10\2\uffff\2\4\5\uffff\1\11\1\4\1\uffff\40\4\76\uffff\1\4", "", "\1\12\2\uffff\1\4\6\uffff\1\13"};

    class DFA65 extends DFA {
        public DFA65(final BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 65;
            this.eot = DFA.unpackEncodedString( DFA65_eotS );
            this.eof = DFA.unpackEncodedString( DFA65_eofS );
            this.min = DFA.unpackEncodedStringToUnsignedChars( DFA65_minS );
            this.max = DFA.unpackEncodedStringToUnsignedChars( DFA65_maxS );
            this.accept = DFA.unpackEncodedString( DFA65_acceptS );
            this.special = DFA.unpackEncodedString( DFA65_specialS );
            final int numStates = DFA65_transition.length;
            this.transition = new short[numStates][];
            for ( int i = 0; i < numStates; i++ ) {
                this.transition[i] = DFA.unpackEncodedString( DFA65_transition[i] );
            }
        }

        public String getDescription() {
            return "722:1: unaryExpressionNotPlusMinus : ( BNOT unaryExpression | LNOT unaryExpression | LPAREN builtInTypeSpec RPAREN unaryExpression | LPAREN classTypeSpec RPAREN unaryExpressionNotPlusMinus | postfixExpression );";
        }
    }

    public static final BitSet FOLLOW_modifiers_in_declaration59                                     = new BitSet( new long[]{0x0000000000000040L, 0x0000000000001FF0L} );
    public static final BitSet FOLLOW_typeSpec_in_declaration61                                      = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_variableDefinitions_in_declaration63                           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_classTypeSpec_in_typeSpec79                                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_builtInTypeSpec_in_typeSpec84                                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_identifier_in_classTypeSpec97                                  = new BitSet( new long[]{0x0000000000000012L} );
    public static final BitSet FOLLOW_LBRACK_in_classTypeSpec100                                     = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_RBRACK_in_classTypeSpec103                                     = new BitSet( new long[]{0x0000000000000012L} );
    public static final BitSet FOLLOW_builtInType_in_builtInTypeSpec118                              = new BitSet( new long[]{0x0000000000000012L} );
    public static final BitSet FOLLOW_LBRACK_in_builtInTypeSpec121                                   = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_RBRACK_in_builtInTypeSpec124                                   = new BitSet( new long[]{0x0000000000000012L} );
    public static final BitSet FOLLOW_identifier_in_type139                                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_builtInType_in_type144                                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_set_in_builtInType156                                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_IDENT_in_identifier209                                         = new BitSet( new long[]{0x0000000000000082L} );
    public static final BitSet FOLLOW_DOT_in_identifier214                                           = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_IDENT_in_identifier216                                         = new BitSet( new long[]{0x0000000000000082L} );
    public static final BitSet FOLLOW_IDENT_in_identifierStar230                                     = new BitSet( new long[]{0x0000000000000082L} );
    public static final BitSet FOLLOW_DOT_in_identifierStar236                                       = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_IDENT_in_identifierStar238                                     = new BitSet( new long[]{0x0000000000000082L} );
    public static final BitSet FOLLOW_DOT_in_identifierStar247                                       = new BitSet( new long[]{0x0000000000000100L} );
    public static final BitSet FOLLOW_STAR_in_identifierStar249                                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_modifier_in_modifiers270                                       = new BitSet( new long[]{0x0000000000000002L, 0x0000000001FFE000L} );
    public static final BitSet FOLLOW_set_in_modifier288                                             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_89_in_classDefinition356                                       = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_IDENT_in_classDefinition358                                    = new BitSet( new long[]{0x0000000000000200L, 0x0000000014000000L} );
    public static final BitSet FOLLOW_superClassClause_in_classDefinition365                         = new BitSet( new long[]{0x0000000000000200L, 0x0000000010000000L} );
    public static final BitSet FOLLOW_implementsClause_in_classDefinition372                         = new BitSet( new long[]{0x0000000000000200L} );
    public static final BitSet FOLLOW_classBlock_in_classDefinition379                               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_90_in_superClassClause392                                      = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_identifier_in_superClassClause394                              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_91_in_interfaceDefinition412                                   = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_IDENT_in_interfaceDefinition414                                = new BitSet( new long[]{0x0000000000000200L, 0x0000000004000000L} );
    public static final BitSet FOLLOW_interfaceExtends_in_interfaceDefinition421                     = new BitSet( new long[]{0x0000000000000200L} );
    public static final BitSet FOLLOW_classBlock_in_interfaceDefinition428                           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LCURLY_in_classBlock442                                        = new BitSet( new long[]{0x0000000000000E40L, 0x000000000BFFFFF0L} );
    public static final BitSet FOLLOW_field_in_classBlock449                                         = new BitSet( new long[]{0x0000000000000E40L, 0x000000000BFFFFF0L} );
    public static final BitSet FOLLOW_SEMI_in_classBlock453                                          = new BitSet( new long[]{0x0000000000000E40L, 0x000000000BFFFFF0L} );
    public static final BitSet FOLLOW_RCURLY_in_classBlock460                                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_90_in_interfaceExtends479                                      = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_identifier_in_interfaceExtends483                              = new BitSet( new long[]{0x0000000000001002L} );
    public static final BitSet FOLLOW_COMMA_in_interfaceExtends487                                   = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_identifier_in_interfaceExtends489                              = new BitSet( new long[]{0x0000000000001002L} );
    public static final BitSet FOLLOW_92_in_implementsClause514                                      = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_identifier_in_implementsClause516                              = new BitSet( new long[]{0x0000000000001002L} );
    public static final BitSet FOLLOW_COMMA_in_implementsClause520                                   = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_identifier_in_implementsClause522                              = new BitSet( new long[]{0x0000000000001002L} );
    public static final BitSet FOLLOW_modifiers_in_field548                                          = new BitSet( new long[]{0x0000000000000040L, 0x000000000A001FF0L} );
    public static final BitSet FOLLOW_ctorHead_in_field554                                           = new BitSet( new long[]{0x0000000000000200L} );
    public static final BitSet FOLLOW_constructorBody_in_field556                                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_classDefinition_in_field568                                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_interfaceDefinition_in_field586                                = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_typeSpec_in_field600                                           = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_IDENT_in_field609                                              = new BitSet( new long[]{0x0000000000002000L} );
    public static final BitSet FOLLOW_LPAREN_in_field623                                             = new BitSet( new long[]{0x0000000000004040L, 0x0000000000041FF0L} );
    public static final BitSet FOLLOW_parameterDeclarationList_in_field625                           = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_field627                                             = new BitSet( new long[]{0x0000000000000610L, 0x0000000080000000L} );
    public static final BitSet FOLLOW_declaratorBrackets_in_field634                                 = new BitSet( new long[]{0x0000000000000600L, 0x0000000080000000L} );
    public static final BitSet FOLLOW_throwsClause_in_field652                                       = new BitSet( new long[]{0x0000000000000600L} );
    public static final BitSet FOLLOW_compoundStatement_in_field663                                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_SEMI_in_field667                                               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_variableDefinitions_in_field676                                = new BitSet( new long[]{0x0000000000000400L} );
    public static final BitSet FOLLOW_SEMI_in_field678                                               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_80_in_field704                                                 = new BitSet( new long[]{0x0000000000000200L} );
    public static final BitSet FOLLOW_compoundStatement_in_field706                                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_compoundStatement_in_field720                                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LCURLY_in_constructorBody739                                   = new BitSet( new long[]{0x007F980000002E40L, 0x000F13FD61FFFFF0L} );
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_constructorBody765            = new BitSet( new long[]{0x007F980000002E40L, 0x000F13FD61FFFFF0L} );
    public static final BitSet FOLLOW_statement_in_constructorBody782                                = new BitSet( new long[]{0x007F980000002E40L, 0x000F13FD61FFFFF0L} );
    public static final BitSet FOLLOW_RCURLY_in_constructorBody794                                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_93_in_explicitConstructorInvocation815                         = new BitSet( new long[]{0x0000000000002000L} );
    public static final BitSet FOLLOW_LPAREN_in_explicitConstructorInvocation817                     = new BitSet( new long[]{0x007F980000006040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_argList_in_explicitConstructorInvocation819                    = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_explicitConstructorInvocation821                     = new BitSet( new long[]{0x0000000000000400L} );
    public static final BitSet FOLLOW_SEMI_in_explicitConstructorInvocation823                       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_94_in_explicitConstructorInvocation836                         = new BitSet( new long[]{0x0000000000002000L} );
    public static final BitSet FOLLOW_LPAREN_in_explicitConstructorInvocation838                     = new BitSet( new long[]{0x007F980000006040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_argList_in_explicitConstructorInvocation840                    = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_explicitConstructorInvocation842                     = new BitSet( new long[]{0x0000000000000400L} );
    public static final BitSet FOLLOW_SEMI_in_explicitConstructorInvocation844                       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_variableDeclarator_in_variableDefinitions861                   = new BitSet( new long[]{0x0000000000001002L} );
    public static final BitSet FOLLOW_COMMA_in_variableDefinitions867                                = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_variableDeclarator_in_variableDefinitions872                   = new BitSet( new long[]{0x0000000000001002L} );
    public static final BitSet FOLLOW_IDENT_in_variableDeclarator890                                 = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_declaratorBrackets_in_variableDeclarator892                    = new BitSet( new long[]{0x0000000000008002L} );
    public static final BitSet FOLLOW_varInitializer_in_variableDeclarator894                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LBRACK_in_declaratorBrackets912                                = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_RBRACK_in_declaratorBrackets915                                = new BitSet( new long[]{0x0000000000000012L} );
    public static final BitSet FOLLOW_ASSIGN_in_varInitializer930                                    = new BitSet( new long[]{0x007F980000002240L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_initializer_in_varInitializer932                               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LCURLY_in_arrayInitializer947                                  = new BitSet( new long[]{0x007F980000002A40L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_initializer_in_arrayInitializer955                             = new BitSet( new long[]{0x0000000000001800L} );
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer992                                   = new BitSet( new long[]{0x007F980000002240L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_initializer_in_arrayInitializer994                             = new BitSet( new long[]{0x0000000000001800L} );
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer1008                                  = new BitSet( new long[]{0x0000000000000800L} );
    public static final BitSet FOLLOW_RCURLY_in_arrayInitializer1020                                 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_expression_in_initializer1034                                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_arrayInitializer_in_initializer1039                            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_IDENT_in_ctorHead1053                                          = new BitSet( new long[]{0x0000000000002000L} );
    public static final BitSet FOLLOW_LPAREN_in_ctorHead1063                                         = new BitSet( new long[]{0x0000000000004040L, 0x0000000000041FF0L} );
    public static final BitSet FOLLOW_parameterDeclarationList_in_ctorHead1065                       = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_ctorHead1067                                         = new BitSet( new long[]{0x0000000000000002L, 0x0000000080000000L} );
    public static final BitSet FOLLOW_throwsClause_in_ctorHead1076                                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_95_in_throwsClause1090                                         = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_identifier_in_throwsClause1092                                 = new BitSet( new long[]{0x0000000000001002L} );
    public static final BitSet FOLLOW_COMMA_in_throwsClause1096                                      = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_identifier_in_throwsClause1098                                 = new BitSet( new long[]{0x0000000000001002L} );
    public static final BitSet FOLLOW_parameterDeclaration_in_parameterDeclarationList1116           = new BitSet( new long[]{0x0000000000001002L} );
    public static final BitSet FOLLOW_COMMA_in_parameterDeclarationList1120                          = new BitSet( new long[]{0x0000000000000040L, 0x0000000000041FF0L} );
    public static final BitSet FOLLOW_parameterDeclaration_in_parameterDeclarationList1122           = new BitSet( new long[]{0x0000000000001002L} );
    public static final BitSet FOLLOW_parameterModifier_in_parameterDeclaration1140                  = new BitSet( new long[]{0x0000000000000040L, 0x0000000000001FF0L} );
    public static final BitSet FOLLOW_typeSpec_in_parameterDeclaration1142                           = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_IDENT_in_parameterDeclaration1144                              = new BitSet( new long[]{0x0000000000000012L} );
    public static final BitSet FOLLOW_declaratorBrackets_in_parameterDeclaration1148                 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_82_in_parameterModifier1160                                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LCURLY_in_compoundStatement1185                                = new BitSet( new long[]{0x007F980000002E40L, 0x000F13FD61FFFFF0L} );
    public static final BitSet FOLLOW_statement_in_compoundStatement1196                             = new BitSet( new long[]{0x007F980000002E40L, 0x000F13FD61FFFFF0L} );
    public static final BitSet FOLLOW_RCURLY_in_compoundStatement1202                                = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_compoundStatement_in_statement1216                             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_declaration_in_statement1232                                   = new BitSet( new long[]{0x0000000000000400L} );
    public static final BitSet FOLLOW_SEMI_in_statement1234                                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_expression_in_statement1246                                    = new BitSet( new long[]{0x0000000000000400L} );
    public static final BitSet FOLLOW_SEMI_in_statement1248                                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_modifiers_in_statement1256                                     = new BitSet( new long[]{0x0000000000000000L, 0x0000000002000000L} );
    public static final BitSet FOLLOW_classDefinition_in_statement1258                               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_IDENT_in_statement1266                                         = new BitSet( new long[]{0x0000000000010000L} );
    public static final BitSet FOLLOW_COLON_in_statement1268                                         = new BitSet( new long[]{0x007F980000002640L, 0x000F13FD61FFFFF0L} );
    public static final BitSet FOLLOW_statement_in_statement1271                                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_96_in_statement1279                                            = new BitSet( new long[]{0x0000000000002000L} );
    public static final BitSet FOLLOW_LPAREN_in_statement1281                                        = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_expression_in_statement1283                                    = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_statement1285                                        = new BitSet( new long[]{0x007F980000002640L, 0x000F13FD61FFFFF0L} );
    public static final BitSet FOLLOW_statement_in_statement1287                                     = new BitSet( new long[]{0x0000000000000002L, 0x0000000200000000L} );
    public static final BitSet FOLLOW_97_in_statement1308                                            = new BitSet( new long[]{0x007F980000002640L, 0x000F13FD61FFFFF0L} );
    public static final BitSet FOLLOW_statement_in_statement1310                                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_98_in_statement1323                                            = new BitSet( new long[]{0x0000000000002000L} );
    public static final BitSet FOLLOW_LPAREN_in_statement1328                                        = new BitSet( new long[]{0x007F980000002440L, 0x000F000061FFFFF0L} );
    public static final BitSet FOLLOW_forInit_in_statement1334                                       = new BitSet( new long[]{0x0000000000000400L} );
    public static final BitSet FOLLOW_SEMI_in_statement1336                                          = new BitSet( new long[]{0x007F980000002440L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_forCond_in_statement1345                                       = new BitSet( new long[]{0x0000000000000400L} );
    public static final BitSet FOLLOW_SEMI_in_statement1347                                          = new BitSet( new long[]{0x007F980000006040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_forIter_in_statement1356                                       = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_statement1370                                        = new BitSet( new long[]{0x007F980000002640L, 0x000F13FD61FFFFF0L} );
    public static final BitSet FOLLOW_statement_in_statement1375                                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_99_in_statement1404                                            = new BitSet( new long[]{0x0000000000002000L} );
    public static final BitSet FOLLOW_LPAREN_in_statement1406                                        = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_expression_in_statement1408                                    = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_statement1410                                        = new BitSet( new long[]{0x007F980000002640L, 0x000F13FD61FFFFF0L} );
    public static final BitSet FOLLOW_statement_in_statement1412                                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_100_in_statement1420                                           = new BitSet( new long[]{0x007F980000002640L, 0x000F13FD61FFFFF0L} );
    public static final BitSet FOLLOW_statement_in_statement1422                                     = new BitSet( new long[]{0x0000000000000000L, 0x0000000800000000L} );
    public static final BitSet FOLLOW_99_in_statement1424                                            = new BitSet( new long[]{0x0000000000002000L} );
    public static final BitSet FOLLOW_LPAREN_in_statement1426                                        = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_expression_in_statement1428                                    = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_statement1430                                        = new BitSet( new long[]{0x0000000000000400L} );
    public static final BitSet FOLLOW_SEMI_in_statement1432                                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_101_in_statement1440                                           = new BitSet( new long[]{0x0000000000000440L} );
    public static final BitSet FOLLOW_IDENT_in_statement1443                                         = new BitSet( new long[]{0x0000000000000400L} );
    public static final BitSet FOLLOW_SEMI_in_statement1447                                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_102_in_statement1455                                           = new BitSet( new long[]{0x0000000000000440L} );
    public static final BitSet FOLLOW_IDENT_in_statement1458                                         = new BitSet( new long[]{0x0000000000000400L} );
    public static final BitSet FOLLOW_SEMI_in_statement1462                                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_103_in_statement1470                                           = new BitSet( new long[]{0x007F980000002440L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_expression_in_statement1473                                    = new BitSet( new long[]{0x0000000000000400L} );
    public static final BitSet FOLLOW_SEMI_in_statement1477                                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_104_in_statement1485                                           = new BitSet( new long[]{0x0000000000002000L} );
    public static final BitSet FOLLOW_LPAREN_in_statement1487                                        = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_expression_in_statement1489                                    = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_statement1491                                        = new BitSet( new long[]{0x0000000000000200L} );
    public static final BitSet FOLLOW_LCURLY_in_statement1493                                        = new BitSet( new long[]{0x0000000000000800L, 0x00000C0000000000L} );
    public static final BitSet FOLLOW_casesGroup_in_statement1500                                    = new BitSet( new long[]{0x0000000000000800L, 0x00000C0000000000L} );
    public static final BitSet FOLLOW_RCURLY_in_statement1507                                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_tryBlock_in_statement1515                                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_105_in_statement1523                                           = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_expression_in_statement1525                                    = new BitSet( new long[]{0x0000000000000400L} );
    public static final BitSet FOLLOW_SEMI_in_statement1527                                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_86_in_statement1535                                            = new BitSet( new long[]{0x0000000000002000L} );
    public static final BitSet FOLLOW_LPAREN_in_statement1537                                        = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_expression_in_statement1539                                    = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_statement1541                                        = new BitSet( new long[]{0x0000000000000200L} );
    public static final BitSet FOLLOW_compoundStatement_in_statement1543                             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_SEMI_in_statement1556                                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_aCase_in_casesGroup1602                                        = new BitSet( new long[]{0x007F980000002642L, 0x000F1FFD61FFFFF0L} );
    public static final BitSet FOLLOW_caseSList_in_casesGroup1611                                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_106_in_aCase1626                                               = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_expression_in_aCase1628                                        = new BitSet( new long[]{0x0000000000010000L} );
    public static final BitSet FOLLOW_107_in_aCase1632                                               = new BitSet( new long[]{0x0000000000010000L} );
    public static final BitSet FOLLOW_COLON_in_aCase1635                                             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_statement_in_caseSList1647                                     = new BitSet( new long[]{0x007F980000002642L, 0x000F13FD61FFFFF0L} );
    public static final BitSet FOLLOW_declaration_in_forInit1678                                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_expressionList_in_forInit1687                                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_expression_in_forCond1707                                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_expressionList_in_forIter1724                                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_108_in_tryBlock1741                                            = new BitSet( new long[]{0x0000000000000200L} );
    public static final BitSet FOLLOW_compoundStatement_in_tryBlock1743                              = new BitSet( new long[]{0x0000000000000002L, 0x0000600000000000L} );
    public static final BitSet FOLLOW_handler_in_tryBlock1748                                        = new BitSet( new long[]{0x0000000000000002L, 0x0000600000000000L} );
    public static final BitSet FOLLOW_finallyClause_in_tryBlock1756                                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_109_in_finallyClause1770                                       = new BitSet( new long[]{0x0000000000000200L} );
    public static final BitSet FOLLOW_compoundStatement_in_finallyClause1772                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_110_in_handler1784                                             = new BitSet( new long[]{0x0000000000002000L} );
    public static final BitSet FOLLOW_LPAREN_in_handler1786                                          = new BitSet( new long[]{0x0000000000000040L, 0x0000000000041FF0L} );
    public static final BitSet FOLLOW_parameterDeclaration_in_handler1788                            = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_handler1790                                          = new BitSet( new long[]{0x0000000000000200L} );
    public static final BitSet FOLLOW_compoundStatement_in_handler1792                               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_assignmentExpression_in_expression1839                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_expression_in_expressionList1855                               = new BitSet( new long[]{0x0000000000001002L} );
    public static final BitSet FOLLOW_COMMA_in_expressionList1858                                    = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_expression_in_expressionList1860                               = new BitSet( new long[]{0x0000000000001002L} );
    public static final BitSet FOLLOW_conditionalExpression_in_assignmentExpression1878              = new BitSet( new long[]{0x000000000FFE8002L} );
    public static final BitSet FOLLOW_set_in_assignmentExpression1886                                = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_assignmentExpression_in_assignmentExpression2103               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_logicalOrExpression_in_conditionalExpression2121               = new BitSet( new long[]{0x0000000010000002L} );
    public static final BitSet FOLLOW_QUESTION_in_conditionalExpression2127                          = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_assignmentExpression_in_conditionalExpression2129              = new BitSet( new long[]{0x0000000000010000L} );
    public static final BitSet FOLLOW_COLON_in_conditionalExpression2131                             = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalExpression2133             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_logicalAndExpression_in_logicalOrExpression2149                = new BitSet( new long[]{0x0000000020000002L} );
    public static final BitSet FOLLOW_LOR_in_logicalOrExpression2152                                 = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_logicalAndExpression_in_logicalOrExpression2154                = new BitSet( new long[]{0x0000000020000002L} );
    public static final BitSet FOLLOW_inclusiveOrExpression_in_logicalAndExpression2169              = new BitSet( new long[]{0x0000000040000002L} );
    public static final BitSet FOLLOW_LAND_in_logicalAndExpression2172                               = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_inclusiveOrExpression_in_logicalAndExpression2174              = new BitSet( new long[]{0x0000000040000002L} );
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression2189             = new BitSet( new long[]{0x0000000080000002L} );
    public static final BitSet FOLLOW_BOR_in_inclusiveOrExpression2192                               = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression2194             = new BitSet( new long[]{0x0000000080000002L} );
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression2209                     = new BitSet( new long[]{0x0000000100000002L} );
    public static final BitSet FOLLOW_BXOR_in_exclusiveOrExpression2212                              = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression2214                     = new BitSet( new long[]{0x0000000100000002L} );
    public static final BitSet FOLLOW_equalityExpression_in_andExpression2229                        = new BitSet( new long[]{0x0000000200000002L} );
    public static final BitSet FOLLOW_BAND_in_andExpression2232                                      = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_equalityExpression_in_andExpression2234                        = new BitSet( new long[]{0x0000000200000002L} );
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression2249                 = new BitSet( new long[]{0x0000000C00000002L} );
    public static final BitSet FOLLOW_set_in_equalityExpression2253                                  = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression2260                 = new BitSet( new long[]{0x0000000C00000002L} );
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression2275                    = new BitSet( new long[]{0x000000F000000002L, 0x0000800000000000L} );
    public static final BitSet FOLLOW_set_in_relationalExpression2285                                = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression2321                    = new BitSet( new long[]{0x000000F000000002L} );
    public static final BitSet FOLLOW_111_in_relationalExpression2333                                = new BitSet( new long[]{0x0000000000000040L, 0x0000000000001FF0L} );
    public static final BitSet FOLLOW_typeSpec_in_relationalExpression2335                           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression2352                      = new BitSet( new long[]{0x0000070000000002L} );
    public static final BitSet FOLLOW_set_in_shiftExpression2356                                     = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression2367                      = new BitSet( new long[]{0x0000070000000002L} );
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression2382             = new BitSet( new long[]{0x0000180000000002L} );
    public static final BitSet FOLLOW_set_in_additiveExpression2386                                  = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression2393             = new BitSet( new long[]{0x0000180000000002L} );
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression2408                = new BitSet( new long[]{0x0000600000000102L} );
    public static final BitSet FOLLOW_set_in_multiplicativeExpression2412                            = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression2424                = new BitSet( new long[]{0x0000600000000102L} );
    public static final BitSet FOLLOW_INC_in_unaryExpression2437                                     = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression2439                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_DEC_in_unaryExpression2444                                     = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression2446                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_MINUS_in_unaryExpression2451                                   = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression2454                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_PLUS_in_unaryExpression2459                                    = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression2463                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression2468             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_BNOT_in_unaryExpressionNotPlusMinus2479                        = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2481             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LNOT_in_unaryExpressionNotPlusMinus2486                        = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2488             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LPAREN_in_unaryExpressionNotPlusMinus2499                      = new BitSet( new long[]{0x0000000000000000L, 0x0000000000001FF0L} );
    public static final BitSet FOLLOW_builtInTypeSpec_in_unaryExpressionNotPlusMinus2501             = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_unaryExpressionNotPlusMinus2503                      = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2513             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LPAREN_in_unaryExpressionNotPlusMinus2549                      = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_classTypeSpec_in_unaryExpressionNotPlusMinus2551               = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_unaryExpressionNotPlusMinus2553                      = new BitSet( new long[]{0x007E000000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpressionNotPlusMinus2563 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_postfixExpression_in_unaryExpressionNotPlusMinus2572           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_primaryExpression_in_postfixExpression2586                     = new BitSet( new long[]{0x0001800000000092L} );
    public static final BitSet FOLLOW_DOT_in_postfixExpression2594                                   = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_IDENT_in_postfixExpression2596                                 = new BitSet( new long[]{0x0001800000002092L} );
    public static final BitSet FOLLOW_LPAREN_in_postfixExpression2603                                = new BitSet( new long[]{0x007F980000006040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_argList_in_postfixExpression2610                               = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_postfixExpression2616                                = new BitSet( new long[]{0x0001800000000092L} );
    public static final BitSet FOLLOW_DOT_in_postfixExpression2628                                   = new BitSet( new long[]{0x0000000000000000L, 0x0000000020000000L} );
    public static final BitSet FOLLOW_93_in_postfixExpression2630                                    = new BitSet( new long[]{0x0001800000000092L} );
    public static final BitSet FOLLOW_DOT_in_postfixExpression2637                                   = new BitSet( new long[]{0x0000000000000000L, 0x0000000040000000L} );
    public static final BitSet FOLLOW_94_in_postfixExpression2639                                    = new BitSet( new long[]{0x0000000000002080L} );
    public static final BitSet FOLLOW_LPAREN_in_postfixExpression2674                                = new BitSet( new long[]{0x007F980000006040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_argList_in_postfixExpression2676                               = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_postfixExpression2678                                = new BitSet( new long[]{0x0001800000000092L} );
    public static final BitSet FOLLOW_DOT_in_postfixExpression2704                                   = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_IDENT_in_postfixExpression2706                                 = new BitSet( new long[]{0x0001800000002092L} );
    public static final BitSet FOLLOW_LPAREN_in_postfixExpression2726                                = new BitSet( new long[]{0x007F980000006040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_argList_in_postfixExpression2749                               = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_postfixExpression2771                                = new BitSet( new long[]{0x0001800000000092L} );
    public static final BitSet FOLLOW_DOT_in_postfixExpression2810                                   = new BitSet( new long[]{0x0000000000000000L, 0x0008000000000000L} );
    public static final BitSet FOLLOW_newExpression_in_postfixExpression2812                         = new BitSet( new long[]{0x0001800000000092L} );
    public static final BitSet FOLLOW_LBRACK_in_postfixExpression2818                                = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_expression_in_postfixExpression2821                            = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_RBRACK_in_postfixExpression2823                                = new BitSet( new long[]{0x0001800000000092L} );
    public static final BitSet FOLLOW_set_in_postfixExpression2854                                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_identPrimary_in_primaryExpression2881                          = new BitSet( new long[]{0x0000000000000082L} );
    public static final BitSet FOLLOW_DOT_in_primaryExpression2893                                   = new BitSet( new long[]{0x0000000000000000L, 0x0000000002000000L} );
    public static final BitSet FOLLOW_89_in_primaryExpression2895                                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_constant_in_primaryExpression2908                              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_112_in_primaryExpression2913                                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_113_in_primaryExpression2918                                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_114_in_primaryExpression2923                                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_newExpression_in_primaryExpression2933                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_93_in_primaryExpression2938                                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_94_in_primaryExpression2943                                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LPAREN_in_primaryExpression2948                                = new BitSet( new long[]{0x007F980000002040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_assignmentExpression_in_primaryExpression2950                  = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_primaryExpression2952                                = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_builtInType_in_primaryExpression2960                           = new BitSet( new long[]{0x0000000000000090L} );
    public static final BitSet FOLLOW_LBRACK_in_primaryExpression2966                                = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_RBRACK_in_primaryExpression2969                                = new BitSet( new long[]{0x0000000000000090L} );
    public static final BitSet FOLLOW_DOT_in_primaryExpression2976                                   = new BitSet( new long[]{0x0000000000000000L, 0x0000000002000000L} );
    public static final BitSet FOLLOW_89_in_primaryExpression2978                                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_IDENT_in_identPrimary2993                                      = new BitSet( new long[]{0x0000000000002092L} );
    public static final BitSet FOLLOW_DOT_in_identPrimary3031                                        = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_IDENT_in_identPrimary3033                                      = new BitSet( new long[]{0x0000000000002092L} );
    public static final BitSet FOLLOW_LPAREN_in_identPrimary3095                                     = new BitSet( new long[]{0x007F980000006040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_argList_in_identPrimary3098                                    = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_identPrimary3100                                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LBRACK_in_identPrimary3133                                     = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_RBRACK_in_identPrimary3136                                     = new BitSet( new long[]{0x0000000000000012L} );
    public static final BitSet FOLLOW_115_in_newExpression3172                                       = new BitSet( new long[]{0x0000000000000040L, 0x0000000000001FF0L} );
    public static final BitSet FOLLOW_type_in_newExpression3174                                      = new BitSet( new long[]{0x0000000000002010L} );
    public static final BitSet FOLLOW_LPAREN_in_newExpression3180                                    = new BitSet( new long[]{0x007F980000006040L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_argList_in_newExpression3182                                   = new BitSet( new long[]{0x0000000000004000L} );
    public static final BitSet FOLLOW_RPAREN_in_newExpression3184                                    = new BitSet( new long[]{0x0000000000000202L} );
    public static final BitSet FOLLOW_classBlock_in_newExpression3187                                = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_newArrayDeclarator_in_newExpression3225                        = new BitSet( new long[]{0x0000000000000202L} );
    public static final BitSet FOLLOW_arrayInitializer_in_newExpression3228                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_expressionList_in_argList3247                                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LBRACK_in_newArrayDeclarator3317                               = new BitSet( new long[]{0x007F980000002060L, 0x000F000060001FF0L} );
    public static final BitSet FOLLOW_expression_in_newArrayDeclarator3325                           = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_RBRACK_in_newArrayDeclarator3332                               = new BitSet( new long[]{0x0000000000000012L} );
    public static final BitSet FOLLOW_set_in_constant3348                                            = new BitSet( new long[]{0x0000000000000002L} );

}
