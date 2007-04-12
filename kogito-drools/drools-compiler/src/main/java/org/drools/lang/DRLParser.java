// $ANTLR 3.0b7 C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g 2007-04-11 16:59:51

package org.drools.lang;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import org.drools.lang.descr.*;
import org.drools.compiler.SwitchingCommonTokenStream;

import org.antlr.runtime.*;

public class DRLParser extends Parser {
    public static final String[] tokenNames                   = new String[]{"<invalid>", "<EOR>", "<DOWN>", "<UP>", "PACKAGE", "IMPORT", "FUNCTION", "GLOBAL", "QUERY", "END", "TEMPLATE", "RULE", "WHEN", "ATTRIBUTES", "DATE_EFFECTIVE", "STRING",
            "DATE_EXPIRES", "ENABLED", "BOOL", "SALIENCE", "INT", "NO_LOOP", "AUTO_FOCUS", "ACTIVATION_GROUP", "RULEFLOW_GROUP", "AGENDA_GROUP", "DURATION", "LOCK_ON_ACTIVE", "ACCUMULATE", "INIT", "ACTION", "RESULT", "COLLECT", "ID", "OR",
            "LEFT_PAREN", "RIGHT_PAREN", "CONTAINS", "MATCHES", "EXCLUDES", "FLOAT", "NULL", "LEFT_CURLY", "RIGHT_CURLY", "LEFT_SQUARE", "RIGHT_SQUARE", "AND", "FROM", "EXISTS", "NOT", "EVAL", "FORALL", "THEN", "EOL", "WS", "EscapeSequence",
            "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "MISC", "';'", "'.'", "'.*'", "','", "':'", "'||'", "'&'", "'|'", "'->'", "'=='", "'>'", "'>='", "'<'",
            "'<='", "'!='", "'&&'"                            };
    public static final int      PACKAGE                      = 4;
    public static final int      FUNCTION                     = 6;
    public static final int      ACCUMULATE                   = 28;
    public static final int      RIGHT_SQUARE                 = 45;
    public static final int      ACTIVATION_GROUP             = 23;
    public static final int      ATTRIBUTES                   = 13;
    public static final int      RIGHT_CURLY                  = 43;
    public static final int      CONTAINS                     = 37;
    public static final int      NO_LOOP                      = 21;
    public static final int      LOCK_ON_ACTIVE               = 27;
    public static final int      AGENDA_GROUP                 = 25;
    public static final int      FLOAT                        = 40;
    public static final int      SH_STYLE_SINGLE_LINE_COMMENT = 59;
    public static final int      NOT                          = 49;
    public static final int      AND                          = 46;
    public static final int      ID                           = 33;
    public static final int      EOF                          = -1;
    public static final int      HexDigit                     = 56;
    public static final int      DATE_EFFECTIVE               = 14;
    public static final int      ACTION                       = 30;
    public static final int      RIGHT_PAREN                  = 36;
    public static final int      IMPORT                       = 5;
    public static final int      EOL                          = 53;
    public static final int      THEN                         = 52;
    public static final int      MATCHES                      = 38;
    public static final int      ENABLED                      = 17;
    public static final int      EXISTS                       = 48;
    public static final int      RULE                         = 11;
    public static final int      EXCLUDES                     = 39;
    public static final int      AUTO_FOCUS                   = 22;
    public static final int      NULL                         = 41;
    public static final int      BOOL                         = 18;
    public static final int      FORALL                       = 51;
    public static final int      SALIENCE                     = 19;
    public static final int      RULEFLOW_GROUP               = 24;
    public static final int      RESULT                       = 31;
    public static final int      INT                          = 20;
    public static final int      MULTI_LINE_COMMENT           = 61;
    public static final int      DURATION                     = 26;
    public static final int      WS                           = 54;
    public static final int      EVAL                         = 50;
    public static final int      TEMPLATE                     = 10;
    public static final int      WHEN                         = 12;
    public static final int      UnicodeEscape                = 57;
    public static final int      LEFT_CURLY                   = 42;
    public static final int      OR                           = 34;
    public static final int      LEFT_PAREN                   = 35;
    public static final int      QUERY                        = 8;
    public static final int      MISC                         = 62;
    public static final int      FROM                         = 47;
    public static final int      END                          = 9;
    public static final int      GLOBAL                       = 7;
    public static final int      COLLECT                      = 32;
    public static final int      LEFT_SQUARE                  = 44;
    public static final int      INIT                         = 29;
    public static final int      OctalEscape                  = 58;
    public static final int      EscapeSequence               = 55;
    public static final int      C_STYLE_SINGLE_LINE_COMMENT  = 60;
    public static final int      DATE_EXPIRES                 = 16;
    public static final int      STRING                       = 15;

    public DRLParser(final TokenStream input) {
        super( input );
        this.ruleMemo = new HashMap[176 + 1];
    }

    public String[] getTokenNames() {
        return tokenNames;
    }

    public String getGrammarFileName() {
        return "C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g";
    }

    private PackageDescr packageDescr;
    private final List         errors      = new ArrayList();
    private String       source      = "unknown";
    private int          lineOffset  = 0;
    private final DescrFactory factory     = new DescrFactory();
    private boolean      parserDebug = false;

    // THE FOLLOWING LINE IS A DUMMY ATTRIBUTE TO WORK AROUND AN ANTLR BUG
    private final BaseDescr    from        = null;

    public void setParserDebug(final boolean parserDebug) {
        this.parserDebug = parserDebug;
    }

    public void debug(final String message) {
        if ( this.parserDebug ) {
            System.err.println( "drl parser: " + message );
        }
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public DescrFactory getFactory() {
        return this.factory;
    }

    public String getSource() {
        return this.source;
    }

    public PackageDescr getPackageDescr() {
        return this.packageDescr;
    }

    private int offset(final int line) {
        return line + this.lineOffset;
    }

    /**
     * This will set the offset to record when reparsing. Normally is zero of course 
     */
    public void setLineOffset(final int i) {
        this.lineOffset = i;
    }

    private String getString(final Token token) {
        final String orig = token.getText();
        return orig.substring( 1,
                               orig.length() - 1 );
    }

    public void reportError(final RecognitionException ex) {
        // if we've already reported an error and have not matched a token
        // yet successfully, don't report any errors.
        if ( this.errorRecovery ) {
            return;
        }
        this.errorRecovery = true;

        ex.line = offset( ex.line ); //add the offset if there is one
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
        } else if ( e instanceof GeneralParseException ) {
            message.append( " " + e.getMessage() );
        }
        return message.toString();
    }

    void checkTrailingSemicolon(final String text,
                                final int line) {
        if ( text.trim().endsWith( ";" ) ) {
            this.errors.add( new GeneralParseException( "Trailing semi-colon not allowed",
                                                        offset( line ) ) );
        }
    }

    // $ANTLR start opt_semicolon
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:168:1: opt_semicolon : ( ( ';' )=> ';' )? ;
    public void opt_semicolon() throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:169:4: ( ( ( ';' )=> ';' )? )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:169:4: ( ( ';' )=> ';' )?
            {
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:169:4: ( ( ';' )=> ';' )?
                int alt1 = 2;
                final int LA1_0 = this.input.LA( 1 );
                if ( (LA1_0 == 63) ) {
                    alt1 = 1;
                }
                switch ( alt1 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( ';' )=> ';'
                    {
                        match( this.input,
                               63,
                               FOLLOW_63_in_opt_semicolon46 );
                        if ( this.failed ) {
                            return;
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

    // $ANTLR end opt_semicolon

    // $ANTLR start compilation_unit
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:172:1: compilation_unit : prolog ( ( statement )=> statement )+ ;
    public void compilation_unit() throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:173:4: ( prolog ( ( statement )=> statement )+ )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:173:4: prolog ( ( statement )=> statement )+
            {
                pushFollow( FOLLOW_prolog_in_compilation_unit58 );
                prolog();
                this._fsp--;
                if ( this.failed ) {
                    return;
                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:174:3: ( ( statement )=> statement )+
                int cnt2 = 0;
                loop2 : do {
                    int alt2 = 2;
                    final int LA2_0 = this.input.LA( 1 );
                    if ( ((LA2_0 >= IMPORT && LA2_0 <= QUERY) || (LA2_0 >= TEMPLATE && LA2_0 <= RULE)) ) {
                        alt2 = 1;
                    }

                    switch ( alt2 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:174:5: ( statement )=> statement
                        {
                            pushFollow( FOLLOW_statement_in_compilation_unit65 );
                            statement();
                            this._fsp--;
                            if ( this.failed ) {
                                return;
                            }

                        }
                            break;

                        default :
                            if ( cnt2 >= 1 ) {
                                break loop2;
                            }
                            if ( this.backtracking > 0 ) {
                                this.failed = true;
                                return;
                            }
                            final EarlyExitException eee = new EarlyExitException( 2,
                                                                             this.input );
                            throw eee;
                    }
                    cnt2++;
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

    // $ANTLR end compilation_unit

    // $ANTLR start prolog
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:177:1: prolog : ( ( package_statement )=>n= package_statement )? ;
    public void prolog() throws RecognitionException {
        String n = null;

        String packageName = "";

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:181:4: ( ( ( package_statement )=>n= package_statement )? )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:181:4: ( ( package_statement )=>n= package_statement )?
            {
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:181:4: ( ( package_statement )=>n= package_statement )?
                int alt3 = 2;
                final int LA3_0 = this.input.LA( 1 );
                if ( (LA3_0 == PACKAGE) ) {
                    alt3 = 1;
                }
                switch ( alt3 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:181:6: ( package_statement )=>n= package_statement
                    {
                        pushFollow( FOLLOW_package_statement_in_prolog90 );
                        n = package_statement();
                        this._fsp--;
                        if ( this.failed ) {
                            return;
                        }
                        if ( this.backtracking == 0 ) {
                            packageName = n;
                        }

                    }
                        break;

                }

                if ( this.backtracking == 0 ) {

                    this.packageDescr = this.factory.createPackage( packageName );

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

    // $ANTLR end prolog

    // $ANTLR start statement
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:187:1: statement : ( ( function_import_statement )=> function_import_statement | ( import_statement )=> import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query ) ;
    public void statement() throws RecognitionException {
        FactTemplateDescr t = null;

        RuleDescr r = null;

        QueryDescr q = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:2: ( ( ( function_import_statement )=> function_import_statement | ( import_statement )=> import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:2: ( ( function_import_statement )=> function_import_statement | ( import_statement )=> import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )
            {
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:2: ( ( function_import_statement )=> function_import_statement | ( import_statement )=> import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )
                int alt4 = 7;
                switch ( this.input.LA( 1 ) ) {
                    case IMPORT :
                        if ( (synpred4()) ) {
                            alt4 = 1;
                        } else if ( (synpred5()) ) {
                            alt4 = 2;
                        } else {
                            if ( this.backtracking > 0 ) {
                                this.failed = true;
                                return;
                            }
                            final NoViableAltException nvae = new NoViableAltException( "189:2: ( ( function_import_statement )=> function_import_statement | ( import_statement )=> import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )",
                                                                                  4,
                                                                                  1,
                                                                                  this.input );

                            throw nvae;
                        }
                        break;
                    case GLOBAL :
                        alt4 = 3;
                        break;
                    case FUNCTION :
                        alt4 = 4;
                        break;
                    case TEMPLATE :
                        alt4 = 5;
                        break;
                    case RULE :
                        alt4 = 6;
                        break;
                    case QUERY :
                        alt4 = 7;
                        break;
                    default :
                        if ( this.backtracking > 0 ) {
                            this.failed = true;
                            return;
                        }
                        final NoViableAltException nvae = new NoViableAltException( "189:2: ( ( function_import_statement )=> function_import_statement | ( import_statement )=> import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )",
                                                                              4,
                                                                              0,
                                                                              this.input );

                        throw nvae;
                }

                switch ( alt4 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:4: ( function_import_statement )=> function_import_statement
                    {
                        pushFollow( FOLLOW_function_import_statement_in_statement114 );
                        function_import_statement();
                        this._fsp--;
                        if ( this.failed ) {
                            return;
                        }

                    }
                        break;
                    case 2 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:190:4: ( import_statement )=> import_statement
                    {
                        pushFollow( FOLLOW_import_statement_in_statement120 );
                        import_statement();
                        this._fsp--;
                        if ( this.failed ) {
                            return;
                        }

                    }
                        break;
                    case 3 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:191:4: ( global )=> global
                    {
                        pushFollow( FOLLOW_global_in_statement126 );
                        global();
                        this._fsp--;
                        if ( this.failed ) {
                            return;
                        }

                    }
                        break;
                    case 4 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:192:4: ( function )=> function
                    {
                        pushFollow( FOLLOW_function_in_statement132 );
                        function();
                        this._fsp--;
                        if ( this.failed ) {
                            return;
                        }

                    }
                        break;
                    case 5 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:193:10: ( template )=>t= template
                    {
                        pushFollow( FOLLOW_template_in_statement146 );
                        t = template();
                        this._fsp--;
                        if ( this.failed ) {
                            return;
                        }
                        if ( this.backtracking == 0 ) {
                            this.packageDescr.addFactTemplate( t );
                        }

                    }
                        break;
                    case 6 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:194:4: ( rule )=>r= rule
                    {
                        pushFollow( FOLLOW_rule_in_statement155 );
                        r = rule();
                        this._fsp--;
                        if ( this.failed ) {
                            return;
                        }
                        if ( this.backtracking == 0 ) {
                            if ( r != null ) {
                                this.packageDescr.addRule( r );
                            }
                        }

                    }
                        break;
                    case 7 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:195:4: q= query
                    {
                        pushFollow( FOLLOW_query_in_statement167 );
                        q = query();
                        this._fsp--;
                        if ( this.failed ) {
                            return;
                        }
                        if ( this.backtracking == 0 ) {
                            if ( q != null ) {
                                this.packageDescr.addRule( q );
                            }
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

    // $ANTLR end statement

    // $ANTLR start package_statement
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:199:1: package_statement returns [String packageName] : PACKAGE n= dotted_name[null] opt_semicolon ;
    public String package_statement() throws RecognitionException {
        String packageName = null;

        String n = null;

        packageName = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:204:3: ( PACKAGE n= dotted_name[null] opt_semicolon )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:204:3: PACKAGE n= dotted_name[null] opt_semicolon
            {
                match( this.input,
                       PACKAGE,
                       FOLLOW_PACKAGE_in_package_statement196 );
                if ( this.failed ) {
                    return packageName;
                }
                pushFollow( FOLLOW_dotted_name_in_package_statement200 );
                n = dotted_name( null );
                this._fsp--;
                if ( this.failed ) {
                    return packageName;
                }
                pushFollow( FOLLOW_opt_semicolon_in_package_statement203 );
                opt_semicolon();
                this._fsp--;
                if ( this.failed ) {
                    return packageName;
                }
                if ( this.backtracking == 0 ) {

                    packageName = n;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return packageName;
    }

    // $ANTLR end package_statement

    // $ANTLR start import_statement
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:211:1: import_statement : imp= IMPORT import_name[importDecl] opt_semicolon ;
    public void import_statement() throws RecognitionException {
        Token imp = null;

        ImportDescr importDecl = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:215:4: (imp= IMPORT import_name[importDecl] opt_semicolon )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:215:4: imp= IMPORT import_name[importDecl] opt_semicolon
            {
                imp = (Token) this.input.LT( 1 );
                match( this.input,
                       IMPORT,
                       FOLLOW_IMPORT_in_import_statement235 );
                if ( this.failed ) {
                    return;
                }
                if ( this.backtracking == 0 ) {

                    importDecl = this.factory.createImport();
                    importDecl.setStartCharacter( ((CommonToken) imp).getStartIndex() );
                    if ( this.packageDescr != null ) {
                        this.packageDescr.addImport( importDecl );
                    }

                }
                pushFollow( FOLLOW_import_name_in_import_statement258 );
                import_name( importDecl );
                this._fsp--;
                if ( this.failed ) {
                    return;
                }
                pushFollow( FOLLOW_opt_semicolon_in_import_statement261 );
                opt_semicolon();
                this._fsp--;
                if ( this.failed ) {
                    return;
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

    // $ANTLR end import_statement

    // $ANTLR start function_import_statement
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:226:1: function_import_statement : imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon ;
    public void function_import_statement() throws RecognitionException {
        Token imp = null;

        FunctionImportDescr importDecl = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:230:4: (imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:230:4: imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon
            {
                imp = (Token) this.input.LT( 1 );
                match( this.input,
                       IMPORT,
                       FOLLOW_IMPORT_in_function_import_statement287 );
                if ( this.failed ) {
                    return;
                }
                match( this.input,
                       FUNCTION,
                       FOLLOW_FUNCTION_in_function_import_statement289 );
                if ( this.failed ) {
                    return;
                }
                if ( this.backtracking == 0 ) {

                    importDecl = this.factory.createFunctionImport();
                    importDecl.setStartCharacter( ((CommonToken) imp).getStartIndex() );
                    if ( this.packageDescr != null ) {
                        this.packageDescr.addFunctionImport( importDecl );
                    }

                }
                pushFollow( FOLLOW_import_name_in_function_import_statement312 );
                import_name( importDecl );
                this._fsp--;
                if ( this.failed ) {
                    return;
                }
                pushFollow( FOLLOW_opt_semicolon_in_function_import_statement315 );
                opt_semicolon();
                this._fsp--;
                if ( this.failed ) {
                    return;
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

    // $ANTLR end function_import_statement

    // $ANTLR start import_name
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:242:1: import_name[ImportDescr importDecl] returns [String name] : id= identifier ( ( '.' identifier )=> '.' id= identifier )* ( ( '.*' )=>star= '.*' )? ;
    public String import_name(final ImportDescr importDecl) throws RecognitionException {
        String name = null;

        Token star = null;
        Token id = null;

        name = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:247:3: (id= identifier ( ( '.' identifier )=> '.' id= identifier )* ( ( '.*' )=>star= '.*' )? )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:247:3: id= identifier ( ( '.' identifier )=> '.' id= identifier )* ( ( '.*' )=>star= '.*' )?
            {
                pushFollow( FOLLOW_identifier_in_import_name343 );
                id = identifier();
                this._fsp--;
                if ( this.failed ) {
                    return name;
                }
                if ( this.backtracking == 0 ) {

                    name = id.getText();
                    importDecl.setTarget( name );
                    importDecl.setEndCharacter( ((CommonToken) id).getStopIndex() );

                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:253:3: ( ( '.' identifier )=> '.' id= identifier )*
                loop5 : do {
                    int alt5 = 2;
                    final int LA5_0 = this.input.LA( 1 );
                    if ( (LA5_0 == 64) ) {
                        alt5 = 1;
                    }

                    switch ( alt5 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:253:5: ( '.' identifier )=> '.' id= identifier
                        {
                            match( this.input,
                                   64,
                                   FOLLOW_64_in_import_name355 );
                            if ( this.failed ) {
                                return name;
                            }
                            pushFollow( FOLLOW_identifier_in_import_name359 );
                            id = identifier();
                            this._fsp--;
                            if ( this.failed ) {
                                return name;
                            }
                            if ( this.backtracking == 0 ) {

                                name = name + "." + id.getText();
                                importDecl.setTarget( name );
                                importDecl.setEndCharacter( ((CommonToken) id).getStopIndex() );

                            }

                        }
                            break;

                        default :
                            break loop5;
                    }
                } while ( true );

                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:260:3: ( ( '.*' )=>star= '.*' )?
                int alt6 = 2;
                final int LA6_0 = this.input.LA( 1 );
                if ( (LA6_0 == 65) ) {
                    alt6 = 1;
                }
                switch ( alt6 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:260:5: ( '.*' )=>star= '.*'
                    {
                        star = (Token) this.input.LT( 1 );
                        match( this.input,
                               65,
                               FOLLOW_65_in_import_name383 );
                        if ( this.failed ) {
                            return name;
                        }
                        if ( this.backtracking == 0 ) {

                            name = name + star.getText();
                            importDecl.setTarget( name );
                            importDecl.setEndCharacter( ((CommonToken) star).getStopIndex() );

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
        return name;
    }

    // $ANTLR end import_name

    // $ANTLR start global
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:270:1: global : loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon ;
    public void global() throws RecognitionException {
        Token loc = null;
        String type = null;

        Token id = null;

        GlobalDescr global = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:275:3: (loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:275:3: loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       GLOBAL,
                       FOLLOW_GLOBAL_in_global419 );
                if ( this.failed ) {
                    return;
                }
                if ( this.backtracking == 0 ) {

                    global = this.factory.createGlobal();
                    global.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                    this.packageDescr.addGlobal( global );

                }
                pushFollow( FOLLOW_dotted_name_in_global430 );
                type = dotted_name( null );
                this._fsp--;
                if ( this.failed ) {
                    return;
                }
                if ( this.backtracking == 0 ) {

                    global.setType( type );

                }
                pushFollow( FOLLOW_identifier_in_global442 );
                id = identifier();
                this._fsp--;
                if ( this.failed ) {
                    return;
                }
                pushFollow( FOLLOW_opt_semicolon_in_global444 );
                opt_semicolon();
                this._fsp--;
                if ( this.failed ) {
                    return;
                }
                if ( this.backtracking == 0 ) {

                    global.setIdentifier( id.getText() );
                    global.setEndCharacter( ((CommonToken) id).getStopIndex() );

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

    // $ANTLR end global

    // $ANTLR start function
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:293:1: function : loc= FUNCTION ( ( dotted_name[null] )=>retType= dotted_name[null] )? n= identifier '(' ( ( ( ( dotted_name[null] )=> dotted_name[null] )? argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )* )=> ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f] ;
    public void function() throws RecognitionException {
        Token loc = null;
        String retType = null;

        Token n = null;

        String paramType = null;

        String paramName = null;

        String body = null;

        FunctionDescr f = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:298:3: (loc= FUNCTION ( ( dotted_name[null] )=>retType= dotted_name[null] )? n= identifier '(' ( ( ( ( dotted_name[null] )=> dotted_name[null] )? argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )* )=> ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f] )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:298:3: loc= FUNCTION ( ( dotted_name[null] )=>retType= dotted_name[null] )? n= identifier '(' ( ( ( ( dotted_name[null] )=> dotted_name[null] )? argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )* )=> ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f]
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       FUNCTION,
                       FOLLOW_FUNCTION_in_function471 );
                if ( this.failed ) {
                    return;
                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:298:16: ( ( dotted_name[null] )=>retType= dotted_name[null] )?
                int alt7 = 2;
                final int LA7_0 = this.input.LA( 1 );
                if ( (LA7_0 == ID) ) {
                    final int LA7_1 = this.input.LA( 2 );
                    if ( ((LA7_1 >= PACKAGE && LA7_1 <= ATTRIBUTES) || LA7_1 == ENABLED || LA7_1 == SALIENCE || LA7_1 == DURATION || (LA7_1 >= ACCUMULATE && LA7_1 <= OR) || (LA7_1 >= CONTAINS && LA7_1 <= EXCLUDES) || LA7_1 == NULL
                          || LA7_1 == LEFT_SQUARE || (LA7_1 >= AND && LA7_1 <= THEN) || LA7_1 == 64) ) {
                        alt7 = 1;
                    }
                }
                switch ( alt7 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:298:17: ( dotted_name[null] )=>retType= dotted_name[null]
                    {
                        pushFollow( FOLLOW_dotted_name_in_function476 );
                        retType = dotted_name( null );
                        this._fsp--;
                        if ( this.failed ) {
                            return;
                        }

                    }
                        break;

                }

                pushFollow( FOLLOW_identifier_in_function483 );
                n = identifier();
                this._fsp--;
                if ( this.failed ) {
                    return;
                }
                if ( this.backtracking == 0 ) {

                    //System.err.println( "function :: " + n.getText() );
                    f = this.factory.createFunction( n.getText(),
                                                retType );
                    f.setLocation( offset( loc.getLine() ),
                                   loc.getCharPositionInLine() );
                    f.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                    this.packageDescr.addFunction( f );

                }
                match( this.input,
                       LEFT_PAREN,
                       FOLLOW_LEFT_PAREN_in_function492 );
                if ( this.failed ) {
                    return;
                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:307:4: ( ( ( ( dotted_name[null] )=> dotted_name[null] )? argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )* )=> ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument )* )?
                int alt11 = 2;
                final int LA11_0 = this.input.LA( 1 );
                if ( ((LA11_0 >= PACKAGE && LA11_0 <= ATTRIBUTES) || LA11_0 == ENABLED || LA11_0 == SALIENCE || LA11_0 == DURATION || (LA11_0 >= ACCUMULATE && LA11_0 <= OR) || (LA11_0 >= CONTAINS && LA11_0 <= EXCLUDES) || LA11_0 == NULL || (LA11_0 >= AND && LA11_0 <= THEN)) ) {
                    alt11 = 1;
                }
                switch ( alt11 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:307:6: ( ( ( dotted_name[null] )=> dotted_name[null] )? argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )* )=> ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument )*
                    {
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:307:6: ( ( dotted_name[null] )=>paramType= dotted_name[null] )?
                        int alt8 = 2;
                        alt8 = this.dfa8.predict( this.input );
                        switch ( alt8 ) {
                            case 1 :
                                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:307:7: ( dotted_name[null] )=>paramType= dotted_name[null]
                            {
                                pushFollow( FOLLOW_dotted_name_in_function502 );
                                paramType = dotted_name( null );
                                this._fsp--;
                                if ( this.failed ) {
                                    return;
                                }

                            }
                                break;

                        }

                        pushFollow( FOLLOW_argument_in_function509 );
                        paramName = argument();
                        this._fsp--;
                        if ( this.failed ) {
                            return;
                        }
                        if ( this.backtracking == 0 ) {

                            f.addParameter( paramType,
                                            paramName );

                        }
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:311:5: ( ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument )*
                        loop10 : do {
                            int alt10 = 2;
                            final int LA10_0 = this.input.LA( 1 );
                            if ( (LA10_0 == 66) ) {
                                alt10 = 1;
                            }

                            switch ( alt10 ) {
                                case 1 :
                                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:311:7: ( ',' ( ( dotted_name[null] )=> dotted_name[null] )? argument )=> ',' ( ( dotted_name[null] )=>paramType= dotted_name[null] )? paramName= argument
                                {
                                    match( this.input,
                                           66,
                                           FOLLOW_66_in_function523 );
                                    if ( this.failed ) {
                                        return;
                                    }
                                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:311:11: ( ( dotted_name[null] )=>paramType= dotted_name[null] )?
                                    int alt9 = 2;
                                    alt9 = this.dfa9.predict( this.input );
                                    switch ( alt9 ) {
                                        case 1 :
                                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:311:12: ( dotted_name[null] )=>paramType= dotted_name[null]
                                        {
                                            pushFollow( FOLLOW_dotted_name_in_function528 );
                                            paramType = dotted_name( null );
                                            this._fsp--;
                                            if ( this.failed ) {
                                                return;
                                            }

                                        }
                                            break;

                                    }

                                    pushFollow( FOLLOW_argument_in_function535 );
                                    paramName = argument();
                                    this._fsp--;
                                    if ( this.failed ) {
                                        return;
                                    }
                                    if ( this.backtracking == 0 ) {

                                        f.addParameter( paramType,
                                                        paramName );

                                    }

                                }
                                    break;

                                default :
                                    break loop10;
                            }
                        } while ( true );

                    }
                        break;

                }

                match( this.input,
                       RIGHT_PAREN,
                       FOLLOW_RIGHT_PAREN_in_function559 );
                if ( this.failed ) {
                    return;
                }
                pushFollow( FOLLOW_curly_chunk_in_function565 );
                body = curly_chunk( f );
                this._fsp--;
                if ( this.failed ) {
                    return;
                }
                if ( this.backtracking == 0 ) {

                    //strip out '{','}'
                    f.setText( body.substring( 1,
                                               body.length() - 1 ) );

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

    // $ANTLR end function

    // $ANTLR start query
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:327:1: query returns [QueryDescr query] : loc= QUERY queryName= name ( normal_lhs_block[lhs] ) loc= END ;
    public QueryDescr query() throws RecognitionException {
        QueryDescr query = null;

        Token loc = null;
        String queryName = null;

        query = null;
        AndDescr lhs = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:333:3: (loc= QUERY queryName= name ( normal_lhs_block[lhs] ) loc= END )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:333:3: loc= QUERY queryName= name ( normal_lhs_block[lhs] ) loc= END
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       QUERY,
                       FOLLOW_QUERY_in_query597 );
                if ( this.failed ) {
                    return query;
                }
                pushFollow( FOLLOW_name_in_query601 );
                queryName = name();
                this._fsp--;
                if ( this.failed ) {
                    return query;
                }
                if ( this.backtracking == 0 ) {

                    query = this.factory.createQuery( queryName );
                    query.setLocation( offset( loc.getLine() ),
                                       loc.getCharPositionInLine() );
                    query.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                    lhs = new AndDescr();
                    query.setLhs( lhs );
                    lhs.setLocation( offset( loc.getLine() ),
                                     loc.getCharPositionInLine() );

                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:341:3: ( normal_lhs_block[lhs] )
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:342:4: normal_lhs_block[lhs]
                {
                    pushFollow( FOLLOW_normal_lhs_block_in_query614 );
                    normal_lhs_block( lhs );
                    this._fsp--;
                    if ( this.failed ) {
                        return query;
                    }

                }

                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       END,
                       FOLLOW_END_in_query631 );
                if ( this.failed ) {
                    return query;
                }
                if ( this.backtracking == 0 ) {

                    query.setEndCharacter( ((CommonToken) loc).getStopIndex() );

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return query;
    }

    // $ANTLR end query

    // $ANTLR start template
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:352:1: template returns [FactTemplateDescr template] : loc= TEMPLATE templateName= identifier opt_semicolon ( ( template_slot )=>slot= template_slot )+ loc= END opt_semicolon ;
    public FactTemplateDescr template() throws RecognitionException {
        FactTemplateDescr template = null;

        Token loc = null;
        Token templateName = null;

        FieldTemplateDescr slot = null;

        template = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:357:3: (loc= TEMPLATE templateName= identifier opt_semicolon ( ( template_slot )=>slot= template_slot )+ loc= END opt_semicolon )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:357:3: loc= TEMPLATE templateName= identifier opt_semicolon ( ( template_slot )=>slot= template_slot )+ loc= END opt_semicolon
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       TEMPLATE,
                       FOLLOW_TEMPLATE_in_template661 );
                if ( this.failed ) {
                    return template;
                }
                pushFollow( FOLLOW_identifier_in_template665 );
                templateName = identifier();
                this._fsp--;
                if ( this.failed ) {
                    return template;
                }
                pushFollow( FOLLOW_opt_semicolon_in_template667 );
                opt_semicolon();
                this._fsp--;
                if ( this.failed ) {
                    return template;
                }
                if ( this.backtracking == 0 ) {

                    template = new FactTemplateDescr( templateName.getText() );
                    template.setLocation( offset( loc.getLine() ),
                                          loc.getCharPositionInLine() );
                    template.setStartCharacter( ((CommonToken) loc).getStartIndex() );

                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:363:3: ( ( template_slot )=>slot= template_slot )+
                int cnt12 = 0;
                loop12 : do {
                    int alt12 = 2;
                    final int LA12_0 = this.input.LA( 1 );
                    if ( (LA12_0 == ID) ) {
                        alt12 = 1;
                    }

                    switch ( alt12 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:364:4: ( template_slot )=>slot= template_slot
                        {
                            pushFollow( FOLLOW_template_slot_in_template682 );
                            slot = template_slot();
                            this._fsp--;
                            if ( this.failed ) {
                                return template;
                            }
                            if ( this.backtracking == 0 ) {

                                template.addFieldTemplate( slot );

                            }

                        }
                            break;

                        default :
                            if ( cnt12 >= 1 ) {
                                break loop12;
                            }
                            if ( this.backtracking > 0 ) {
                                this.failed = true;
                                return template;
                            }
                            final EarlyExitException eee = new EarlyExitException( 12,
                                                                             this.input );
                            throw eee;
                    }
                    cnt12++;
                } while ( true );

                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       END,
                       FOLLOW_END_in_template699 );
                if ( this.failed ) {
                    return template;
                }
                pushFollow( FOLLOW_opt_semicolon_in_template701 );
                opt_semicolon();
                this._fsp--;
                if ( this.failed ) {
                    return template;
                }
                if ( this.backtracking == 0 ) {

                    template.setEndCharacter( ((CommonToken) loc).getStopIndex() );

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return template;
    }

    // $ANTLR end template

    // $ANTLR start template_slot
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:375:1: template_slot returns [FieldTemplateDescr field] : fieldType= dotted_name[field] n= identifier opt_semicolon ;
    public FieldTemplateDescr template_slot() throws RecognitionException {
        FieldTemplateDescr field = null;

        String fieldType = null;

        Token n = null;

        field = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:380:11: (fieldType= dotted_name[field] n= identifier opt_semicolon )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:380:11: fieldType= dotted_name[field] n= identifier opt_semicolon
            {
                if ( this.backtracking == 0 ) {

                    field = this.factory.createFieldTemplate();

                }
                pushFollow( FOLLOW_dotted_name_in_template_slot747 );
                fieldType = dotted_name( field );
                this._fsp--;
                if ( this.failed ) {
                    return field;
                }
                if ( this.backtracking == 0 ) {

                    field.setClassType( fieldType );

                }
                pushFollow( FOLLOW_identifier_in_template_slot765 );
                n = identifier();
                this._fsp--;
                if ( this.failed ) {
                    return field;
                }
                pushFollow( FOLLOW_opt_semicolon_in_template_slot767 );
                opt_semicolon();
                this._fsp--;
                if ( this.failed ) {
                    return field;
                }
                if ( this.backtracking == 0 ) {

                    field.setName( n.getText() );
                    field.setLocation( offset( n.getLine() ),
                                       n.getCharPositionInLine() );
                    field.setEndCharacter( ((CommonToken) n).getStopIndex() );

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return field;
    }

    // $ANTLR end template_slot

    // $ANTLR start rule
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:396:1: rule returns [RuleDescr rule] : loc= RULE ruleName= name rule_attributes[rule] ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule] ;
    public RuleDescr rule() throws RecognitionException {
        RuleDescr rule = null;

        Token loc = null;
        String ruleName = null;

        rule = null;
        final String consequence = "";
        AndDescr lhs = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:403:3: (loc= RULE ruleName= name rule_attributes[rule] ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule] )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:403:3: loc= RULE ruleName= name rule_attributes[rule] ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule]
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       RULE,
                       FOLLOW_RULE_in_rule800 );
                if ( this.failed ) {
                    return rule;
                }
                pushFollow( FOLLOW_name_in_rule804 );
                ruleName = name();
                this._fsp--;
                if ( this.failed ) {
                    return rule;
                }
                if ( this.backtracking == 0 ) {

                    debug( "start rule: " + ruleName );
                    rule = new RuleDescr( ruleName,
                                          null );
                    rule.setLocation( offset( loc.getLine() ),
                                      loc.getCharPositionInLine() );
                    rule.setStartCharacter( ((CommonToken) loc).getStartIndex() );

                }
                pushFollow( FOLLOW_rule_attributes_in_rule813 );
                rule_attributes( rule );
                this._fsp--;
                if ( this.failed ) {
                    return rule;
                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:411:3: ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )?
                int alt14 = 2;
                final int LA14_0 = this.input.LA( 1 );
                if ( (LA14_0 == WHEN) ) {
                    alt14 = 1;
                }
                switch ( alt14 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:411:5: ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] )
                    {
                        loc = (Token) this.input.LT( 1 );
                        match( this.input,
                               WHEN,
                               FOLLOW_WHEN_in_rule822 );
                        if ( this.failed ) {
                            return rule;
                        }
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:411:14: ( ( ':' )=> ':' )?
                        int alt13 = 2;
                        final int LA13_0 = this.input.LA( 1 );
                        if ( (LA13_0 == 67) ) {
                            alt13 = 1;
                        }
                        switch ( alt13 ) {
                            case 1 :
                                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( ':' )=> ':'
                            {
                                match( this.input,
                                       67,
                                       FOLLOW_67_in_rule824 );
                                if ( this.failed ) {
                                    return rule;
                                }

                            }
                                break;

                        }

                        if ( this.backtracking == 0 ) {

                            lhs = new AndDescr();
                            rule.setLhs( lhs );
                            lhs.setLocation( offset( loc.getLine() ),
                                             loc.getCharPositionInLine() );
                            lhs.setStartCharacter( ((CommonToken) loc).getStartIndex() );

                        }
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:417:4: ( normal_lhs_block[lhs] )
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:419:5: normal_lhs_block[lhs]
                        {
                            pushFollow( FOLLOW_normal_lhs_block_in_rule842 );
                            normal_lhs_block( lhs );
                            this._fsp--;
                            if ( this.failed ) {
                                return rule;
                            }

                        }

                    }
                        break;

                }

                pushFollow( FOLLOW_rhs_chunk_in_rule863 );
                rhs_chunk( rule );
                this._fsp--;
                if ( this.failed ) {
                    return rule;
                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return rule;
    }

    // $ANTLR end rule

    // $ANTLR start rule_attributes
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:428:1: rule_attributes[RuleDescr rule] : ( ( ATTRIBUTES ':' )=> ATTRIBUTES ':' )? ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )* ;
    public void rule_attributes(final RuleDescr rule) throws RecognitionException {
        AttributeDescr a = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:430:4: ( ( ( ATTRIBUTES ':' )=> ATTRIBUTES ':' )? ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )* )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:430:4: ( ( ATTRIBUTES ':' )=> ATTRIBUTES ':' )? ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )*
            {
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:430:4: ( ( ATTRIBUTES ':' )=> ATTRIBUTES ':' )?
                int alt15 = 2;
                final int LA15_0 = this.input.LA( 1 );
                if ( (LA15_0 == ATTRIBUTES) ) {
                    alt15 = 1;
                }
                switch ( alt15 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:430:5: ( ATTRIBUTES ':' )=> ATTRIBUTES ':'
                    {
                        match( this.input,
                               ATTRIBUTES,
                               FOLLOW_ATTRIBUTES_in_rule_attributes884 );
                        if ( this.failed ) {
                            return;
                        }
                        match( this.input,
                               67,
                               FOLLOW_67_in_rule_attributes886 );
                        if ( this.failed ) {
                            return;
                        }

                    }
                        break;

                }

                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:431:4: ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )*
                loop17 : do {
                    int alt17 = 2;
                    final int LA17_0 = this.input.LA( 1 );
                    if ( (LA17_0 == DATE_EFFECTIVE || (LA17_0 >= DATE_EXPIRES && LA17_0 <= ENABLED) || LA17_0 == SALIENCE || (LA17_0 >= NO_LOOP && LA17_0 <= LOCK_ON_ACTIVE) || LA17_0 == 66) ) {
                        alt17 = 1;
                    }

                    switch ( alt17 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:431:6: ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute
                        {
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:431:6: ( ( ',' )=> ',' )?
                            int alt16 = 2;
                            final int LA16_0 = this.input.LA( 1 );
                            if ( (LA16_0 == 66) ) {
                                alt16 = 1;
                            }
                            switch ( alt16 ) {
                                case 1 :
                                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( ',' )=> ','
                                {
                                    match( this.input,
                                           66,
                                           FOLLOW_66_in_rule_attributes895 );
                                    if ( this.failed ) {
                                        return;
                                    }

                                }
                                    break;

                            }

                            pushFollow( FOLLOW_rule_attribute_in_rule_attributes900 );
                            a = rule_attribute();
                            this._fsp--;
                            if ( this.failed ) {
                                return;
                            }
                            if ( this.backtracking == 0 ) {

                                rule.addAttribute( a );

                            }

                        }
                            break;

                        default :
                            break loop17;
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

    // $ANTLR end rule_attributes

    // $ANTLR start rule_attribute
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:440:1: rule_attribute returns [AttributeDescr d] : ( ( salience )=>a= salience | ( no_loop )=>a= no_loop | ( agenda_group )=>a= agenda_group | ( duration )=>a= duration | ( activation_group )=>a= activation_group | ( auto_focus )=>a= auto_focus | ( date_effective )=>a= date_effective | ( date_expires )=>a= date_expires | ( enabled )=>a= enabled | ( ruleflow_group )=>a= ruleflow_group | a= lock_on_active );
    public AttributeDescr rule_attribute() throws RecognitionException {
        AttributeDescr d = null;

        AttributeDescr a = null;

        d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:445:4: ( ( salience )=>a= salience | ( no_loop )=>a= no_loop | ( agenda_group )=>a= agenda_group | ( duration )=>a= duration | ( activation_group )=>a= activation_group | ( auto_focus )=>a= auto_focus | ( date_effective )=>a= date_effective | ( date_expires )=>a= date_expires | ( enabled )=>a= enabled | ( ruleflow_group )=>a= ruleflow_group | a= lock_on_active )
            int alt18 = 11;
            switch ( this.input.LA( 1 ) ) {
                case SALIENCE :
                    alt18 = 1;
                    break;
                case NO_LOOP :
                    alt18 = 2;
                    break;
                case AGENDA_GROUP :
                    alt18 = 3;
                    break;
                case DURATION :
                    alt18 = 4;
                    break;
                case ACTIVATION_GROUP :
                    alt18 = 5;
                    break;
                case AUTO_FOCUS :
                    alt18 = 6;
                    break;
                case DATE_EFFECTIVE :
                    alt18 = 7;
                    break;
                case DATE_EXPIRES :
                    alt18 = 8;
                    break;
                case ENABLED :
                    alt18 = 9;
                    break;
                case RULEFLOW_GROUP :
                    alt18 = 10;
                    break;
                case LOCK_ON_ACTIVE :
                    alt18 = 11;
                    break;
                default :
                    if ( this.backtracking > 0 ) {
                        this.failed = true;
                        return d;
                    }
                    final NoViableAltException nvae = new NoViableAltException( "440:1: rule_attribute returns [AttributeDescr d] : ( ( salience )=>a= salience | ( no_loop )=>a= no_loop | ( agenda_group )=>a= agenda_group | ( duration )=>a= duration | ( activation_group )=>a= activation_group | ( auto_focus )=>a= auto_focus | ( date_effective )=>a= date_effective | ( date_expires )=>a= date_expires | ( enabled )=>a= enabled | ( ruleflow_group )=>a= ruleflow_group | a= lock_on_active );",
                                                                          18,
                                                                          0,
                                                                          this.input );

                    throw nvae;
            }

            switch ( alt18 ) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:445:4: ( salience )=>a= salience
                {
                    pushFollow( FOLLOW_salience_in_rule_attribute941 );
                    a = salience();
                    this._fsp--;
                    if ( this.failed ) {
                        return d;
                    }
                    if ( this.backtracking == 0 ) {
                        d = a;
                    }

                }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:446:5: ( no_loop )=>a= no_loop
                {
                    pushFollow( FOLLOW_no_loop_in_rule_attribute951 );
                    a = no_loop();
                    this._fsp--;
                    if ( this.failed ) {
                        return d;
                    }
                    if ( this.backtracking == 0 ) {
                        d = a;
                    }

                }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:447:5: ( agenda_group )=>a= agenda_group
                {
                    pushFollow( FOLLOW_agenda_group_in_rule_attribute962 );
                    a = agenda_group();
                    this._fsp--;
                    if ( this.failed ) {
                        return d;
                    }
                    if ( this.backtracking == 0 ) {
                        d = a;
                    }

                }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:448:5: ( duration )=>a= duration
                {
                    pushFollow( FOLLOW_duration_in_rule_attribute975 );
                    a = duration();
                    this._fsp--;
                    if ( this.failed ) {
                        return d;
                    }
                    if ( this.backtracking == 0 ) {
                        d = a;
                    }

                }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:449:5: ( activation_group )=>a= activation_group
                {
                    pushFollow( FOLLOW_activation_group_in_rule_attribute989 );
                    a = activation_group();
                    this._fsp--;
                    if ( this.failed ) {
                        return d;
                    }
                    if ( this.backtracking == 0 ) {
                        d = a;
                    }

                }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:450:5: ( auto_focus )=>a= auto_focus
                {
                    pushFollow( FOLLOW_auto_focus_in_rule_attribute1000 );
                    a = auto_focus();
                    this._fsp--;
                    if ( this.failed ) {
                        return d;
                    }
                    if ( this.backtracking == 0 ) {
                        d = a;
                    }

                }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:451:5: ( date_effective )=>a= date_effective
                {
                    pushFollow( FOLLOW_date_effective_in_rule_attribute1011 );
                    a = date_effective();
                    this._fsp--;
                    if ( this.failed ) {
                        return d;
                    }
                    if ( this.backtracking == 0 ) {
                        d = a;
                    }

                }
                    break;
                case 8 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:452:5: ( date_expires )=>a= date_expires
                {
                    pushFollow( FOLLOW_date_expires_in_rule_attribute1021 );
                    a = date_expires();
                    this._fsp--;
                    if ( this.failed ) {
                        return d;
                    }
                    if ( this.backtracking == 0 ) {
                        d = a;
                    }

                }
                    break;
                case 9 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:453:5: ( enabled )=>a= enabled
                {
                    pushFollow( FOLLOW_enabled_in_rule_attribute1031 );
                    a = enabled();
                    this._fsp--;
                    if ( this.failed ) {
                        return d;
                    }
                    if ( this.backtracking == 0 ) {
                        d = a;
                    }

                }
                    break;
                case 10 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:454:5: ( ruleflow_group )=>a= ruleflow_group
                {
                    pushFollow( FOLLOW_ruleflow_group_in_rule_attribute1041 );
                    a = ruleflow_group();
                    this._fsp--;
                    if ( this.failed ) {
                        return d;
                    }
                    if ( this.backtracking == 0 ) {
                        d = a;
                    }

                }
                    break;
                case 11 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:455:5: a= lock_on_active
                {
                    pushFollow( FOLLOW_lock_on_active_in_rule_attribute1051 );
                    a = lock_on_active();
                    this._fsp--;
                    if ( this.failed ) {
                        return d;
                    }
                    if ( this.backtracking == 0 ) {
                        d = a;
                    }

                }
                    break;

            }
        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end rule_attribute

    // $ANTLR start date_effective
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:459:1: date_effective returns [AttributeDescr d] : loc= DATE_EFFECTIVE val= STRING ;
    public AttributeDescr date_effective() throws RecognitionException {
        AttributeDescr d = null;

        Token loc = null;
        Token val = null;

        d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:464:3: (loc= DATE_EFFECTIVE val= STRING )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:464:3: loc= DATE_EFFECTIVE val= STRING
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       DATE_EFFECTIVE,
                       FOLLOW_DATE_EFFECTIVE_in_date_effective1082 );
                if ( this.failed ) {
                    return d;
                }
                val = (Token) this.input.LT( 1 );
                match( this.input,
                       STRING,
                       FOLLOW_STRING_in_date_effective1086 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    d = new AttributeDescr( "date-effective",
                                            getString( val ) );
                    d.setLocation( offset( loc.getLine() ),
                                   loc.getCharPositionInLine() );
                    d.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                    d.setEndCharacter( ((CommonToken) val).getStopIndex() );

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end date_effective

    // $ANTLR start date_expires
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:474:1: date_expires returns [AttributeDescr d] : loc= DATE_EXPIRES val= STRING ;
    public AttributeDescr date_expires() throws RecognitionException {
        AttributeDescr d = null;

        Token loc = null;
        Token val = null;

        d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:479:3: (loc= DATE_EXPIRES val= STRING )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:479:3: loc= DATE_EXPIRES val= STRING
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       DATE_EXPIRES,
                       FOLLOW_DATE_EXPIRES_in_date_expires1119 );
                if ( this.failed ) {
                    return d;
                }
                val = (Token) this.input.LT( 1 );
                match( this.input,
                       STRING,
                       FOLLOW_STRING_in_date_expires1123 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    d = new AttributeDescr( "date-expires",
                                            getString( val ) );
                    d.setLocation( offset( loc.getLine() ),
                                   loc.getCharPositionInLine() );
                    d.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                    d.setEndCharacter( ((CommonToken) val).getStopIndex() );

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end date_expires

    // $ANTLR start enabled
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:490:1: enabled returns [AttributeDescr d] : loc= ENABLED t= BOOL ;
    public AttributeDescr enabled() throws RecognitionException {
        AttributeDescr d = null;

        Token loc = null;
        Token t = null;

        d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:495:4: (loc= ENABLED t= BOOL )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:495:4: loc= ENABLED t= BOOL
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       ENABLED,
                       FOLLOW_ENABLED_in_enabled1158 );
                if ( this.failed ) {
                    return d;
                }
                t = (Token) this.input.LT( 1 );
                match( this.input,
                       BOOL,
                       FOLLOW_BOOL_in_enabled1162 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    d = new AttributeDescr( "enabled",
                                            t.getText() );
                    d.setLocation( offset( loc.getLine() ),
                                   loc.getCharPositionInLine() );
                    d.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                    d.setEndCharacter( ((CommonToken) t).getStopIndex() );

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end enabled

    // $ANTLR start salience
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:508:1: salience returns [AttributeDescr d ] : loc= SALIENCE i= INT ;
    public AttributeDescr salience() throws RecognitionException {
        AttributeDescr d = null;

        Token loc = null;
        Token i = null;

        d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:513:3: (loc= SALIENCE i= INT )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:513:3: loc= SALIENCE i= INT
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       SALIENCE,
                       FOLLOW_SALIENCE_in_salience1207 );
                if ( this.failed ) {
                    return d;
                }
                i = (Token) this.input.LT( 1 );
                match( this.input,
                       INT,
                       FOLLOW_INT_in_salience1211 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    d = new AttributeDescr( "salience",
                                            i.getText() );
                    d.setLocation( offset( loc.getLine() ),
                                   loc.getCharPositionInLine() );
                    d.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                    d.setEndCharacter( ((CommonToken) i).getStopIndex() );

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end salience

    // $ANTLR start no_loop
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:522:1: no_loop returns [AttributeDescr d] : ( ( ( NO_LOOP ) )=> (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );
    public AttributeDescr no_loop() throws RecognitionException {
        AttributeDescr d = null;

        Token loc = null;
        Token t = null;

        d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:527:3: ( ( ( NO_LOOP ) )=> (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) )
            int alt19 = 2;
            final int LA19_0 = this.input.LA( 1 );
            if ( (LA19_0 == NO_LOOP) ) {
                final int LA19_1 = this.input.LA( 2 );
                if ( (LA19_1 == BOOL) ) {
                    alt19 = 2;
                } else if ( (LA19_1 == EOF || LA19_1 == WHEN || LA19_1 == DATE_EFFECTIVE || (LA19_1 >= DATE_EXPIRES && LA19_1 <= ENABLED) || LA19_1 == SALIENCE || (LA19_1 >= NO_LOOP && LA19_1 <= LOCK_ON_ACTIVE) || LA19_1 == THEN || LA19_1 == 66) ) {
                    alt19 = 1;
                } else {
                    if ( this.backtracking > 0 ) {
                        this.failed = true;
                        return d;
                    }
                    final NoViableAltException nvae = new NoViableAltException( "522:1: no_loop returns [AttributeDescr d] : ( ( ( NO_LOOP ) )=> (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );",
                                                                          19,
                                                                          1,
                                                                          this.input );

                    throw nvae;
                }
            } else {
                if ( this.backtracking > 0 ) {
                    this.failed = true;
                    return d;
                }
                final NoViableAltException nvae = new NoViableAltException( "522:1: no_loop returns [AttributeDescr d] : ( ( ( NO_LOOP ) )=> (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );",
                                                                      19,
                                                                      0,
                                                                      this.input );

                throw nvae;
            }
            switch ( alt19 ) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:527:3: ( ( NO_LOOP ) )=> (loc= NO_LOOP )
                {
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:527:3: (loc= NO_LOOP )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:528:4: loc= NO_LOOP
                    {
                        loc = (Token) this.input.LT( 1 );
                        match( this.input,
                               NO_LOOP,
                               FOLLOW_NO_LOOP_in_no_loop1249 );
                        if ( this.failed ) {
                            return d;
                        }
                        if ( this.backtracking == 0 ) {

                            d = new AttributeDescr( "no-loop",
                                                    "true" );
                            d.setLocation( offset( loc.getLine() ),
                                           loc.getCharPositionInLine() );
                            d.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                            d.setEndCharacter( ((CommonToken) loc).getStopIndex() );

                        }

                    }

                }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:537:3: (loc= NO_LOOP t= BOOL )
                {
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:537:3: (loc= NO_LOOP t= BOOL )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:538:4: loc= NO_LOOP t= BOOL
                    {
                        loc = (Token) this.input.LT( 1 );
                        match( this.input,
                               NO_LOOP,
                               FOLLOW_NO_LOOP_in_no_loop1277 );
                        if ( this.failed ) {
                            return d;
                        }
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               BOOL,
                               FOLLOW_BOOL_in_no_loop1281 );
                        if ( this.failed ) {
                            return d;
                        }
                        if ( this.backtracking == 0 ) {

                            d = new AttributeDescr( "no-loop",
                                                    t.getText() );
                            d.setLocation( offset( loc.getLine() ),
                                           loc.getCharPositionInLine() );
                            d.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                            d.setEndCharacter( ((CommonToken) t).getStopIndex() );

                        }

                    }

                }
                    break;

            }
        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end no_loop

    // $ANTLR start auto_focus
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:550:1: auto_focus returns [AttributeDescr d] : ( ( ( AUTO_FOCUS ) )=> (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );
    public AttributeDescr auto_focus() throws RecognitionException {
        AttributeDescr d = null;

        Token loc = null;
        Token t = null;

        d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:555:3: ( ( ( AUTO_FOCUS ) )=> (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) )
            int alt20 = 2;
            final int LA20_0 = this.input.LA( 1 );
            if ( (LA20_0 == AUTO_FOCUS) ) {
                final int LA20_1 = this.input.LA( 2 );
                if ( (LA20_1 == BOOL) ) {
                    alt20 = 2;
                } else if ( (LA20_1 == EOF || LA20_1 == WHEN || LA20_1 == DATE_EFFECTIVE || (LA20_1 >= DATE_EXPIRES && LA20_1 <= ENABLED) || LA20_1 == SALIENCE || (LA20_1 >= NO_LOOP && LA20_1 <= LOCK_ON_ACTIVE) || LA20_1 == THEN || LA20_1 == 66) ) {
                    alt20 = 1;
                } else {
                    if ( this.backtracking > 0 ) {
                        this.failed = true;
                        return d;
                    }
                    final NoViableAltException nvae = new NoViableAltException( "550:1: auto_focus returns [AttributeDescr d] : ( ( ( AUTO_FOCUS ) )=> (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );",
                                                                          20,
                                                                          1,
                                                                          this.input );

                    throw nvae;
                }
            } else {
                if ( this.backtracking > 0 ) {
                    this.failed = true;
                    return d;
                }
                final NoViableAltException nvae = new NoViableAltException( "550:1: auto_focus returns [AttributeDescr d] : ( ( ( AUTO_FOCUS ) )=> (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );",
                                                                      20,
                                                                      0,
                                                                      this.input );

                throw nvae;
            }
            switch ( alt20 ) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:555:3: ( ( AUTO_FOCUS ) )=> (loc= AUTO_FOCUS )
                {
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:555:3: (loc= AUTO_FOCUS )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:556:4: loc= AUTO_FOCUS
                    {
                        loc = (Token) this.input.LT( 1 );
                        match( this.input,
                               AUTO_FOCUS,
                               FOLLOW_AUTO_FOCUS_in_auto_focus1330 );
                        if ( this.failed ) {
                            return d;
                        }
                        if ( this.backtracking == 0 ) {

                            d = new AttributeDescr( "auto-focus",
                                                    "true" );
                            d.setLocation( offset( loc.getLine() ),
                                           loc.getCharPositionInLine() );
                            d.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                            d.setEndCharacter( ((CommonToken) loc).getStopIndex() );

                        }

                    }

                }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:565:3: (loc= AUTO_FOCUS t= BOOL )
                {
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:565:3: (loc= AUTO_FOCUS t= BOOL )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:566:4: loc= AUTO_FOCUS t= BOOL
                    {
                        loc = (Token) this.input.LT( 1 );
                        match( this.input,
                               AUTO_FOCUS,
                               FOLLOW_AUTO_FOCUS_in_auto_focus1358 );
                        if ( this.failed ) {
                            return d;
                        }
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               BOOL,
                               FOLLOW_BOOL_in_auto_focus1362 );
                        if ( this.failed ) {
                            return d;
                        }
                        if ( this.backtracking == 0 ) {

                            d = new AttributeDescr( "auto-focus",
                                                    t.getText() );
                            d.setLocation( offset( loc.getLine() ),
                                           loc.getCharPositionInLine() );
                            d.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                            d.setEndCharacter( ((CommonToken) t).getStopIndex() );

                        }

                    }

                }
                    break;

            }
        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end auto_focus

    // $ANTLR start activation_group
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:578:1: activation_group returns [AttributeDescr d] : loc= ACTIVATION_GROUP n= STRING ;
    public AttributeDescr activation_group() throws RecognitionException {
        AttributeDescr d = null;

        Token loc = null;
        Token n = null;

        d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:583:3: (loc= ACTIVATION_GROUP n= STRING )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:583:3: loc= ACTIVATION_GROUP n= STRING
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       ACTIVATION_GROUP,
                       FOLLOW_ACTIVATION_GROUP_in_activation_group1407 );
                if ( this.failed ) {
                    return d;
                }
                n = (Token) this.input.LT( 1 );
                match( this.input,
                       STRING,
                       FOLLOW_STRING_in_activation_group1411 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    d = new AttributeDescr( "activation-group",
                                            getString( n ) );
                    d.setLocation( offset( loc.getLine() ),
                                   loc.getCharPositionInLine() );
                    d.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                    d.setEndCharacter( ((CommonToken) n).getStopIndex() );

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end activation_group

    // $ANTLR start ruleflow_group
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:592:1: ruleflow_group returns [AttributeDescr d] : loc= RULEFLOW_GROUP n= STRING ;
    public AttributeDescr ruleflow_group() throws RecognitionException {
        AttributeDescr d = null;

        Token loc = null;
        Token n = null;

        d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:597:3: (loc= RULEFLOW_GROUP n= STRING )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:597:3: loc= RULEFLOW_GROUP n= STRING
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       RULEFLOW_GROUP,
                       FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1443 );
                if ( this.failed ) {
                    return d;
                }
                n = (Token) this.input.LT( 1 );
                match( this.input,
                       STRING,
                       FOLLOW_STRING_in_ruleflow_group1447 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    d = new AttributeDescr( "ruleflow-group",
                                            getString( n ) );
                    d.setLocation( offset( loc.getLine() ),
                                   loc.getCharPositionInLine() );
                    d.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                    d.setEndCharacter( ((CommonToken) n).getStopIndex() );

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end ruleflow_group

    // $ANTLR start agenda_group
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:606:1: agenda_group returns [AttributeDescr d] : loc= AGENDA_GROUP n= STRING ;
    public AttributeDescr agenda_group() throws RecognitionException {
        AttributeDescr d = null;

        Token loc = null;
        Token n = null;

        d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:611:3: (loc= AGENDA_GROUP n= STRING )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:611:3: loc= AGENDA_GROUP n= STRING
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       AGENDA_GROUP,
                       FOLLOW_AGENDA_GROUP_in_agenda_group1479 );
                if ( this.failed ) {
                    return d;
                }
                n = (Token) this.input.LT( 1 );
                match( this.input,
                       STRING,
                       FOLLOW_STRING_in_agenda_group1483 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    d = new AttributeDescr( "agenda-group",
                                            getString( n ) );
                    d.setLocation( offset( loc.getLine() ),
                                   loc.getCharPositionInLine() );
                    d.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                    d.setEndCharacter( ((CommonToken) n).getStopIndex() );

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end agenda_group

    // $ANTLR start duration
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:621:1: duration returns [AttributeDescr d] : loc= DURATION i= INT ;
    public AttributeDescr duration() throws RecognitionException {
        AttributeDescr d = null;

        Token loc = null;
        Token i = null;

        d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:626:3: (loc= DURATION i= INT )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:626:3: loc= DURATION i= INT
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       DURATION,
                       FOLLOW_DURATION_in_duration1518 );
                if ( this.failed ) {
                    return d;
                }
                i = (Token) this.input.LT( 1 );
                match( this.input,
                       INT,
                       FOLLOW_INT_in_duration1522 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    d = new AttributeDescr( "duration",
                                            i.getText() );
                    d.setLocation( offset( loc.getLine() ),
                                   loc.getCharPositionInLine() );
                    d.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                    d.setEndCharacter( ((CommonToken) i).getStopIndex() );

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end duration

    // $ANTLR start lock_on_active
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:637:1: lock_on_active returns [AttributeDescr d] : ( ( ( LOCK_ON_ACTIVE ) )=> (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) );
    public AttributeDescr lock_on_active() throws RecognitionException {
        AttributeDescr d = null;

        Token loc = null;
        Token t = null;

        d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:642:3: ( ( ( LOCK_ON_ACTIVE ) )=> (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) )
            int alt21 = 2;
            final int LA21_0 = this.input.LA( 1 );
            if ( (LA21_0 == LOCK_ON_ACTIVE) ) {
                final int LA21_1 = this.input.LA( 2 );
                if ( (LA21_1 == BOOL) ) {
                    alt21 = 2;
                } else if ( (LA21_1 == EOF || LA21_1 == WHEN || LA21_1 == DATE_EFFECTIVE || (LA21_1 >= DATE_EXPIRES && LA21_1 <= ENABLED) || LA21_1 == SALIENCE || (LA21_1 >= NO_LOOP && LA21_1 <= LOCK_ON_ACTIVE) || LA21_1 == THEN || LA21_1 == 66) ) {
                    alt21 = 1;
                } else {
                    if ( this.backtracking > 0 ) {
                        this.failed = true;
                        return d;
                    }
                    final NoViableAltException nvae = new NoViableAltException( "637:1: lock_on_active returns [AttributeDescr d] : ( ( ( LOCK_ON_ACTIVE ) )=> (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) );",
                                                                          21,
                                                                          1,
                                                                          this.input );

                    throw nvae;
                }
            } else {
                if ( this.backtracking > 0 ) {
                    this.failed = true;
                    return d;
                }
                final NoViableAltException nvae = new NoViableAltException( "637:1: lock_on_active returns [AttributeDescr d] : ( ( ( LOCK_ON_ACTIVE ) )=> (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) );",
                                                                      21,
                                                                      0,
                                                                      this.input );

                throw nvae;
            }
            switch ( alt21 ) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:642:3: ( ( LOCK_ON_ACTIVE ) )=> (loc= LOCK_ON_ACTIVE )
                {
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:642:3: (loc= LOCK_ON_ACTIVE )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:643:4: loc= LOCK_ON_ACTIVE
                    {
                        loc = (Token) this.input.LT( 1 );
                        match( this.input,
                               LOCK_ON_ACTIVE,
                               FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1564 );
                        if ( this.failed ) {
                            return d;
                        }
                        if ( this.backtracking == 0 ) {

                            d = new AttributeDescr( "lock-on-active",
                                                    "true" );
                            d.setLocation( offset( loc.getLine() ),
                                           loc.getCharPositionInLine() );
                            d.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                            d.setEndCharacter( ((CommonToken) loc).getStopIndex() );

                        }

                    }

                }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:652:3: (loc= LOCK_ON_ACTIVE t= BOOL )
                {
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:652:3: (loc= LOCK_ON_ACTIVE t= BOOL )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:653:4: loc= LOCK_ON_ACTIVE t= BOOL
                    {
                        loc = (Token) this.input.LT( 1 );
                        match( this.input,
                               LOCK_ON_ACTIVE,
                               FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1592 );
                        if ( this.failed ) {
                            return d;
                        }
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               BOOL,
                               FOLLOW_BOOL_in_lock_on_active1596 );
                        if ( this.failed ) {
                            return d;
                        }
                        if ( this.backtracking == 0 ) {

                            d = new AttributeDescr( "lock-on-active",
                                                    t.getText() );
                            d.setLocation( offset( loc.getLine() ),
                                           loc.getCharPositionInLine() );
                            d.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                            d.setEndCharacter( ((CommonToken) t).getStopIndex() );

                        }

                    }

                }
                    break;

            }
        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end lock_on_active

    // $ANTLR start normal_lhs_block
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:665:1: normal_lhs_block[AndDescr descr] : ( ( lhs[descr] )=>d= lhs[descr] )* ;
    public void normal_lhs_block(final AndDescr descr) throws RecognitionException {
        BaseDescr d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:667:3: ( ( ( lhs[descr] )=>d= lhs[descr] )* )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:667:3: ( ( lhs[descr] )=>d= lhs[descr] )*
            {
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:667:3: ( ( lhs[descr] )=>d= lhs[descr] )*
                loop22 : do {
                    int alt22 = 2;
                    final int LA22_0 = this.input.LA( 1 );
                    if ( (LA22_0 == ID || LA22_0 == LEFT_PAREN || (LA22_0 >= EXISTS && LA22_0 <= FORALL)) ) {
                        alt22 = 1;
                    }

                    switch ( alt22 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:667:5: ( lhs[descr] )=>d= lhs[descr]
                        {
                            pushFollow( FOLLOW_lhs_in_normal_lhs_block1634 );
                            d = lhs( descr );
                            this._fsp--;
                            if ( this.failed ) {
                                return;
                            }
                            if ( this.backtracking == 0 ) {
                                if ( d != null ) {
                                    descr.addDescr( d );
                                }
                            }

                        }
                            break;

                        default :
                            break loop22;
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

    // $ANTLR end normal_lhs_block

    // $ANTLR start lhs
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:673:1: lhs[ConditionalElementDescr ce] returns [BaseDescr d] : l= lhs_or ;
    public BaseDescr lhs(final ConditionalElementDescr ce) throws RecognitionException {
        BaseDescr d = null;

        BaseDescr l = null;

        d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:677:4: (l= lhs_or )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:677:4: l= lhs_or
            {
                pushFollow( FOLLOW_lhs_or_in_lhs1671 );
                l = lhs_or();
                this._fsp--;
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {
                    d = l;
                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end lhs

    // $ANTLR start lhs_column
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:681:1: lhs_column returns [BaseDescr d] : ( ( fact_binding )=>f= fact_binding | f= fact );
    public BaseDescr lhs_column() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr f = null;

        d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:685:4: ( ( fact_binding )=>f= fact_binding | f= fact )
            int alt23 = 2;
            final int LA23_0 = this.input.LA( 1 );
            if ( (LA23_0 == ID) ) {
                final int LA23_1 = this.input.LA( 2 );
                if ( (LA23_1 == 67) ) {
                    alt23 = 1;
                } else if ( (LA23_1 == LEFT_PAREN || LA23_1 == LEFT_SQUARE || LA23_1 == 64) ) {
                    alt23 = 2;
                } else {
                    if ( this.backtracking > 0 ) {
                        this.failed = true;
                        return d;
                    }
                    final NoViableAltException nvae = new NoViableAltException( "681:1: lhs_column returns [BaseDescr d] : ( ( fact_binding )=>f= fact_binding | f= fact );",
                                                                          23,
                                                                          1,
                                                                          this.input );

                    throw nvae;
                }
            } else {
                if ( this.backtracking > 0 ) {
                    this.failed = true;
                    return d;
                }
                final NoViableAltException nvae = new NoViableAltException( "681:1: lhs_column returns [BaseDescr d] : ( ( fact_binding )=>f= fact_binding | f= fact );",
                                                                      23,
                                                                      0,
                                                                      this.input );

                throw nvae;
            }
            switch ( alt23 ) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:685:4: ( fact_binding )=>f= fact_binding
                {
                    pushFollow( FOLLOW_fact_binding_in_lhs_column1699 );
                    f = fact_binding();
                    this._fsp--;
                    if ( this.failed ) {
                        return d;
                    }
                    if ( this.backtracking == 0 ) {
                        d = f;
                    }

                }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:686:4: f= fact
                {
                    pushFollow( FOLLOW_fact_in_lhs_column1708 );
                    f = fact();
                    this._fsp--;
                    if ( this.failed ) {
                        return d;
                    }
                    if ( this.backtracking == 0 ) {
                        d = f;
                    }

                }
                    break;

            }
        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end lhs_column

    // $ANTLR start from_statement
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:689:1: from_statement returns [FromDescr d] : ds= from_source[d] ;
    public FromDescr from_statement() throws RecognitionException {
        FromDescr d = null;

        DeclarativeInvokerDescr ds = null;

        d = this.factory.createFrom();

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:694:2: (ds= from_source[d] )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:694:2: ds= from_source[d]
            {
                pushFollow( FOLLOW_from_source_in_from_statement1735 );
                ds = from_source( d );
                this._fsp--;
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    d.setDataSource( ds );

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end from_statement

    // $ANTLR start from_source
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:704:1: from_source[FromDescr from] returns [DeclarativeInvokerDescr ds] : ident= identifier ( ( paren_chunk[from] )=>args= paren_chunk[from] )? ( ( expression_chain[from, ad] )=> expression_chain[from, ad] )? ;
    public DeclarativeInvokerDescr from_source(final FromDescr from) throws RecognitionException {
        DeclarativeInvokerDescr ds = null;

        Token ident = null;

        String args = null;

        ds = null;
        AccessorDescr ad = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:710:3: (ident= identifier ( ( paren_chunk[from] )=>args= paren_chunk[from] )? ( ( expression_chain[from, ad] )=> expression_chain[from, ad] )? )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:710:3: ident= identifier ( ( paren_chunk[from] )=>args= paren_chunk[from] )? ( ( expression_chain[from, ad] )=> expression_chain[from, ad] )?
            {
                pushFollow( FOLLOW_identifier_in_from_source1777 );
                ident = identifier();
                this._fsp--;
                if ( this.failed ) {
                    return ds;
                }
                if ( this.backtracking == 0 ) {

                    ad = new AccessorDescr( ident.getText() );
                    ad.setLocation( offset( ident.getLine() ),
                                    ident.getCharPositionInLine() );
                    ad.setStartCharacter( ((CommonToken) ident).getStartIndex() );
                    ad.setEndCharacter( ((CommonToken) ident).getStopIndex() );
                    ds = ad;

                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:718:3: ( ( paren_chunk[from] )=>args= paren_chunk[from] )?
                int alt24 = 2;
                final int LA24_0 = this.input.LA( 1 );
                if ( (LA24_0 == LEFT_PAREN) ) {
                    if ( (synpred38()) ) {
                        alt24 = 1;
                    }
                }
                switch ( alt24 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:718:4: ( paren_chunk[from] )=>args= paren_chunk[from]
                    {
                        pushFollow( FOLLOW_paren_chunk_in_from_source1788 );
                        args = paren_chunk( from );
                        this._fsp--;
                        if ( this.failed ) {
                            return ds;
                        }
                        if ( this.backtracking == 0 ) {

                            if ( args != null ) {
                                ad.setVariableName( null );
                                final FunctionCallDescr fc = new FunctionCallDescr( ident.getText() );
                                fc.setLocation( offset( ident.getLine() ),
                                                ident.getCharPositionInLine() );
                                fc.setArguments( args );
                                fc.setStartCharacter( ((CommonToken) ident).getStartIndex() );
                                fc.setEndCharacter( ((CommonToken) ident).getStopIndex() );
                                ad.addInvoker( fc );
                            }

                        }

                    }
                        break;

                }

                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:731:3: ( ( expression_chain[from, ad] )=> expression_chain[from, ad] )?
                int alt25 = 2;
                final int LA25_0 = this.input.LA( 1 );
                if ( (LA25_0 == 64) ) {
                    alt25 = 1;
                }
                switch ( alt25 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( expression_chain[from, ad] )=> expression_chain[from, ad]
                    {
                        pushFollow( FOLLOW_expression_chain_in_from_source1802 );
                        expression_chain( from,
                                          ad );
                        this._fsp--;
                        if ( this.failed ) {
                            return ds;
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
        return ds;
    }

    // $ANTLR end from_source

    // $ANTLR start expression_chain
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:734:1: expression_chain[FromDescr from, AccessorDescr as] : ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( ( expression_chain[from, as] )=> expression_chain[from, as] )? ) ;
    public void expression_chain(final FromDescr from,
                                 final AccessorDescr as) throws RecognitionException {
        Token field = null;

        String sqarg = null;

        String paarg = null;

        FieldAccessDescr fa = null;
        MethodAccessDescr ma = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:740:2: ( ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( ( expression_chain[from, as] )=> expression_chain[from, as] )? ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:740:2: ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( ( expression_chain[from, as] )=> expression_chain[from, as] )? )
            {
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:740:2: ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( ( expression_chain[from, as] )=> expression_chain[from, as] )? )
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:740:4: '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( ( expression_chain[from, as] )=> expression_chain[from, as] )?
                {
                    match( this.input,
                           64,
                           FOLLOW_64_in_expression_chain1827 );
                    if ( this.failed ) {
                        return;
                    }
                    pushFollow( FOLLOW_identifier_in_expression_chain1831 );
                    field = identifier();
                    this._fsp--;
                    if ( this.failed ) {
                        return;
                    }
                    if ( this.backtracking == 0 ) {

                        fa = new FieldAccessDescr( field.getText() );
                        fa.setLocation( offset( field.getLine() ),
                                        field.getCharPositionInLine() );
                        fa.setStartCharacter( ((CommonToken) field).getStartIndex() );
                        fa.setEndCharacter( ((CommonToken) field).getStopIndex() );

                    }
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:747:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )?
                    int alt26 = 3;
                    final int LA26_0 = this.input.LA( 1 );
                    if ( (LA26_0 == LEFT_SQUARE) ) {
                        alt26 = 1;
                    } else if ( (LA26_0 == LEFT_PAREN) ) {
                        if ( (synpred41()) ) {
                            alt26 = 2;
                        }
                    }
                    switch ( alt26 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:748:6: ( LEFT_SQUARE )=>sqarg= square_chunk[from]
                        {
                            pushFollow( FOLLOW_square_chunk_in_expression_chain1862 );
                            sqarg = square_chunk( from );
                            this._fsp--;
                            if ( this.failed ) {
                                return;
                            }
                            if ( this.backtracking == 0 ) {

                                fa.setArgument( sqarg );

                            }

                        }
                            break;
                        case 2 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:753:6: ( LEFT_PAREN )=>paarg= paren_chunk[from]
                        {
                            pushFollow( FOLLOW_paren_chunk_in_expression_chain1896 );
                            paarg = paren_chunk( from );
                            this._fsp--;
                            if ( this.failed ) {
                                return;
                            }
                            if ( this.backtracking == 0 ) {

                                ma = new MethodAccessDescr( field.getText(),
                                                            paarg );
                                ma.setLocation( offset( field.getLine() ),
                                                field.getCharPositionInLine() );
                                ma.setStartCharacter( ((CommonToken) field).getStartIndex() );

                            }

                        }
                            break;

                    }

                    if ( this.backtracking == 0 ) {

                        if ( ma != null ) {
                            as.addInvoker( ma );
                        } else {
                            as.addInvoker( fa );
                        }

                    }
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:767:4: ( ( expression_chain[from, as] )=> expression_chain[from, as] )?
                    int alt27 = 2;
                    final int LA27_0 = this.input.LA( 1 );
                    if ( (LA27_0 == 64) ) {
                        alt27 = 1;
                    }
                    switch ( alt27 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( expression_chain[from, as] )=> expression_chain[from, as]
                        {
                            pushFollow( FOLLOW_expression_chain_in_expression_chain1917 );
                            expression_chain( from,
                                              as );
                            this._fsp--;
                            if ( this.failed ) {
                                return;
                            }

                        }
                            break;

                    }

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

    // $ANTLR end expression_chain

    // $ANTLR start accumulate_statement
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:771:1: accumulate_statement returns [AccumulateDescr d] : loc= ACCUMULATE '(' column= lhs_column ',' INIT text= paren_chunk[null] ',' ACTION text= paren_chunk[null] ',' RESULT text= paren_chunk[null] loc= ')' ;
    public AccumulateDescr accumulate_statement() throws RecognitionException {
        AccumulateDescr d = null;

        Token loc = null;
        BaseDescr column = null;

        String text = null;

        d = this.factory.createAccumulate();

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:776:10: (loc= ACCUMULATE '(' column= lhs_column ',' INIT text= paren_chunk[null] ',' ACTION text= paren_chunk[null] ',' RESULT text= paren_chunk[null] loc= ')' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:776:10: loc= ACCUMULATE '(' column= lhs_column ',' INIT text= paren_chunk[null] ',' ACTION text= paren_chunk[null] ',' RESULT text= paren_chunk[null] loc= ')'
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       ACCUMULATE,
                       FOLLOW_ACCUMULATE_in_accumulate_statement1958 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    d.setLocation( offset( loc.getLine() ),
                                   loc.getCharPositionInLine() );
                    d.setStartCharacter( ((CommonToken) loc).getStartIndex() );

                }
                match( this.input,
                       LEFT_PAREN,
                       FOLLOW_LEFT_PAREN_in_accumulate_statement1968 );
                if ( this.failed ) {
                    return d;
                }
                pushFollow( FOLLOW_lhs_column_in_accumulate_statement1972 );
                column = lhs_column();
                this._fsp--;
                if ( this.failed ) {
                    return d;
                }
                match( this.input,
                       66,
                       FOLLOW_66_in_accumulate_statement1974 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    d.setSourceColumn( (ColumnDescr) column );

                }
                match( this.input,
                       INIT,
                       FOLLOW_INIT_in_accumulate_statement1983 );
                if ( this.failed ) {
                    return d;
                }
                pushFollow( FOLLOW_paren_chunk_in_accumulate_statement1987 );
                text = paren_chunk( null );
                this._fsp--;
                if ( this.failed ) {
                    return d;
                }
                match( this.input,
                       66,
                       FOLLOW_66_in_accumulate_statement1990 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    d.setInitCode( text.substring( 1,
                                                   text.length() - 1 ) );

                }
                match( this.input,
                       ACTION,
                       FOLLOW_ACTION_in_accumulate_statement1999 );
                if ( this.failed ) {
                    return d;
                }
                pushFollow( FOLLOW_paren_chunk_in_accumulate_statement2003 );
                text = paren_chunk( null );
                this._fsp--;
                if ( this.failed ) {
                    return d;
                }
                match( this.input,
                       66,
                       FOLLOW_66_in_accumulate_statement2006 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    d.setActionCode( text.substring( 1,
                                                     text.length() - 1 ) );

                }
                match( this.input,
                       RESULT,
                       FOLLOW_RESULT_in_accumulate_statement2015 );
                if ( this.failed ) {
                    return d;
                }
                pushFollow( FOLLOW_paren_chunk_in_accumulate_statement2019 );
                text = paren_chunk( null );
                this._fsp--;
                if ( this.failed ) {
                    return d;
                }
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       RIGHT_PAREN,
                       FOLLOW_RIGHT_PAREN_in_accumulate_statement2024 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    d.setResultCode( text.substring( 1,
                                                     text.length() - 1 ) );
                    d.setEndCharacter( ((CommonToken) loc).getStopIndex() );

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end accumulate_statement

    // $ANTLR start collect_statement
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:800:1: collect_statement returns [CollectDescr d] : loc= COLLECT '(' column= lhs_column loc= ')' ;
    public CollectDescr collect_statement() throws RecognitionException {
        CollectDescr d = null;

        Token loc = null;
        BaseDescr column = null;

        d = this.factory.createCollect();

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:805:10: (loc= COLLECT '(' column= lhs_column loc= ')' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:805:10: loc= COLLECT '(' column= lhs_column loc= ')'
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       COLLECT,
                       FOLLOW_COLLECT_in_collect_statement2067 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    d.setLocation( offset( loc.getLine() ),
                                   loc.getCharPositionInLine() );
                    d.setStartCharacter( ((CommonToken) loc).getStartIndex() );

                }
                match( this.input,
                       LEFT_PAREN,
                       FOLLOW_LEFT_PAREN_in_collect_statement2077 );
                if ( this.failed ) {
                    return d;
                }
                pushFollow( FOLLOW_lhs_column_in_collect_statement2081 );
                column = lhs_column();
                this._fsp--;
                if ( this.failed ) {
                    return d;
                }
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       RIGHT_PAREN,
                       FOLLOW_RIGHT_PAREN_in_collect_statement2085 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    d.setSourceColumn( (ColumnDescr) column );
                    d.setEndCharacter( ((CommonToken) loc).getStopIndex() );

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end collect_statement

    // $ANTLR start fact_binding
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:817:1: fact_binding returns [BaseDescr d] : id= ID ':' fe= fact_expression[id.getText()] ;
    public BaseDescr fact_binding() throws RecognitionException {
        BaseDescr d = null;

        Token id = null;
        BaseDescr fe = null;

        d = null;
        final boolean multi = false;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:823:4: (id= ID ':' fe= fact_expression[id.getText()] )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:823:4: id= ID ':' fe= fact_expression[id.getText()]
            {
                id = (Token) this.input.LT( 1 );
                match( this.input,
                       ID,
                       FOLLOW_ID_in_fact_binding2119 );
                if ( this.failed ) {
                    return d;
                }
                match( this.input,
                       67,
                       FOLLOW_67_in_fact_binding2121 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    // handling incomplete parsing
                    d = new ColumnDescr();
                    ((ColumnDescr) d).setIdentifier( id.getText() );

                }
                pushFollow( FOLLOW_fact_expression_in_fact_binding2134 );
                fe = fact_expression( id.getText() );
                this._fsp--;
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    // override previously instantiated column
                    d = fe;
                    if ( d != null ) {
                        d.setStartCharacter( ((CommonToken) id).getStartIndex() );
                    }

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end fact_binding

    // $ANTLR start fact_expression
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:839:2: fact_expression[String id] returns [BaseDescr pd] : ( ( '(' fact_expression[id] ')' )=> '(' fe= fact_expression[id] ')' | f= fact ( ( (OR|'||') fact )=> (OR|'||')f= fact )* );
    public BaseDescr fact_expression(final String id) throws RecognitionException {
        BaseDescr pd = null;

        BaseDescr fe = null;

        BaseDescr f = null;

        pd = null;
        boolean multi = false;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:844:5: ( ( '(' fact_expression[id] ')' )=> '(' fe= fact_expression[id] ')' | f= fact ( ( (OR|'||') fact )=> (OR|'||')f= fact )* )
            int alt29 = 2;
            final int LA29_0 = this.input.LA( 1 );
            if ( (LA29_0 == LEFT_PAREN) ) {
                alt29 = 1;
            } else if ( (LA29_0 == ID) ) {
                alt29 = 2;
            } else {
                if ( this.backtracking > 0 ) {
                    this.failed = true;
                    return pd;
                }
                final NoViableAltException nvae = new NoViableAltException( "839:2: fact_expression[String id] returns [BaseDescr pd] : ( ( '(' fact_expression[id] ')' )=> '(' fe= fact_expression[id] ')' | f= fact ( ( (OR|'||') fact )=> (OR|'||')f= fact )* );",
                                                                      29,
                                                                      0,
                                                                      this.input );

                throw nvae;
            }
            switch ( alt29 ) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:844:5: ( '(' fact_expression[id] ')' )=> '(' fe= fact_expression[id] ')'
                {
                    match( this.input,
                           LEFT_PAREN,
                           FOLLOW_LEFT_PAREN_in_fact_expression2166 );
                    if ( this.failed ) {
                        return pd;
                    }
                    pushFollow( FOLLOW_fact_expression_in_fact_expression2170 );
                    fe = fact_expression( id );
                    this._fsp--;
                    if ( this.failed ) {
                        return pd;
                    }
                    match( this.input,
                           RIGHT_PAREN,
                           FOLLOW_RIGHT_PAREN_in_fact_expression2173 );
                    if ( this.failed ) {
                        return pd;
                    }
                    if ( this.backtracking == 0 ) {
                        pd = fe;
                    }

                }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:845:6: f= fact ( ( (OR|'||') fact )=> (OR|'||')f= fact )*
                {
                    pushFollow( FOLLOW_fact_in_fact_expression2184 );
                    f = fact();
                    this._fsp--;
                    if ( this.failed ) {
                        return pd;
                    }
                    if ( this.backtracking == 0 ) {

                        ((ColumnDescr) f).setIdentifier( id );
                        pd = f;

                    }
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:850:4: ( ( (OR|'||') fact )=> (OR|'||')f= fact )*
                    loop28 : do {
                        int alt28 = 2;
                        final int LA28_0 = this.input.LA( 1 );
                        if ( (LA28_0 == OR || LA28_0 == 68) ) {
                            if ( (synpred44()) ) {
                                alt28 = 1;
                            }

                        }

                        switch ( alt28 ) {
                            case 1 :
                                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:850:6: ( (OR|'||') fact )=> (OR|'||')f= fact
                            {
                                if ( this.input.LA( 1 ) == OR || this.input.LA( 1 ) == 68 ) {
                                    this.input.consume();
                                    this.errorRecovery = false;
                                    this.failed = false;
                                } else {
                                    if ( this.backtracking > 0 ) {
                                        this.failed = true;
                                        return pd;
                                    }
                                    final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                             this.input );
                                    recoverFromMismatchedSet( this.input,
                                                              mse,
                                                              FOLLOW_set_in_fact_expression2197 );
                                    throw mse;
                                }

                                if ( this.backtracking == 0 ) {
                                    if ( !multi ) {
                                        final BaseDescr first = pd;
                                        pd = new OrDescr();
                                        ((OrDescr) pd).addDescr( first );
                                        multi = true;
                                    }

                                }
                                pushFollow( FOLLOW_fact_in_fact_expression2214 );
                                f = fact();
                                this._fsp--;
                                if ( this.failed ) {
                                    return pd;
                                }
                                if ( this.backtracking == 0 ) {

                                    ((ColumnDescr) f).setIdentifier( id );
                                    ((OrDescr) pd).addDescr( f );

                                }

                            }
                                break;

                            default :
                                break loop28;
                        }
                    } while ( true );

                }
                    break;

            }
        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return pd;
    }

    // $ANTLR end fact_expression

    // $ANTLR start fact
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:866:1: fact returns [BaseDescr d] : id= dotted_name[d] loc= LEFT_PAREN ( ( constraints[column] )=> constraints[column] )? endLoc= RIGHT_PAREN ;
    public BaseDescr fact() throws RecognitionException {
        BaseDescr d = null;

        Token loc = null;
        Token endLoc = null;
        String id = null;

        d = null;
        ColumnDescr column = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:872:11: (id= dotted_name[d] loc= LEFT_PAREN ( ( constraints[column] )=> constraints[column] )? endLoc= RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:872:11: id= dotted_name[d] loc= LEFT_PAREN ( ( constraints[column] )=> constraints[column] )? endLoc= RIGHT_PAREN
            {
                if ( this.backtracking == 0 ) {

                    column = new ColumnDescr();
                    d = column;

                }
                pushFollow( FOLLOW_dotted_name_in_fact2275 );
                id = dotted_name( d );
                this._fsp--;
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    column.setObjectType( id );
                    column.setEndCharacter( -1 );

                }
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       LEFT_PAREN,
                       FOLLOW_LEFT_PAREN_in_fact2289 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    column.setLocation( offset( loc.getLine() ),
                                        loc.getCharPositionInLine() );
                    column.setLeftParentCharacter( ((CommonToken) loc).getStartIndex() );

                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:885:4: ( ( constraints[column] )=> constraints[column] )?
                int alt30 = 2;
                final int LA30_0 = this.input.LA( 1 );
                if ( ((LA30_0 >= PACKAGE && LA30_0 <= ATTRIBUTES) || LA30_0 == ENABLED || LA30_0 == SALIENCE || LA30_0 == DURATION || (LA30_0 >= ACCUMULATE && LA30_0 <= LEFT_PAREN) || (LA30_0 >= CONTAINS && LA30_0 <= EXCLUDES) || LA30_0 == NULL || (LA30_0 >= AND && LA30_0 <= THEN)) ) {
                    alt30 = 1;
                }
                switch ( alt30 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:885:6: ( constraints[column] )=> constraints[column]
                    {
                        pushFollow( FOLLOW_constraints_in_fact2299 );
                        constraints( column );
                        this._fsp--;
                        if ( this.failed ) {
                            return d;
                        }

                    }
                        break;

                }

                endLoc = (Token) this.input.LT( 1 );
                match( this.input,
                       RIGHT_PAREN,
                       FOLLOW_RIGHT_PAREN_in_fact2312 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    if ( endLoc.getType() == RIGHT_PAREN ) {
                        column.setEndLocation( offset( endLoc.getLine() ),
                                               endLoc.getCharPositionInLine() );
                        column.setEndCharacter( ((CommonToken) endLoc).getStopIndex() );
                        column.setRightParentCharacter( ((CommonToken) endLoc).getStartIndex() );
                    }

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end fact

    // $ANTLR start constraints
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:897:1: constraints[ColumnDescr column] : ( ( constraint[column] )=> constraint[column] | predicate[column] ) ( ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )* ;
    public void constraints(final ColumnDescr column) throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:898:4: ( ( ( constraint[column] )=> constraint[column] | predicate[column] ) ( ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )* )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:898:4: ( ( constraint[column] )=> constraint[column] | predicate[column] ) ( ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )*
            {
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:898:4: ( ( constraint[column] )=> constraint[column] | predicate[column] )
                int alt31 = 2;
                final int LA31_0 = this.input.LA( 1 );
                if ( ((LA31_0 >= PACKAGE && LA31_0 <= ATTRIBUTES) || LA31_0 == ENABLED || LA31_0 == SALIENCE || LA31_0 == DURATION || (LA31_0 >= ACCUMULATE && LA31_0 <= OR) || (LA31_0 >= CONTAINS && LA31_0 <= EXCLUDES) || LA31_0 == NULL || (LA31_0 >= AND && LA31_0 <= THEN)) ) {
                    alt31 = 1;
                } else if ( (LA31_0 == LEFT_PAREN) ) {
                    alt31 = 2;
                } else {
                    if ( this.backtracking > 0 ) {
                        this.failed = true;
                        return;
                    }
                    final NoViableAltException nvae = new NoViableAltException( "898:4: ( ( constraint[column] )=> constraint[column] | predicate[column] )",
                                                                          31,
                                                                          0,
                                                                          this.input );

                    throw nvae;
                }
                switch ( alt31 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:898:5: ( constraint[column] )=> constraint[column]
                    {
                        pushFollow( FOLLOW_constraint_in_constraints2333 );
                        constraint( column );
                        this._fsp--;
                        if ( this.failed ) {
                            return;
                        }

                    }
                        break;
                    case 2 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:898:24: predicate[column]
                    {
                        pushFollow( FOLLOW_predicate_in_constraints2336 );
                        predicate( column );
                        this._fsp--;
                        if ( this.failed ) {
                            return;
                        }

                    }
                        break;

                }

                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:899:3: ( ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )*
                loop33 : do {
                    int alt33 = 2;
                    final int LA33_0 = this.input.LA( 1 );
                    if ( (LA33_0 == 66) ) {
                        alt33 = 1;
                    }

                    switch ( alt33 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:899:5: ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] )
                        {
                            match( this.input,
                                   66,
                                   FOLLOW_66_in_constraints2344 );
                            if ( this.failed ) {
                                return;
                            }
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:899:9: ( ( constraint[column] )=> constraint[column] | predicate[column] )
                            int alt32 = 2;
                            final int LA32_0 = this.input.LA( 1 );
                            if ( ((LA32_0 >= PACKAGE && LA32_0 <= ATTRIBUTES) || LA32_0 == ENABLED || LA32_0 == SALIENCE || LA32_0 == DURATION || (LA32_0 >= ACCUMULATE && LA32_0 <= OR) || (LA32_0 >= CONTAINS && LA32_0 <= EXCLUDES) || LA32_0 == NULL || (LA32_0 >= AND && LA32_0 <= THEN)) ) {
                                alt32 = 1;
                            } else if ( (LA32_0 == LEFT_PAREN) ) {
                                alt32 = 2;
                            } else {
                                if ( this.backtracking > 0 ) {
                                    this.failed = true;
                                    return;
                                }
                                final NoViableAltException nvae = new NoViableAltException( "899:9: ( ( constraint[column] )=> constraint[column] | predicate[column] )",
                                                                                      32,
                                                                                      0,
                                                                                      this.input );

                                throw nvae;
                            }
                            switch ( alt32 ) {
                                case 1 :
                                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:899:10: ( constraint[column] )=> constraint[column]
                                {
                                    pushFollow( FOLLOW_constraint_in_constraints2347 );
                                    constraint( column );
                                    this._fsp--;
                                    if ( this.failed ) {
                                        return;
                                    }

                                }
                                    break;
                                case 2 :
                                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:899:29: predicate[column]
                                {
                                    pushFollow( FOLLOW_predicate_in_constraints2350 );
                                    predicate( column );
                                    this._fsp--;
                                    if ( this.failed ) {
                                        return;
                                    }

                                }
                                    break;

                            }

                        }
                            break;

                        default :
                            break loop33;
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

    // $ANTLR end constraints

    // $ANTLR start constraint
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:902:1: constraint[ColumnDescr column] : ( ( ID ':' )=>fb= ID ':' )? f= identifier ( ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* ) | ( '->' predicate[column] )=> '->' predicate[column] )? ;
    public void constraint(final ColumnDescr column) throws RecognitionException {
        Token fb = null;
        Token con = null;
        Token f = null;

        RestrictionDescr rd = null;

        FieldBindingDescr fbd = null;
        FieldConstraintDescr fc = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:908:3: ( ( ( ID ':' )=>fb= ID ':' )? f= identifier ( ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* ) | ( '->' predicate[column] )=> '->' predicate[column] )? )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:908:3: ( ( ID ':' )=>fb= ID ':' )? f= identifier ( ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* ) | ( '->' predicate[column] )=> '->' predicate[column] )?
            {
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:908:3: ( ( ID ':' )=>fb= ID ':' )?
                int alt34 = 2;
                final int LA34_0 = this.input.LA( 1 );
                if ( (LA34_0 == ID) ) {
                    final int LA34_1 = this.input.LA( 2 );
                    if ( (LA34_1 == 67) ) {
                        alt34 = 1;
                    }
                }
                switch ( alt34 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:908:5: ( ID ':' )=>fb= ID ':'
                    {
                        fb = (Token) this.input.LT( 1 );
                        match( this.input,
                               ID,
                               FOLLOW_ID_in_constraint2379 );
                        if ( this.failed ) {
                            return;
                        }
                        match( this.input,
                               67,
                               FOLLOW_67_in_constraint2381 );
                        if ( this.failed ) {
                            return;
                        }
                        if ( this.backtracking == 0 ) {

                            fbd = new FieldBindingDescr();
                            fbd.setIdentifier( fb.getText() );
                            fbd.setLocation( offset( fb.getLine() ),
                                             fb.getCharPositionInLine() );
                            fbd.setStartCharacter( ((CommonToken) fb).getStartIndex() );
                            column.addDescr( fbd );

                        }

                    }
                        break;

                }

                pushFollow( FOLLOW_identifier_in_constraint2402 );
                f = identifier();
                this._fsp--;
                if ( this.failed ) {
                    return;
                }
                if ( this.backtracking == 0 ) {

                    if ( f != null ) {

                        if ( fbd != null ) {
                            fbd.setFieldName( f.getText() );
                            fbd.setEndCharacter( ((CommonToken) f).getStopIndex() );
                        }
                        fc = new FieldConstraintDescr( f.getText() );
                        fc.setLocation( offset( f.getLine() ),
                                        f.getCharPositionInLine() );
                        fc.setStartCharacter( ((CommonToken) f).getStartIndex() );

                        // it must be a field constraint, as it is not a binding
                        if ( fb == null ) {
                            column.addDescr( fc );
                        }
                    }

                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:936:3: ( ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* ) | ( '->' predicate[column] )=> '->' predicate[column] )?
                int alt36 = 3;
                final int LA36_0 = this.input.LA( 1 );
                if ( ((LA36_0 >= CONTAINS && LA36_0 <= EXCLUDES) || (LA36_0 >= 72 && LA36_0 <= 77)) ) {
                    alt36 = 1;
                } else if ( (LA36_0 == 71) ) {
                    alt36 = 2;
                }
                switch ( alt36 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:937:4: ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* )
                    {
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:937:4: (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* )
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:937:6: rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )*
                        {
                            pushFollow( FOLLOW_constraint_expression_in_constraint2420 );
                            rd = constraint_expression();
                            this._fsp--;
                            if ( this.failed ) {
                                return;
                            }
                            if ( this.backtracking == 0 ) {

                                fc.addRestriction( rd );
                                // we must add now as we didn't before
                                if ( fb != null ) {
                                    column.addDescr( fc );
                                }

                            }
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:945:5: ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )*
                            loop35 : do {
                                int alt35 = 2;
                                final int LA35_0 = this.input.LA( 1 );
                                if ( ((LA35_0 >= 69 && LA35_0 <= 70)) ) {
                                    alt35 = 1;
                                }

                                switch ( alt35 ) {
                                    case 1 :
                                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:946:6: ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression
                                    {
                                        con = (Token) this.input.LT( 1 );
                                        if ( (this.input.LA( 1 ) >= 69 && this.input.LA( 1 ) <= 70) ) {
                                            this.input.consume();
                                            this.errorRecovery = false;
                                            this.failed = false;
                                        } else {
                                            if ( this.backtracking > 0 ) {
                                                this.failed = true;
                                                return;
                                            }
                                            final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                                     this.input );
                                            recoverFromMismatchedSet( this.input,
                                                                      mse,
                                                                      FOLLOW_set_in_constraint2442 );
                                            throw mse;
                                        }

                                        if ( this.backtracking == 0 ) {

                                            if ( con.getText().equals( "&" ) ) {
                                                fc.addRestriction( new RestrictionConnectiveDescr( RestrictionConnectiveDescr.AND ) );
                                            } else {
                                                fc.addRestriction( new RestrictionConnectiveDescr( RestrictionConnectiveDescr.OR ) );
                                            }

                                        }
                                        pushFollow( FOLLOW_constraint_expression_in_constraint2461 );
                                        rd = constraint_expression();
                                        this._fsp--;
                                        if ( this.failed ) {
                                            return;
                                        }
                                        if ( this.backtracking == 0 ) {

                                            if ( rd != null ) {
                                                fc.addRestriction( rd );
                                            }

                                        }

                                    }
                                        break;

                                    default :
                                        break loop35;
                                }
                            } while ( true );

                        }

                    }
                        break;
                    case 2 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:963:4: ( '->' predicate[column] )=> '->' predicate[column]
                    {
                        match( this.input,
                               71,
                               FOLLOW_71_in_constraint2489 );
                        if ( this.failed ) {
                            return;
                        }
                        pushFollow( FOLLOW_predicate_in_constraint2491 );
                        predicate( column );
                        this._fsp--;
                        if ( this.failed ) {
                            return;
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

    // $ANTLR end constraint

    // $ANTLR start constraint_expression
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:967:1: constraint_expression returns [RestrictionDescr rd] : op= ('=='|'>'|'>='|'<'|'<='|'!='|CONTAINS|MATCHES|EXCLUDES) ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint ) ;
    public RestrictionDescr constraint_expression() throws RecognitionException {
        RestrictionDescr rd = null;

        Token op = null;
        Token bvc = null;
        String lc = null;

        String rvc = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:969:3: (op= ('=='|'>'|'>='|'<'|'<='|'!='|CONTAINS|MATCHES|EXCLUDES) ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:969:3: op= ('=='|'>'|'>='|'<'|'<='|'!='|CONTAINS|MATCHES|EXCLUDES) ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )
            {
                op = (Token) this.input.LT( 1 );
                if ( (this.input.LA( 1 ) >= CONTAINS && this.input.LA( 1 ) <= EXCLUDES) || (this.input.LA( 1 ) >= 72 && this.input.LA( 1 ) <= 77) ) {
                    this.input.consume();
                    this.errorRecovery = false;
                    this.failed = false;
                } else {
                    if ( this.backtracking > 0 ) {
                        this.failed = true;
                        return rd;
                    }
                    final MismatchedSetException mse = new MismatchedSetException( null,
                                                                             this.input );
                    recoverFromMismatchedSet( this.input,
                                              mse,
                                              FOLLOW_set_in_constraint_expression2528 );
                    throw mse;
                }

                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:979:3: ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )
                int alt37 = 4;
                switch ( this.input.LA( 1 ) ) {
                    case ID :
                        final int LA37_1 = this.input.LA( 2 );
                        if ( (LA37_1 == 64) ) {
                            alt37 = 2;
                        } else if ( (LA37_1 == EOF || LA37_1 == RIGHT_PAREN || LA37_1 == 66 || (LA37_1 >= 69 && LA37_1 <= 70)) ) {
                            alt37 = 1;
                        } else {
                            if ( this.backtracking > 0 ) {
                                this.failed = true;
                                return rd;
                            }
                            final NoViableAltException nvae = new NoViableAltException( "979:3: ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )",
                                                                                  37,
                                                                                  1,
                                                                                  this.input );

                            throw nvae;
                        }
                        break;
                    case STRING :
                    case BOOL :
                    case INT :
                    case FLOAT :
                    case NULL :
                        alt37 = 3;
                        break;
                    case LEFT_PAREN :
                        alt37 = 4;
                        break;
                    default :
                        if ( this.backtracking > 0 ) {
                            this.failed = true;
                            return rd;
                        }
                        final NoViableAltException nvae = new NoViableAltException( "979:3: ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )",
                                                                              37,
                                                                              0,
                                                                              this.input );

                        throw nvae;
                }

                switch ( alt37 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:979:5: ( ID )=>bvc= ID
                    {
                        bvc = (Token) this.input.LT( 1 );
                        match( this.input,
                               ID,
                               FOLLOW_ID_in_constraint_expression2595 );
                        if ( this.failed ) {
                            return rd;
                        }
                        if ( this.backtracking == 0 ) {

                            rd = new VariableRestrictionDescr( op.getText(),
                                                               bvc.getText() );

                        }

                    }
                        break;
                    case 2 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:984:4: ( enum_constraint )=>lc= enum_constraint
                    {
                        pushFollow( FOLLOW_enum_constraint_in_constraint_expression2611 );
                        lc = enum_constraint();
                        this._fsp--;
                        if ( this.failed ) {
                            return rd;
                        }
                        if ( this.backtracking == 0 ) {

                            rd = new LiteralRestrictionDescr( op.getText(),
                                                              lc,
                                                              true );

                        }

                    }
                        break;
                    case 3 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:989:4: ( literal_constraint )=>lc= literal_constraint
                    {
                        pushFollow( FOLLOW_literal_constraint_in_constraint_expression2634 );
                        lc = literal_constraint();
                        this._fsp--;
                        if ( this.failed ) {
                            return rd;
                        }
                        if ( this.backtracking == 0 ) {

                            rd = new LiteralRestrictionDescr( op.getText(),
                                                              lc );

                        }

                    }
                        break;
                    case 4 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:993:5: rvc= retval_constraint
                    {
                        pushFollow( FOLLOW_retval_constraint_in_constraint_expression2648 );
                        rvc = retval_constraint();
                        this._fsp--;
                        if ( this.failed ) {
                            return rd;
                        }
                        if ( this.backtracking == 0 ) {

                            rd = new ReturnValueRestrictionDescr( op.getText(),
                                                                  rvc );

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
        return rd;
    }

    // $ANTLR end constraint_expression

    // $ANTLR start literal_constraint
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1000:1: literal_constraint returns [String text] : ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= NULL ) ;
    public String literal_constraint() throws RecognitionException {
        String text = null;

        Token t = null;

        text = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1004:4: ( ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= NULL ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1004:4: ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= NULL )
            {
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1004:4: ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= NULL )
                int alt38 = 5;
                switch ( this.input.LA( 1 ) ) {
                    case STRING :
                        alt38 = 1;
                        break;
                    case INT :
                        alt38 = 2;
                        break;
                    case FLOAT :
                        alt38 = 3;
                        break;
                    case BOOL :
                        alt38 = 4;
                        break;
                    case NULL :
                        alt38 = 5;
                        break;
                    default :
                        if ( this.backtracking > 0 ) {
                            this.failed = true;
                            return text;
                        }
                        final NoViableAltException nvae = new NoViableAltException( "1004:4: ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= NULL )",
                                                                              38,
                                                                              0,
                                                                              this.input );

                        throw nvae;
                }

                switch ( alt38 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1004:6: ( STRING )=>t= STRING
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               STRING,
                               FOLLOW_STRING_in_literal_constraint2687 );
                        if ( this.failed ) {
                            return text;
                        }
                        if ( this.backtracking == 0 ) {
                            text = getString( t );
                        }

                    }
                        break;
                    case 2 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1005:5: ( INT )=>t= INT
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               INT,
                               FOLLOW_INT_in_literal_constraint2698 );
                        if ( this.failed ) {
                            return text;
                        }
                        if ( this.backtracking == 0 ) {
                            text = t.getText();
                        }

                    }
                        break;
                    case 3 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1006:5: ( FLOAT )=>t= FLOAT
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               FLOAT,
                               FOLLOW_FLOAT_in_literal_constraint2711 );
                        if ( this.failed ) {
                            return text;
                        }
                        if ( this.backtracking == 0 ) {
                            text = t.getText();
                        }

                    }
                        break;
                    case 4 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1007:5: ( BOOL )=>t= BOOL
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               BOOL,
                               FOLLOW_BOOL_in_literal_constraint2722 );
                        if ( this.failed ) {
                            return text;
                        }
                        if ( this.backtracking == 0 ) {
                            text = t.getText();
                        }

                    }
                        break;
                    case 5 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1008:5: t= NULL
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               NULL,
                               FOLLOW_NULL_in_literal_constraint2734 );
                        if ( this.failed ) {
                            return text;
                        }
                        if ( this.backtracking == 0 ) {
                            text = null;
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
        return text;
    }

    // $ANTLR end literal_constraint

    // $ANTLR start enum_constraint
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1012:1: enum_constraint returns [String text] : id= ID ( ( '.' identifier )=> '.' ident= identifier )+ ;
    public String enum_constraint() throws RecognitionException {
        String text = null;

        Token id = null;
        Token ident = null;

        text = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1017:3: (id= ID ( ( '.' identifier )=> '.' ident= identifier )+ )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1017:3: id= ID ( ( '.' identifier )=> '.' ident= identifier )+
            {
                id = (Token) this.input.LT( 1 );
                match( this.input,
                       ID,
                       FOLLOW_ID_in_enum_constraint2769 );
                if ( this.failed ) {
                    return text;
                }
                if ( this.backtracking == 0 ) {
                    text = id.getText();
                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1017:32: ( ( '.' identifier )=> '.' ident= identifier )+
                int cnt39 = 0;
                loop39 : do {
                    int alt39 = 2;
                    final int LA39_0 = this.input.LA( 1 );
                    if ( (LA39_0 == 64) ) {
                        alt39 = 1;
                    }

                    switch ( alt39 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1017:34: ( '.' identifier )=> '.' ident= identifier
                        {
                            match( this.input,
                                   64,
                                   FOLLOW_64_in_enum_constraint2775 );
                            if ( this.failed ) {
                                return text;
                            }
                            pushFollow( FOLLOW_identifier_in_enum_constraint2779 );
                            ident = identifier();
                            this._fsp--;
                            if ( this.failed ) {
                                return text;
                            }
                            if ( this.backtracking == 0 ) {
                                text += "." + ident.getText();
                            }

                        }
                            break;

                        default :
                            if ( cnt39 >= 1 ) {
                                break loop39;
                            }
                            if ( this.backtracking > 0 ) {
                                this.failed = true;
                                return text;
                            }
                            final EarlyExitException eee = new EarlyExitException( 39,
                                                                             this.input );
                            throw eee;
                    }
                    cnt39++;
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return text;
    }

    // $ANTLR end enum_constraint

    // $ANTLR start predicate
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1021:1: predicate[ColumnDescr column] : text= paren_chunk[d] ;
    public void predicate(final ColumnDescr column) throws RecognitionException {
        String text = null;

        PredicateDescr d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1026:3: (text= paren_chunk[d] )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1026:3: text= paren_chunk[d]
            {
                if ( this.backtracking == 0 ) {

                    d = new PredicateDescr();

                }
                pushFollow( FOLLOW_paren_chunk_in_predicate2821 );
                text = paren_chunk( d );
                this._fsp--;
                if ( this.failed ) {
                    return;
                }
                if ( this.backtracking == 0 ) {

                    if ( text != null ) {
                        final String body = text.substring( 1,
                                                      text.length() - 1 );
                        d.setContent( body );
                        column.addDescr( d );
                    }

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

    // $ANTLR end predicate

    // $ANTLR start paren_chunk
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1039:1: paren_chunk[BaseDescr descr] returns [String text] : loc= LEFT_PAREN ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk[null] )=>chunk= paren_chunk[null] )* loc= RIGHT_PAREN ;
    public String paren_chunk(final BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc = null;
        String chunk = null;

        StringBuffer buf = null;
        Integer channel = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1045:10: (loc= LEFT_PAREN ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk[null] )=>chunk= paren_chunk[null] )* loc= RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1045:10: loc= LEFT_PAREN ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk[null] )=>chunk= paren_chunk[null] )* loc= RIGHT_PAREN
            {
                if ( this.backtracking == 0 ) {

                    channel = ((SwitchingCommonTokenStream) this.input).getTokenTypeChannel( WS );
                    ((SwitchingCommonTokenStream) this.input).setTokenTypeChannel( WS,
                                                                              Token.DEFAULT_CHANNEL );
                    buf = new StringBuffer();

                }
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       LEFT_PAREN,
                       FOLLOW_LEFT_PAREN_in_paren_chunk2870 );
                if ( this.failed ) {
                    return text;
                }
                if ( this.backtracking == 0 ) {

                    buf.append( loc.getText() );

                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1055:3: ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk[null] )=>chunk= paren_chunk[null] )*
                loop40 : do {
                    int alt40 = 3;
                    final int LA40_0 = this.input.LA( 1 );
                    if ( ((LA40_0 >= PACKAGE && LA40_0 <= OR) || (LA40_0 >= CONTAINS && LA40_0 <= 78)) ) {
                        alt40 = 1;
                    } else if ( (LA40_0 == LEFT_PAREN) ) {
                        alt40 = 2;
                    }

                    switch ( alt40 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1056:4: (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN)
                        {
                            if ( (this.input.LA( 1 ) >= PACKAGE && this.input.LA( 1 ) <= OR) || (this.input.LA( 1 ) >= CONTAINS && this.input.LA( 1 ) <= 78) ) {
                                this.input.consume();
                                this.errorRecovery = false;
                                this.failed = false;
                            } else {
                                if ( this.backtracking > 0 ) {
                                    this.failed = true;
                                    return text;
                                }
                                final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                         this.input );
                                recoverFromMismatchedSet( this.input,
                                                          mse,
                                                          FOLLOW_set_in_paren_chunk2886 );
                                throw mse;
                            }

                            if ( this.backtracking == 0 ) {

                                buf.append( this.input.LT( -1 ).getText() );

                            }

                        }
                            break;
                        case 2 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1061:4: ( paren_chunk[null] )=>chunk= paren_chunk[null]
                        {
                            pushFollow( FOLLOW_paren_chunk_in_paren_chunk2910 );
                            chunk = paren_chunk( null );
                            this._fsp--;
                            if ( this.failed ) {
                                return text;
                            }
                            if ( this.backtracking == 0 ) {

                                buf.append( chunk );

                            }

                        }
                            break;

                        default :
                            break loop40;
                    }
                } while ( true );

                if ( this.backtracking == 0 ) {

                    if ( channel != null ) {
                        ((SwitchingCommonTokenStream) this.input).setTokenTypeChannel( WS,
                                                                                  channel.intValue() );
                    } else {
                        ((SwitchingCommonTokenStream) this.input).setTokenTypeChannel( WS,
                                                                                  Token.HIDDEN_CHANNEL );
                    }

                }
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       RIGHT_PAREN,
                       FOLLOW_RIGHT_PAREN_in_paren_chunk2947 );
                if ( this.failed ) {
                    return text;
                }
                if ( this.backtracking == 0 ) {

                    buf.append( loc.getText() );
                    text = buf.toString();
                    if ( descr != null ) {
                        descr.setEndCharacter( ((CommonToken) loc).getStopIndex() );
                    }

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return text;
    }

    // $ANTLR end paren_chunk

    // $ANTLR start curly_chunk
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1083:1: curly_chunk[BaseDescr descr] returns [String text] : loc= LEFT_CURLY ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk[descr] )=>chunk= curly_chunk[descr] )* loc= RIGHT_CURLY ;
    public String curly_chunk(final BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc = null;
        String chunk = null;

        StringBuffer buf = null;
        Integer channel = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1089:3: (loc= LEFT_CURLY ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk[descr] )=>chunk= curly_chunk[descr] )* loc= RIGHT_CURLY )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1089:3: loc= LEFT_CURLY ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk[descr] )=>chunk= curly_chunk[descr] )* loc= RIGHT_CURLY
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       LEFT_CURLY,
                       FOLLOW_LEFT_CURLY_in_curly_chunk2998 );
                if ( this.failed ) {
                    return text;
                }
                if ( this.backtracking == 0 ) {

                    channel = ((SwitchingCommonTokenStream) this.input).getTokenTypeChannel( WS );
                    ((SwitchingCommonTokenStream) this.input).setTokenTypeChannel( WS,
                                                                              Token.DEFAULT_CHANNEL );
                    buf = new StringBuffer();

                    buf.append( loc.getText() );

                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1097:3: ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk[descr] )=>chunk= curly_chunk[descr] )*
                loop41 : do {
                    int alt41 = 3;
                    final int LA41_0 = this.input.LA( 1 );
                    if ( ((LA41_0 >= PACKAGE && LA41_0 <= NULL) || (LA41_0 >= LEFT_SQUARE && LA41_0 <= 78)) ) {
                        alt41 = 1;
                    } else if ( (LA41_0 == LEFT_CURLY) ) {
                        alt41 = 2;
                    }

                    switch ( alt41 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1098:4: (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY)
                        {
                            if ( (this.input.LA( 1 ) >= PACKAGE && this.input.LA( 1 ) <= NULL) || (this.input.LA( 1 ) >= LEFT_SQUARE && this.input.LA( 1 ) <= 78) ) {
                                this.input.consume();
                                this.errorRecovery = false;
                                this.failed = false;
                            } else {
                                if ( this.backtracking > 0 ) {
                                    this.failed = true;
                                    return text;
                                }
                                final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                         this.input );
                                recoverFromMismatchedSet( this.input,
                                                          mse,
                                                          FOLLOW_set_in_curly_chunk3014 );
                                throw mse;
                            }

                            if ( this.backtracking == 0 ) {

                                buf.append( this.input.LT( -1 ).getText() );

                            }

                        }
                            break;
                        case 2 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1103:4: ( curly_chunk[descr] )=>chunk= curly_chunk[descr]
                        {
                            pushFollow( FOLLOW_curly_chunk_in_curly_chunk3038 );
                            chunk = curly_chunk( descr );
                            this._fsp--;
                            if ( this.failed ) {
                                return text;
                            }
                            if ( this.backtracking == 0 ) {

                                buf.append( chunk );

                            }

                        }
                            break;

                        default :
                            break loop41;
                    }
                } while ( true );

                if ( this.backtracking == 0 ) {

                    if ( channel != null ) {
                        ((SwitchingCommonTokenStream) this.input).setTokenTypeChannel( WS,
                                                                                  channel.intValue() );
                    } else {
                        ((SwitchingCommonTokenStream) this.input).setTokenTypeChannel( WS,
                                                                                  Token.HIDDEN_CHANNEL );
                    }

                }
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       RIGHT_CURLY,
                       FOLLOW_RIGHT_CURLY_in_curly_chunk3075 );
                if ( this.failed ) {
                    return text;
                }
                if ( this.backtracking == 0 ) {

                    buf.append( loc.getText() );
                    text = buf.toString();
                    if ( descr != null ) {
                        descr.setEndCharacter( ((CommonToken) loc).getStopIndex() );
                    }

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return text;
    }

    // $ANTLR end curly_chunk

    // $ANTLR start square_chunk
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1125:1: square_chunk[BaseDescr descr] returns [String text] : loc= LEFT_SQUARE ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk[null] )=>chunk= square_chunk[null] )* loc= RIGHT_SQUARE ;
    public String square_chunk(final BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc = null;
        String chunk = null;

        StringBuffer buf = null;
        Integer channel = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1131:10: (loc= LEFT_SQUARE ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk[null] )=>chunk= square_chunk[null] )* loc= RIGHT_SQUARE )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1131:10: loc= LEFT_SQUARE ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk[null] )=>chunk= square_chunk[null] )* loc= RIGHT_SQUARE
            {
                if ( this.backtracking == 0 ) {

                    channel = ((SwitchingCommonTokenStream) this.input).getTokenTypeChannel( WS );
                    ((SwitchingCommonTokenStream) this.input).setTokenTypeChannel( WS,
                                                                              Token.DEFAULT_CHANNEL );
                    buf = new StringBuffer();

                }
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       LEFT_SQUARE,
                       FOLLOW_LEFT_SQUARE_in_square_chunk3138 );
                if ( this.failed ) {
                    return text;
                }
                if ( this.backtracking == 0 ) {

                    buf.append( loc.getText() );

                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1141:3: ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk[null] )=>chunk= square_chunk[null] )*
                loop42 : do {
                    int alt42 = 3;
                    final int LA42_0 = this.input.LA( 1 );
                    if ( ((LA42_0 >= PACKAGE && LA42_0 <= RIGHT_CURLY) || (LA42_0 >= AND && LA42_0 <= 78)) ) {
                        alt42 = 1;
                    } else if ( (LA42_0 == LEFT_SQUARE) ) {
                        alt42 = 2;
                    }

                    switch ( alt42 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1142:4: (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE)
                        {
                            if ( (this.input.LA( 1 ) >= PACKAGE && this.input.LA( 1 ) <= RIGHT_CURLY) || (this.input.LA( 1 ) >= AND && this.input.LA( 1 ) <= 78) ) {
                                this.input.consume();
                                this.errorRecovery = false;
                                this.failed = false;
                            } else {
                                if ( this.backtracking > 0 ) {
                                    this.failed = true;
                                    return text;
                                }
                                final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                         this.input );
                                recoverFromMismatchedSet( this.input,
                                                          mse,
                                                          FOLLOW_set_in_square_chunk3154 );
                                throw mse;
                            }

                            if ( this.backtracking == 0 ) {

                                buf.append( this.input.LT( -1 ).getText() );

                            }

                        }
                            break;
                        case 2 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1147:4: ( square_chunk[null] )=>chunk= square_chunk[null]
                        {
                            pushFollow( FOLLOW_square_chunk_in_square_chunk3178 );
                            chunk = square_chunk( null );
                            this._fsp--;
                            if ( this.failed ) {
                                return text;
                            }
                            if ( this.backtracking == 0 ) {

                                buf.append( chunk );

                            }

                        }
                            break;

                        default :
                            break loop42;
                    }
                } while ( true );

                if ( this.backtracking == 0 ) {

                    if ( channel != null ) {
                        ((SwitchingCommonTokenStream) this.input).setTokenTypeChannel( WS,
                                                                                  channel.intValue() );
                    } else {
                        ((SwitchingCommonTokenStream) this.input).setTokenTypeChannel( WS,
                                                                                  Token.HIDDEN_CHANNEL );
                    }

                }
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       RIGHT_SQUARE,
                       FOLLOW_RIGHT_SQUARE_in_square_chunk3215 );
                if ( this.failed ) {
                    return text;
                }
                if ( this.backtracking == 0 ) {

                    buf.append( loc.getText() );
                    text = buf.toString();
                    if ( descr != null ) {
                        descr.setEndCharacter( ((CommonToken) loc).getStopIndex() );
                    }

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return text;
    }

    // $ANTLR end square_chunk

    // $ANTLR start retval_constraint
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1169:1: retval_constraint returns [String text] : c= paren_chunk[null] ;
    public String retval_constraint() throws RecognitionException {
        String text = null;

        String c = null;

        text = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1174:3: (c= paren_chunk[null] )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1174:3: c= paren_chunk[null]
            {
                pushFollow( FOLLOW_paren_chunk_in_retval_constraint3260 );
                c = paren_chunk( null );
                this._fsp--;
                if ( this.failed ) {
                    return text;
                }
                if ( this.backtracking == 0 ) {
                    text = c.substring( 1,
                                        c.length() - 1 );
                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return text;
    }

    // $ANTLR end retval_constraint

    // $ANTLR start lhs_or
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1177:1: lhs_or returns [BaseDescr d] : left= lhs_and ( ( (OR|'||') lhs_and )=> (OR|'||')right= lhs_and )* ;
    public BaseDescr lhs_or() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;

        d = null;
        OrDescr or = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1183:3: (left= lhs_and ( ( (OR|'||') lhs_and )=> (OR|'||')right= lhs_and )* )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1183:3: left= lhs_and ( ( (OR|'||') lhs_and )=> (OR|'||')right= lhs_and )*
            {
                pushFollow( FOLLOW_lhs_and_in_lhs_or3288 );
                left = lhs_and();
                this._fsp--;
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {
                    d = left;
                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1184:3: ( ( (OR|'||') lhs_and )=> (OR|'||')right= lhs_and )*
                loop43 : do {
                    int alt43 = 2;
                    final int LA43_0 = this.input.LA( 1 );
                    if ( (LA43_0 == OR || LA43_0 == 68) ) {
                        alt43 = 1;
                    }

                    switch ( alt43 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1184:5: ( (OR|'||') lhs_and )=> (OR|'||')right= lhs_and
                        {
                            if ( this.input.LA( 1 ) == OR || this.input.LA( 1 ) == 68 ) {
                                this.input.consume();
                                this.errorRecovery = false;
                                this.failed = false;
                            } else {
                                if ( this.backtracking > 0 ) {
                                    this.failed = true;
                                    return d;
                                }
                                final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                         this.input );
                                recoverFromMismatchedSet( this.input,
                                                          mse,
                                                          FOLLOW_set_in_lhs_or3297 );
                                throw mse;
                            }

                            pushFollow( FOLLOW_lhs_and_in_lhs_or3307 );
                            right = lhs_and();
                            this._fsp--;
                            if ( this.failed ) {
                                return d;
                            }
                            if ( this.backtracking == 0 ) {

                                if ( or == null ) {
                                    or = new OrDescr();
                                    or.addDescr( left );
                                    d = or;
                                }

                                or.addDescr( right );

                            }

                        }
                            break;

                        default :
                            break loop43;
                    }
                } while ( true );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end lhs_or

    // $ANTLR start lhs_and
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1198:1: lhs_and returns [BaseDescr d] : left= lhs_unary ( ( (AND|'&&') lhs_unary )=> (AND|'&&')right= lhs_unary )* ;
    public BaseDescr lhs_and() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;

        d = null;
        AndDescr and = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1204:3: (left= lhs_unary ( ( (AND|'&&') lhs_unary )=> (AND|'&&')right= lhs_unary )* )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1204:3: left= lhs_unary ( ( (AND|'&&') lhs_unary )=> (AND|'&&')right= lhs_unary )*
            {
                pushFollow( FOLLOW_lhs_unary_in_lhs_and3343 );
                left = lhs_unary();
                this._fsp--;
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {
                    d = left;
                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1205:3: ( ( (AND|'&&') lhs_unary )=> (AND|'&&')right= lhs_unary )*
                loop44 : do {
                    int alt44 = 2;
                    final int LA44_0 = this.input.LA( 1 );
                    if ( (LA44_0 == AND || LA44_0 == 78) ) {
                        alt44 = 1;
                    }

                    switch ( alt44 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1205:5: ( (AND|'&&') lhs_unary )=> (AND|'&&')right= lhs_unary
                        {
                            if ( this.input.LA( 1 ) == AND || this.input.LA( 1 ) == 78 ) {
                                this.input.consume();
                                this.errorRecovery = false;
                                this.failed = false;
                            } else {
                                if ( this.backtracking > 0 ) {
                                    this.failed = true;
                                    return d;
                                }
                                final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                         this.input );
                                recoverFromMismatchedSet( this.input,
                                                          mse,
                                                          FOLLOW_set_in_lhs_and3352 );
                                throw mse;
                            }

                            pushFollow( FOLLOW_lhs_unary_in_lhs_and3362 );
                            right = lhs_unary();
                            this._fsp--;
                            if ( this.failed ) {
                                return d;
                            }
                            if ( this.backtracking == 0 ) {

                                if ( and == null ) {
                                    and = new AndDescr();
                                    and.addDescr( left );
                                    d = and;
                                }

                                and.addDescr( right );

                            }

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
        return d;
    }

    // $ANTLR end lhs_and

    // $ANTLR start lhs_unary
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1219:1: lhs_unary returns [BaseDescr d] : ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )? )=>u= lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon ;
    public BaseDescr lhs_unary() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr u = null;

        AccumulateDescr ac = null;

        CollectDescr cs = null;

        FromDescr fm = null;

        d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1223:4: ( ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )? )=>u= lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1223:4: ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )? )=>u= lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon
            {
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1223:4: ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )? )=>u= lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs_or ')' )
                int alt47 = 6;
                switch ( this.input.LA( 1 ) ) {
                    case EXISTS :
                        alt47 = 1;
                        break;
                    case NOT :
                        alt47 = 2;
                        break;
                    case EVAL :
                        alt47 = 3;
                        break;
                    case ID :
                        alt47 = 4;
                        break;
                    case FORALL :
                        alt47 = 5;
                        break;
                    case LEFT_PAREN :
                        alt47 = 6;
                        break;
                    default :
                        if ( this.backtracking > 0 ) {
                            this.failed = true;
                            return d;
                        }
                        final NoViableAltException nvae = new NoViableAltException( "1223:4: ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )? )=>u= lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs_or ')' )",
                                                                              47,
                                                                              0,
                                                                              this.input );

                        throw nvae;
                }

                switch ( alt47 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1223:6: ( lhs_exist )=>u= lhs_exist
                    {
                        pushFollow( FOLLOW_lhs_exist_in_lhs_unary3399 );
                        u = lhs_exist();
                        this._fsp--;
                        if ( this.failed ) {
                            return d;
                        }

                    }
                        break;
                    case 2 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1224:5: ( lhs_not )=>u= lhs_not
                    {
                        pushFollow( FOLLOW_lhs_not_in_lhs_unary3407 );
                        u = lhs_not();
                        this._fsp--;
                        if ( this.failed ) {
                            return d;
                        }

                    }
                        break;
                    case 3 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1225:5: ( lhs_eval )=>u= lhs_eval
                    {
                        pushFollow( FOLLOW_lhs_eval_in_lhs_unary3415 );
                        u = lhs_eval();
                        this._fsp--;
                        if ( this.failed ) {
                            return d;
                        }

                    }
                        break;
                    case 4 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1226:5: ( lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )? )=>u= lhs_column ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) ) )?
                    {
                        pushFollow( FOLLOW_lhs_column_in_lhs_unary3423 );
                        u = lhs_column();
                        this._fsp--;
                        if ( this.failed ) {
                            return d;
                        }
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1226:18: ( ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) ) )?
                        int alt46 = 2;
                        final int LA46_0 = this.input.LA( 1 );
                        if ( (LA46_0 == FROM) ) {
                            alt46 = 1;
                        }
                        switch ( alt46 ) {
                            case 1 :
                                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1227:13: ( FROM ( ( ACCUMULATE )=> ( accumulate_statement ) | ( COLLECT )=> ( collect_statement ) | (~ (ACCUMULATE|COLLECT))=> ( from_statement ) ) )=> FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) )
                            {
                                match( this.input,
                                       FROM,
                                       FOLLOW_FROM_in_lhs_unary3439 );
                                if ( this.failed ) {
                                    return d;
                                }
                                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1227:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) )
                                int alt45 = 3;
                                switch ( this.input.LA( 1 ) ) {
                                    case ACCUMULATE :
                                        if ( (synpred72()) ) {
                                            alt45 = 1;
                                        } else if ( (synpred74()) ) {
                                            alt45 = 3;
                                        } else {
                                            if ( this.backtracking > 0 ) {
                                                this.failed = true;
                                                return d;
                                            }
                                            final NoViableAltException nvae = new NoViableAltException( "1227:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) )",
                                                                                                  45,
                                                                                                  1,
                                                                                                  this.input );

                                            throw nvae;
                                        }
                                        break;
                                    case COLLECT :
                                        if ( (synpred73()) ) {
                                            alt45 = 2;
                                        } else if ( (synpred74()) ) {
                                            alt45 = 3;
                                        } else {
                                            if ( this.backtracking > 0 ) {
                                                this.failed = true;
                                                return d;
                                            }
                                            final NoViableAltException nvae = new NoViableAltException( "1227:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) )",
                                                                                                  45,
                                                                                                  2,
                                                                                                  this.input );

                                            throw nvae;
                                        }
                                        break;
                                    case PACKAGE :
                                    case IMPORT :
                                    case FUNCTION :
                                    case GLOBAL :
                                    case QUERY :
                                    case END :
                                    case TEMPLATE :
                                    case RULE :
                                    case WHEN :
                                    case ATTRIBUTES :
                                    case ENABLED :
                                    case SALIENCE :
                                    case DURATION :
                                    case INIT :
                                    case ACTION :
                                    case RESULT :
                                    case ID :
                                    case OR :
                                    case CONTAINS :
                                    case MATCHES :
                                    case EXCLUDES :
                                    case NULL :
                                    case AND :
                                    case FROM :
                                    case EXISTS :
                                    case NOT :
                                    case EVAL :
                                    case FORALL :
                                    case THEN :
                                        alt45 = 3;
                                        break;
                                    default :
                                        if ( this.backtracking > 0 ) {
                                            this.failed = true;
                                            return d;
                                        }
                                        final NoViableAltException nvae = new NoViableAltException( "1227:18: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ (ACCUMULATE|COLLECT))=> (fm= from_statement ) )",
                                                                                              45,
                                                                                              0,
                                                                                              this.input );

                                        throw nvae;
                                }

                                switch ( alt45 ) {
                                    case 1 :
                                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1228:14: ( ACCUMULATE )=> (ac= accumulate_statement )
                                    {
                                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1228:32: (ac= accumulate_statement )
                                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1228:33: ac= accumulate_statement
                                        {
                                            pushFollow( FOLLOW_accumulate_statement_in_lhs_unary3467 );
                                            ac = accumulate_statement();
                                            this._fsp--;
                                            if ( this.failed ) {
                                                return d;
                                            }
                                            if ( this.backtracking == 0 ) {
                                                ac.setResultColumn( (ColumnDescr) u );
                                                u = ac;
                                            }

                                        }

                                    }
                                        break;
                                    case 2 :
                                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1229:14: ( COLLECT )=> (cs= collect_statement )
                                    {
                                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1229:29: (cs= collect_statement )
                                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1229:30: cs= collect_statement
                                        {
                                            pushFollow( FOLLOW_collect_statement_in_lhs_unary3496 );
                                            cs = collect_statement();
                                            this._fsp--;
                                            if ( this.failed ) {
                                                return d;
                                            }
                                            if ( this.backtracking == 0 ) {
                                                cs.setResultColumn( (ColumnDescr) u );
                                                u = cs;
                                            }

                                        }

                                    }
                                        break;
                                    case 3 :
                                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1230:14: (~ (ACCUMULATE|COLLECT))=> (fm= from_statement )
                                    {
                                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1230:43: (fm= from_statement )
                                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1230:44: fm= from_statement
                                        {
                                            pushFollow( FOLLOW_from_statement_in_lhs_unary3531 );
                                            fm = from_statement();
                                            this._fsp--;
                                            if ( this.failed ) {
                                                return d;
                                            }
                                            if ( this.backtracking == 0 ) {
                                                fm.setColumn( (ColumnDescr) u );
                                                u = fm;
                                            }

                                        }

                                    }
                                        break;

                                }

                            }
                                break;

                        }

                    }
                        break;
                    case 5 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1233:5: ( lhs_forall )=>u= lhs_forall
                    {
                        pushFollow( FOLLOW_lhs_forall_in_lhs_unary3570 );
                        u = lhs_forall();
                        this._fsp--;
                        if ( this.failed ) {
                            return d;
                        }

                    }
                        break;
                    case 6 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1234:5: '(' u= lhs_or ')'
                    {
                        match( this.input,
                               LEFT_PAREN,
                               FOLLOW_LEFT_PAREN_in_lhs_unary3578 );
                        if ( this.failed ) {
                            return d;
                        }
                        pushFollow( FOLLOW_lhs_or_in_lhs_unary3582 );
                        u = lhs_or();
                        this._fsp--;
                        if ( this.failed ) {
                            return d;
                        }
                        match( this.input,
                               RIGHT_PAREN,
                               FOLLOW_RIGHT_PAREN_in_lhs_unary3584 );
                        if ( this.failed ) {
                            return d;
                        }

                    }
                        break;

                }

                if ( this.backtracking == 0 ) {
                    d = u;
                }
                pushFollow( FOLLOW_opt_semicolon_in_lhs_unary3594 );
                opt_semicolon();
                this._fsp--;
                if ( this.failed ) {
                    return d;
                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end lhs_unary

    // $ANTLR start lhs_exist
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1239:1: lhs_exist returns [BaseDescr d] : loc= EXISTS ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column ) ;
    public BaseDescr lhs_exist() throws RecognitionException {
        BaseDescr d = null;

        Token loc = null;
        Token end = null;
        BaseDescr column = null;

        d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1243:4: (loc= EXISTS ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1243:4: loc= EXISTS ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column )
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       EXISTS,
                       FOLLOW_EXISTS_in_lhs_exist3618 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    d = new ExistsDescr();
                    d.setLocation( offset( loc.getLine() ),
                                   loc.getCharPositionInLine() );
                    d.setStartCharacter( ((CommonToken) loc).getStartIndex() );

                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1249:10: ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column )
                int alt48 = 2;
                final int LA48_0 = this.input.LA( 1 );
                if ( (LA48_0 == LEFT_PAREN) ) {
                    alt48 = 1;
                } else if ( (LA48_0 == ID) ) {
                    alt48 = 2;
                } else {
                    if ( this.backtracking > 0 ) {
                        this.failed = true;
                        return d;
                    }
                    final NoViableAltException nvae = new NoViableAltException( "1249:10: ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column )",
                                                                          48,
                                                                          0,
                                                                          this.input );

                    throw nvae;
                }
                switch ( alt48 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1249:12: ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' )
                    {
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1249:12: ( '(' column= lhs_or end= ')' )
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1249:14: '(' column= lhs_or end= ')'
                        {
                            match( this.input,
                                   LEFT_PAREN,
                                   FOLLOW_LEFT_PAREN_in_lhs_exist3638 );
                            if ( this.failed ) {
                                return d;
                            }
                            pushFollow( FOLLOW_lhs_or_in_lhs_exist3642 );
                            column = lhs_or();
                            this._fsp--;
                            if ( this.failed ) {
                                return d;
                            }
                            if ( this.backtracking == 0 ) {
                                if ( column != null ) {
                                    ((ExistsDescr) d).addDescr( column );
                                }
                            }
                            end = (Token) this.input.LT( 1 );
                            match( this.input,
                                   RIGHT_PAREN,
                                   FOLLOW_RIGHT_PAREN_in_lhs_exist3674 );
                            if ( this.failed ) {
                                return d;
                            }
                            if ( this.backtracking == 0 ) {
                                if ( end != null ) {
                                    d.setEndCharacter( ((CommonToken) end).getStopIndex() );
                                }
                            }

                        }

                    }
                        break;
                    case 2 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1254:12: column= lhs_column
                    {
                        pushFollow( FOLLOW_lhs_column_in_lhs_exist3724 );
                        column = lhs_column();
                        this._fsp--;
                        if ( this.failed ) {
                            return d;
                        }
                        if ( this.backtracking == 0 ) {

                            if ( column != null ) {
                                ((ExistsDescr) d).addDescr( column );
                                d.setEndCharacter( column.getEndCharacter() );
                            }

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
        return d;
    }

    // $ANTLR end lhs_exist

    // $ANTLR start lhs_not
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1264:1: lhs_not returns [NotDescr d] : loc= NOT ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column ) ;
    public NotDescr lhs_not() throws RecognitionException {
        NotDescr d = null;

        Token loc = null;
        Token end = null;
        BaseDescr column = null;

        d = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1268:4: (loc= NOT ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1268:4: loc= NOT ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column )
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       NOT,
                       FOLLOW_NOT_in_lhs_not3778 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    d = new NotDescr();
                    d.setLocation( offset( loc.getLine() ),
                                   loc.getCharPositionInLine() );
                    d.setStartCharacter( ((CommonToken) loc).getStartIndex() );

                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1274:3: ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column )
                int alt49 = 2;
                final int LA49_0 = this.input.LA( 1 );
                if ( (LA49_0 == LEFT_PAREN) ) {
                    alt49 = 1;
                } else if ( (LA49_0 == ID) ) {
                    alt49 = 2;
                } else {
                    if ( this.backtracking > 0 ) {
                        this.failed = true;
                        return d;
                    }
                    final NoViableAltException nvae = new NoViableAltException( "1274:3: ( ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' ) | column= lhs_column )",
                                                                          49,
                                                                          0,
                                                                          this.input );

                    throw nvae;
                }
                switch ( alt49 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1274:5: ( ( '(' lhs_or ')' ) )=> ( '(' column= lhs_or end= ')' )
                    {
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1274:5: ( '(' column= lhs_or end= ')' )
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1274:7: '(' column= lhs_or end= ')'
                        {
                            match( this.input,
                                   LEFT_PAREN,
                                   FOLLOW_LEFT_PAREN_in_lhs_not3791 );
                            if ( this.failed ) {
                                return d;
                            }
                            pushFollow( FOLLOW_lhs_or_in_lhs_not3795 );
                            column = lhs_or();
                            this._fsp--;
                            if ( this.failed ) {
                                return d;
                            }
                            if ( this.backtracking == 0 ) {
                                if ( column != null ) {
                                    d.addDescr( column );
                                }
                            }
                            end = (Token) this.input.LT( 1 );
                            match( this.input,
                                   RIGHT_PAREN,
                                   FOLLOW_RIGHT_PAREN_in_lhs_not3828 );
                            if ( this.failed ) {
                                return d;
                            }
                            if ( this.backtracking == 0 ) {
                                if ( end != null ) {
                                    d.setEndCharacter( ((CommonToken) end).getStopIndex() );
                                }
                            }

                        }

                    }
                        break;
                    case 2 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1280:3: column= lhs_column
                    {
                        pushFollow( FOLLOW_lhs_column_in_lhs_not3865 );
                        column = lhs_column();
                        this._fsp--;
                        if ( this.failed ) {
                            return d;
                        }
                        if ( this.backtracking == 0 ) {

                            if ( column != null ) {
                                d.addDescr( column );
                                d.setEndCharacter( column.getEndCharacter() );
                            }

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
        return d;
    }

    // $ANTLR end lhs_not

    // $ANTLR start lhs_eval
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1290:1: lhs_eval returns [BaseDescr d] : loc= EVAL c= paren_chunk[d] ;
    public BaseDescr lhs_eval() throws RecognitionException {
        BaseDescr d = null;

        Token loc = null;
        String c = null;

        d = new EvalDescr();

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1295:3: (loc= EVAL c= paren_chunk[d] )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1295:3: loc= EVAL c= paren_chunk[d]
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       EVAL,
                       FOLLOW_EVAL_in_lhs_eval3913 );
                if ( this.failed ) {
                    return d;
                }
                pushFollow( FOLLOW_paren_chunk_in_lhs_eval3917 );
                c = paren_chunk( d );
                this._fsp--;
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    if ( loc != null ) {
                        d.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                    }
                    if ( c != null ) {
                        final String body = c.substring( 1,
                                                   c.length() - 1 );
                        checkTrailingSemicolon( body,
                                                offset( loc.getLine() ) );
                        ((EvalDescr) d).setContent( body );
                    }

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end lhs_eval

    // $ANTLR start lhs_forall
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1306:1: lhs_forall returns [ForallDescr d] : loc= FORALL '(' base= lhs_column ( ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column )+ end= ')' ;
    public ForallDescr lhs_forall() throws RecognitionException {
        ForallDescr d = null;

        Token loc = null;
        Token end = null;
        BaseDescr base = null;

        BaseDescr column = null;

        d = this.factory.createForall();

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1310:4: (loc= FORALL '(' base= lhs_column ( ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column )+ end= ')' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1310:4: loc= FORALL '(' base= lhs_column ( ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column )+ end= ')'
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       FORALL,
                       FOLLOW_FORALL_in_lhs_forall3946 );
                if ( this.failed ) {
                    return d;
                }
                match( this.input,
                       LEFT_PAREN,
                       FOLLOW_LEFT_PAREN_in_lhs_forall3948 );
                if ( this.failed ) {
                    return d;
                }
                pushFollow( FOLLOW_lhs_column_in_lhs_forall3952 );
                base = lhs_column();
                this._fsp--;
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    if ( loc != null ) {
                        d.setStartCharacter( ((CommonToken) loc).getStartIndex() );
                    }
                    // adding the base column
                    d.addDescr( base );
                    d.setLocation( offset( loc.getLine() ),
                                   loc.getCharPositionInLine() );

                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1317:3: ( ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column )+
                int cnt51 = 0;
                loop51 : do {
                    int alt51 = 2;
                    final int LA51_0 = this.input.LA( 1 );
                    if ( (LA51_0 == ID || LA51_0 == 66) ) {
                        alt51 = 1;
                    }

                    switch ( alt51 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1317:5: ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column
                        {
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1317:5: ( ( ',' )=> ',' )?
                            int alt50 = 2;
                            final int LA50_0 = this.input.LA( 1 );
                            if ( (LA50_0 == 66) ) {
                                alt50 = 1;
                            }
                            switch ( alt50 ) {
                                case 1 :
                                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1317:6: ( ',' )=> ','
                                {
                                    match( this.input,
                                           66,
                                           FOLLOW_66_in_lhs_forall3966 );
                                    if ( this.failed ) {
                                        return d;
                                    }

                                }
                                    break;

                            }

                            pushFollow( FOLLOW_lhs_column_in_lhs_forall3972 );
                            column = lhs_column();
                            this._fsp--;
                            if ( this.failed ) {
                                return d;
                            }
                            if ( this.backtracking == 0 ) {

                                // adding additional columns
                                d.addDescr( column );

                            }

                        }
                            break;

                        default :
                            if ( cnt51 >= 1 ) {
                                break loop51;
                            }
                            if ( this.backtracking > 0 ) {
                                this.failed = true;
                                return d;
                            }
                            final EarlyExitException eee = new EarlyExitException( 51,
                                                                             this.input );
                            throw eee;
                    }
                    cnt51++;
                } while ( true );

                end = (Token) this.input.LT( 1 );
                match( this.input,
                       RIGHT_PAREN,
                       FOLLOW_RIGHT_PAREN_in_lhs_forall3987 );
                if ( this.failed ) {
                    return d;
                }
                if ( this.backtracking == 0 ) {

                    if ( end != null ) {
                        d.setEndCharacter( ((CommonToken) end).getStopIndex() );
                    }

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return d;
    }

    // $ANTLR end lhs_forall

    // $ANTLR start dotted_name
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1329:1: dotted_name[BaseDescr descr] returns [String name] : id= ID ( ( '.' identifier )=> '.' ident= identifier )* ( ( '[' ']' )=> '[' loc= ']' )* ;
    public String dotted_name(final BaseDescr descr) throws RecognitionException {
        String name = null;

        Token id = null;
        Token loc = null;
        Token ident = null;

        name = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1334:3: (id= ID ( ( '.' identifier )=> '.' ident= identifier )* ( ( '[' ']' )=> '[' loc= ']' )* )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1334:3: id= ID ( ( '.' identifier )=> '.' ident= identifier )* ( ( '[' ']' )=> '[' loc= ']' )*
            {
                id = (Token) this.input.LT( 1 );
                match( this.input,
                       ID,
                       FOLLOW_ID_in_dotted_name4018 );
                if ( this.failed ) {
                    return name;
                }
                if ( this.backtracking == 0 ) {

                    name = id.getText();
                    if ( descr != null ) {
                        descr.setStartCharacter( ((CommonToken) id).getStartIndex() );
                        descr.setEndCharacter( ((CommonToken) id).getStopIndex() );
                    }

                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1342:3: ( ( '.' identifier )=> '.' ident= identifier )*
                loop52 : do {
                    int alt52 = 2;
                    final int LA52_0 = this.input.LA( 1 );
                    if ( (LA52_0 == 64) ) {
                        alt52 = 1;
                    }

                    switch ( alt52 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1342:5: ( '.' identifier )=> '.' ident= identifier
                        {
                            match( this.input,
                                   64,
                                   FOLLOW_64_in_dotted_name4030 );
                            if ( this.failed ) {
                                return name;
                            }
                            pushFollow( FOLLOW_identifier_in_dotted_name4034 );
                            ident = identifier();
                            this._fsp--;
                            if ( this.failed ) {
                                return name;
                            }
                            if ( this.backtracking == 0 ) {

                                name = name + "." + ident.getText();
                                if ( descr != null ) {
                                    descr.setEndCharacter( ((CommonToken) ident).getStopIndex() );
                                }

                            }

                        }
                            break;

                        default :
                            break loop52;
                    }
                } while ( true );

                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1350:3: ( ( '[' ']' )=> '[' loc= ']' )*
                loop53 : do {
                    int alt53 = 2;
                    final int LA53_0 = this.input.LA( 1 );
                    if ( (LA53_0 == LEFT_SQUARE) ) {
                        alt53 = 1;
                    }

                    switch ( alt53 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1350:5: ( '[' ']' )=> '[' loc= ']'
                        {
                            match( this.input,
                                   LEFT_SQUARE,
                                   FOLLOW_LEFT_SQUARE_in_dotted_name4056 );
                            if ( this.failed ) {
                                return name;
                            }
                            loc = (Token) this.input.LT( 1 );
                            match( this.input,
                                   RIGHT_SQUARE,
                                   FOLLOW_RIGHT_SQUARE_in_dotted_name4060 );
                            if ( this.failed ) {
                                return name;
                            }
                            if ( this.backtracking == 0 ) {

                                name = name + "[]";
                                if ( descr != null ) {
                                    descr.setEndCharacter( ((CommonToken) loc).getStopIndex() );
                                }

                            }

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
        return name;
    }

    // $ANTLR end dotted_name

    // $ANTLR start argument
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1360:1: argument returns [String name] : id= identifier ( ( '[' ']' )=> '[' ']' )* ;
    public String argument() throws RecognitionException {
        String name = null;

        Token id = null;

        name = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1365:3: (id= identifier ( ( '[' ']' )=> '[' ']' )* )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1365:3: id= identifier ( ( '[' ']' )=> '[' ']' )*
            {
                pushFollow( FOLLOW_identifier_in_argument4099 );
                id = identifier();
                this._fsp--;
                if ( this.failed ) {
                    return name;
                }
                if ( this.backtracking == 0 ) {
                    name = id.getText();
                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1365:40: ( ( '[' ']' )=> '[' ']' )*
                loop54 : do {
                    int alt54 = 2;
                    final int LA54_0 = this.input.LA( 1 );
                    if ( (LA54_0 == LEFT_SQUARE) ) {
                        alt54 = 1;
                    }

                    switch ( alt54 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1365:42: ( '[' ']' )=> '[' ']'
                        {
                            match( this.input,
                                   LEFT_SQUARE,
                                   FOLLOW_LEFT_SQUARE_in_argument4105 );
                            if ( this.failed ) {
                                return name;
                            }
                            match( this.input,
                                   RIGHT_SQUARE,
                                   FOLLOW_RIGHT_SQUARE_in_argument4107 );
                            if ( this.failed ) {
                                return name;
                            }
                            if ( this.backtracking == 0 ) {
                                name = name + "[]";
                            }

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
        return name;
    }

    // $ANTLR end argument

    // $ANTLR start rhs_chunk
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1368:1: rhs_chunk[RuleDescr rule] : start= THEN ( (~ END )=>~ END )* loc= END ;
    public void rhs_chunk(final RuleDescr rule) throws RecognitionException {
        Token start = null;
        Token loc = null;

        StringBuffer buf = null;
        Integer channel = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1374:10: (start= THEN ( (~ END )=>~ END )* loc= END )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1374:10: start= THEN ( (~ END )=>~ END )* loc= END
            {
                if ( this.backtracking == 0 ) {

                    channel = ((SwitchingCommonTokenStream) this.input).getTokenTypeChannel( WS );
                    ((SwitchingCommonTokenStream) this.input).setTokenTypeChannel( WS,
                                                                              Token.DEFAULT_CHANNEL );
                    buf = new StringBuffer();

                }
                start = (Token) this.input.LT( 1 );
                match( this.input,
                       THEN,
                       FOLLOW_THEN_in_rhs_chunk4151 );
                if ( this.failed ) {
                    return;
                }
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1380:3: ( (~ END )=>~ END )*
                loop55 : do {
                    int alt55 = 2;
                    final int LA55_0 = this.input.LA( 1 );
                    if ( ((LA55_0 >= PACKAGE && LA55_0 <= QUERY) || (LA55_0 >= TEMPLATE && LA55_0 <= 78)) ) {
                        alt55 = 1;
                    }

                    switch ( alt55 ) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1381:6: (~ END )=>~ END
                        {
                            if ( (this.input.LA( 1 ) >= PACKAGE && this.input.LA( 1 ) <= QUERY) || (this.input.LA( 1 ) >= TEMPLATE && this.input.LA( 1 ) <= 78) ) {
                                this.input.consume();
                                this.errorRecovery = false;
                                this.failed = false;
                            } else {
                                if ( this.backtracking > 0 ) {
                                    this.failed = true;
                                    return;
                                }
                                final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                         this.input );
                                recoverFromMismatchedSet( this.input,
                                                          mse,
                                                          FOLLOW_set_in_rhs_chunk4163 );
                                throw mse;
                            }

                            if ( this.backtracking == 0 ) {

                                buf.append( this.input.LT( -1 ).getText() );

                            }

                        }
                            break;

                        default :
                            break loop55;
                    }
                } while ( true );

                if ( this.backtracking == 0 ) {

                    if ( channel != null ) {
                        ((SwitchingCommonTokenStream) this.input).setTokenTypeChannel( WS,
                                                                                  channel.intValue() );
                    } else {
                        ((SwitchingCommonTokenStream) this.input).setTokenTypeChannel( WS,
                                                                                  Token.HIDDEN_CHANNEL );
                    }

                }
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       END,
                       FOLLOW_END_in_rhs_chunk4200 );
                if ( this.failed ) {
                    return;
                }
                if ( this.backtracking == 0 ) {

                    // ignoring first line in the consequence
                    int index = 0;
                    while ( (index < buf.length()) && Character.isWhitespace( buf.charAt( index ) ) && (buf.charAt( index ) != 10) && (buf.charAt( index ) != 13) ) {
                        index++;
                    }
                    if ( (index < buf.length()) && (buf.charAt( index ) == '\r') ) {
                        index++;
                    }
                    if ( (index < buf.length()) && (buf.charAt( index ) == '\n') ) {
                        index++;
                    }

                    rule.setConsequence( buf.substring( index ) );
                    rule.setConsequenceLocation( offset( start.getLine() ),
                                                 start.getCharPositionInLine() );
                    rule.setEndCharacter( ((CommonToken) loc).getStopIndex() );

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

    // $ANTLR end rhs_chunk

    // $ANTLR start name
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1411:1: name returns [String s] : ( ( ID )=>tok= ID | str= STRING ) ;
    public String name() throws RecognitionException {
        String s = null;

        Token tok = null;
        Token str = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1413:2: ( ( ( ID )=>tok= ID | str= STRING ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1413:2: ( ( ID )=>tok= ID | str= STRING )
            {
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1413:2: ( ( ID )=>tok= ID | str= STRING )
                int alt56 = 2;
                final int LA56_0 = this.input.LA( 1 );
                if ( (LA56_0 == ID) ) {
                    alt56 = 1;
                } else if ( (LA56_0 == STRING) ) {
                    alt56 = 2;
                } else {
                    if ( this.backtracking > 0 ) {
                        this.failed = true;
                        return s;
                    }
                    final NoViableAltException nvae = new NoViableAltException( "1413:2: ( ( ID )=>tok= ID | str= STRING )",
                                                                          56,
                                                                          0,
                                                                          this.input );

                    throw nvae;
                }
                switch ( alt56 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1414:6: ( ID )=>tok= ID
                    {
                        tok = (Token) this.input.LT( 1 );
                        match( this.input,
                               ID,
                               FOLLOW_ID_in_name4244 );
                        if ( this.failed ) {
                            return s;
                        }
                        if ( this.backtracking == 0 ) {

                            s = tok.getText();

                        }

                    }
                        break;
                    case 2 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1419:6: str= STRING
                    {
                        str = (Token) this.input.LT( 1 );
                        match( this.input,
                               STRING,
                               FOLLOW_STRING_in_name4263 );
                        if ( this.failed ) {
                            return s;
                        }
                        if ( this.backtracking == 0 ) {

                            s = getString( str );

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
        return s;
    }

    // $ANTLR end name

    // $ANTLR start identifier
    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1426:1: identifier returns [Token tok] : ( ( ID )=>t= ID | ( PACKAGE )=>t= PACKAGE | ( FUNCTION )=>t= FUNCTION | ( GLOBAL )=>t= GLOBAL | ( IMPORT )=>t= IMPORT | ( RULE )=>t= RULE | ( QUERY )=>t= QUERY | ( TEMPLATE )=>t= TEMPLATE | ( ATTRIBUTES )=>t= ATTRIBUTES | ( ENABLED )=>t= ENABLED | ( SALIENCE )=>t= SALIENCE | ( DURATION )=>t= DURATION | ( FROM )=>t= FROM | ( ACCUMULATE )=>t= ACCUMULATE | ( INIT )=>t= INIT | ( ACTION )=>t= ACTION | ( RESULT )=>t= RESULT | ( COLLECT )=>t= COLLECT | ( OR )=>t= OR | ( AND )=>t= AND | ( CONTAINS )=>t= CONTAINS | ( EXCLUDES )=>t= EXCLUDES | ( MATCHES )=>t= MATCHES | ( NULL )=>t= NULL | ( EXISTS )=>t= EXISTS | ( NOT )=>t= NOT | ( EVAL )=>t= EVAL | ( FORALL )=>t= FORALL | ( WHEN )=>t= WHEN | ( THEN )=>t= THEN | t= END ) ;
    public Token identifier() throws RecognitionException {
        Token tok = null;

        Token t = null;

        try {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1428:2: ( ( ( ID )=>t= ID | ( PACKAGE )=>t= PACKAGE | ( FUNCTION )=>t= FUNCTION | ( GLOBAL )=>t= GLOBAL | ( IMPORT )=>t= IMPORT | ( RULE )=>t= RULE | ( QUERY )=>t= QUERY | ( TEMPLATE )=>t= TEMPLATE | ( ATTRIBUTES )=>t= ATTRIBUTES | ( ENABLED )=>t= ENABLED | ( SALIENCE )=>t= SALIENCE | ( DURATION )=>t= DURATION | ( FROM )=>t= FROM | ( ACCUMULATE )=>t= ACCUMULATE | ( INIT )=>t= INIT | ( ACTION )=>t= ACTION | ( RESULT )=>t= RESULT | ( COLLECT )=>t= COLLECT | ( OR )=>t= OR | ( AND )=>t= AND | ( CONTAINS )=>t= CONTAINS | ( EXCLUDES )=>t= EXCLUDES | ( MATCHES )=>t= MATCHES | ( NULL )=>t= NULL | ( EXISTS )=>t= EXISTS | ( NOT )=>t= NOT | ( EVAL )=>t= EVAL | ( FORALL )=>t= FORALL | ( WHEN )=>t= WHEN | ( THEN )=>t= THEN | t= END ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1428:2: ( ( ID )=>t= ID | ( PACKAGE )=>t= PACKAGE | ( FUNCTION )=>t= FUNCTION | ( GLOBAL )=>t= GLOBAL | ( IMPORT )=>t= IMPORT | ( RULE )=>t= RULE | ( QUERY )=>t= QUERY | ( TEMPLATE )=>t= TEMPLATE | ( ATTRIBUTES )=>t= ATTRIBUTES | ( ENABLED )=>t= ENABLED | ( SALIENCE )=>t= SALIENCE | ( DURATION )=>t= DURATION | ( FROM )=>t= FROM | ( ACCUMULATE )=>t= ACCUMULATE | ( INIT )=>t= INIT | ( ACTION )=>t= ACTION | ( RESULT )=>t= RESULT | ( COLLECT )=>t= COLLECT | ( OR )=>t= OR | ( AND )=>t= AND | ( CONTAINS )=>t= CONTAINS | ( EXCLUDES )=>t= EXCLUDES | ( MATCHES )=>t= MATCHES | ( NULL )=>t= NULL | ( EXISTS )=>t= EXISTS | ( NOT )=>t= NOT | ( EVAL )=>t= EVAL | ( FORALL )=>t= FORALL | ( WHEN )=>t= WHEN | ( THEN )=>t= THEN | t= END )
            {
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1428:2: ( ( ID )=>t= ID | ( PACKAGE )=>t= PACKAGE | ( FUNCTION )=>t= FUNCTION | ( GLOBAL )=>t= GLOBAL | ( IMPORT )=>t= IMPORT | ( RULE )=>t= RULE | ( QUERY )=>t= QUERY | ( TEMPLATE )=>t= TEMPLATE | ( ATTRIBUTES )=>t= ATTRIBUTES | ( ENABLED )=>t= ENABLED | ( SALIENCE )=>t= SALIENCE | ( DURATION )=>t= DURATION | ( FROM )=>t= FROM | ( ACCUMULATE )=>t= ACCUMULATE | ( INIT )=>t= INIT | ( ACTION )=>t= ACTION | ( RESULT )=>t= RESULT | ( COLLECT )=>t= COLLECT | ( OR )=>t= OR | ( AND )=>t= AND | ( CONTAINS )=>t= CONTAINS | ( EXCLUDES )=>t= EXCLUDES | ( MATCHES )=>t= MATCHES | ( NULL )=>t= NULL | ( EXISTS )=>t= EXISTS | ( NOT )=>t= NOT | ( EVAL )=>t= EVAL | ( FORALL )=>t= FORALL | ( WHEN )=>t= WHEN | ( THEN )=>t= THEN | t= END )
                int alt57 = 31;
                switch ( this.input.LA( 1 ) ) {
                    case ID :
                        alt57 = 1;
                        break;
                    case PACKAGE :
                        alt57 = 2;
                        break;
                    case FUNCTION :
                        alt57 = 3;
                        break;
                    case GLOBAL :
                        alt57 = 4;
                        break;
                    case IMPORT :
                        alt57 = 5;
                        break;
                    case RULE :
                        alt57 = 6;
                        break;
                    case QUERY :
                        alt57 = 7;
                        break;
                    case TEMPLATE :
                        alt57 = 8;
                        break;
                    case ATTRIBUTES :
                        alt57 = 9;
                        break;
                    case ENABLED :
                        alt57 = 10;
                        break;
                    case SALIENCE :
                        alt57 = 11;
                        break;
                    case DURATION :
                        alt57 = 12;
                        break;
                    case FROM :
                        alt57 = 13;
                        break;
                    case ACCUMULATE :
                        alt57 = 14;
                        break;
                    case INIT :
                        alt57 = 15;
                        break;
                    case ACTION :
                        alt57 = 16;
                        break;
                    case RESULT :
                        alt57 = 17;
                        break;
                    case COLLECT :
                        alt57 = 18;
                        break;
                    case OR :
                        alt57 = 19;
                        break;
                    case AND :
                        alt57 = 20;
                        break;
                    case CONTAINS :
                        alt57 = 21;
                        break;
                    case EXCLUDES :
                        alt57 = 22;
                        break;
                    case MATCHES :
                        alt57 = 23;
                        break;
                    case NULL :
                        alt57 = 24;
                        break;
                    case EXISTS :
                        alt57 = 25;
                        break;
                    case NOT :
                        alt57 = 26;
                        break;
                    case EVAL :
                        alt57 = 27;
                        break;
                    case FORALL :
                        alt57 = 28;
                        break;
                    case WHEN :
                        alt57 = 29;
                        break;
                    case THEN :
                        alt57 = 30;
                        break;
                    case END :
                        alt57 = 31;
                        break;
                    default :
                        if ( this.backtracking > 0 ) {
                            this.failed = true;
                            return tok;
                        }
                        final NoViableAltException nvae = new NoViableAltException( "1428:2: ( ( ID )=>t= ID | ( PACKAGE )=>t= PACKAGE | ( FUNCTION )=>t= FUNCTION | ( GLOBAL )=>t= GLOBAL | ( IMPORT )=>t= IMPORT | ( RULE )=>t= RULE | ( QUERY )=>t= QUERY | ( TEMPLATE )=>t= TEMPLATE | ( ATTRIBUTES )=>t= ATTRIBUTES | ( ENABLED )=>t= ENABLED | ( SALIENCE )=>t= SALIENCE | ( DURATION )=>t= DURATION | ( FROM )=>t= FROM | ( ACCUMULATE )=>t= ACCUMULATE | ( INIT )=>t= INIT | ( ACTION )=>t= ACTION | ( RESULT )=>t= RESULT | ( COLLECT )=>t= COLLECT | ( OR )=>t= OR | ( AND )=>t= AND | ( CONTAINS )=>t= CONTAINS | ( EXCLUDES )=>t= EXCLUDES | ( MATCHES )=>t= MATCHES | ( NULL )=>t= NULL | ( EXISTS )=>t= EXISTS | ( NOT )=>t= NOT | ( EVAL )=>t= EVAL | ( FORALL )=>t= FORALL | ( WHEN )=>t= WHEN | ( THEN )=>t= THEN | t= END )",
                                                                              57,
                                                                              0,
                                                                              this.input );

                        throw nvae;
                }

                switch ( alt57 ) {
                    case 1 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1428:10: ( ID )=>t= ID
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               ID,
                               FOLLOW_ID_in_identifier4301 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 2 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1429:4: ( PACKAGE )=>t= PACKAGE
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               PACKAGE,
                               FOLLOW_PACKAGE_in_identifier4314 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 3 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1430:4: ( FUNCTION )=>t= FUNCTION
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               FUNCTION,
                               FOLLOW_FUNCTION_in_identifier4321 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 4 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1431:4: ( GLOBAL )=>t= GLOBAL
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               GLOBAL,
                               FOLLOW_GLOBAL_in_identifier4328 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 5 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1432:4: ( IMPORT )=>t= IMPORT
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               IMPORT,
                               FOLLOW_IMPORT_in_identifier4335 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 6 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1433:4: ( RULE )=>t= RULE
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               RULE,
                               FOLLOW_RULE_in_identifier4344 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 7 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1434:4: ( QUERY )=>t= QUERY
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               QUERY,
                               FOLLOW_QUERY_in_identifier4351 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 8 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1435:17: ( TEMPLATE )=>t= TEMPLATE
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               TEMPLATE,
                               FOLLOW_TEMPLATE_in_identifier4372 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 9 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1436:17: ( ATTRIBUTES )=>t= ATTRIBUTES
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               ATTRIBUTES,
                               FOLLOW_ATTRIBUTES_in_identifier4400 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 10 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1437:17: ( ENABLED )=>t= ENABLED
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               ENABLED,
                               FOLLOW_ENABLED_in_identifier4426 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 11 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1438:17: ( SALIENCE )=>t= SALIENCE
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               SALIENCE,
                               FOLLOW_SALIENCE_in_identifier4455 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 12 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1439:17: ( DURATION )=>t= DURATION
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               DURATION,
                               FOLLOW_DURATION_in_identifier4477 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 13 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1440:17: ( FROM )=>t= FROM
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               FROM,
                               FOLLOW_FROM_in_identifier4499 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 14 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1441:17: ( ACCUMULATE )=>t= ACCUMULATE
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               ACCUMULATE,
                               FOLLOW_ACCUMULATE_in_identifier4528 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 15 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1442:17: ( INIT )=>t= INIT
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               INIT,
                               FOLLOW_INIT_in_identifier4550 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 16 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1443:17: ( ACTION )=>t= ACTION
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               ACTION,
                               FOLLOW_ACTION_in_identifier4579 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 17 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1444:17: ( RESULT )=>t= RESULT
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               RESULT,
                               FOLLOW_RESULT_in_identifier4608 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 18 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1445:17: ( COLLECT )=>t= COLLECT
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               COLLECT,
                               FOLLOW_COLLECT_in_identifier4637 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 19 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1446:17: ( OR )=>t= OR
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               OR,
                               FOLLOW_OR_in_identifier4666 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 20 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1447:17: ( AND )=>t= AND
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               AND,
                               FOLLOW_AND_in_identifier4695 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 21 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1448:17: ( CONTAINS )=>t= CONTAINS
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               CONTAINS,
                               FOLLOW_CONTAINS_in_identifier4724 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 22 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1449:17: ( EXCLUDES )=>t= EXCLUDES
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               EXCLUDES,
                               FOLLOW_EXCLUDES_in_identifier4746 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 23 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1450:17: ( MATCHES )=>t= MATCHES
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               MATCHES,
                               FOLLOW_MATCHES_in_identifier4768 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 24 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1451:17: ( NULL )=>t= NULL
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               NULL,
                               FOLLOW_NULL_in_identifier4797 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 25 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1452:17: ( EXISTS )=>t= EXISTS
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               EXISTS,
                               FOLLOW_EXISTS_in_identifier4826 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 26 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1453:17: ( NOT )=>t= NOT
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               NOT,
                               FOLLOW_NOT_in_identifier4855 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 27 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1454:17: ( EVAL )=>t= EVAL
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               EVAL,
                               FOLLOW_EVAL_in_identifier4884 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 28 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1455:17: ( FORALL )=>t= FORALL
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               FORALL,
                               FOLLOW_FORALL_in_identifier4913 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 29 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1456:17: ( WHEN )=>t= WHEN
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               WHEN,
                               FOLLOW_WHEN_in_identifier4951 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 30 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1457:17: ( THEN )=>t= THEN
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               THEN,
                               FOLLOW_THEN_in_identifier4983 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;
                    case 31 :
                        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1458:17: t= END
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               END,
                               FOLLOW_END_in_identifier5012 );
                        if ( this.failed ) {
                            return tok;
                        }

                    }
                        break;

                }

                if ( this.backtracking == 0 ) {

                    tok = t;

                }

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return tok;
    }

    // $ANTLR end identifier

    // $ANTLR start synpred4
    public void synpred4_fragment() throws RecognitionException {
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:4: ( function_import_statement )
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:189:4: function_import_statement
        {
            pushFollow( FOLLOW_function_import_statement_in_synpred4114 );
            function_import_statement();
            this._fsp--;
            if ( this.failed ) {
                return;
            }

        }
    }

    // $ANTLR end synpred4

    // $ANTLR start synpred5
    public void synpred5_fragment() throws RecognitionException {
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:190:4: ( import_statement )
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:190:4: import_statement
        {
            pushFollow( FOLLOW_import_statement_in_synpred5120 );
            import_statement();
            this._fsp--;
            if ( this.failed ) {
                return;
            }

        }
    }

    // $ANTLR end synpred5

    // $ANTLR start synpred38
    public void synpred38_fragment() throws RecognitionException {
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:718:4: ( paren_chunk[from] )
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:718:4: paren_chunk[from]
        {
            pushFollow( FOLLOW_paren_chunk_in_synpred381788 );
            paren_chunk( this.from );
            this._fsp--;
            if ( this.failed ) {
                return;
            }

        }
    }

    // $ANTLR end synpred38

    // $ANTLR start synpred41
    public void synpred41_fragment() throws RecognitionException {
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:753:6: ( LEFT_PAREN )
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:753:8: LEFT_PAREN
        {
            match( this.input,
                   LEFT_PAREN,
                   FOLLOW_LEFT_PAREN_in_synpred411888 );
            if ( this.failed ) {
                return;
            }

        }
    }

    // $ANTLR end synpred41

    // $ANTLR start synpred44
    public void synpred44_fragment() throws RecognitionException {
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:850:6: ( (OR|'||') fact )
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:850:6: (OR|'||') fact
        {
            if ( this.input.LA( 1 ) == OR || this.input.LA( 1 ) == 68 ) {
                this.input.consume();
                this.errorRecovery = false;
                this.failed = false;
            } else {
                if ( this.backtracking > 0 ) {
                    this.failed = true;
                    return;
                }
                final MismatchedSetException mse = new MismatchedSetException( null,
                                                                         this.input );
                recoverFromMismatchedSet( this.input,
                                          mse,
                                          FOLLOW_set_in_synpred442197 );
                throw mse;
            }

            pushFollow( FOLLOW_fact_in_synpred442214 );
            fact();
            this._fsp--;
            if ( this.failed ) {
                return;
            }

        }
    }

    // $ANTLR end synpred44

    // $ANTLR start synpred72
    public void synpred72_fragment() throws RecognitionException {
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1228:14: ( ACCUMULATE )
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1228:16: ACCUMULATE
        {
            match( this.input,
                   ACCUMULATE,
                   FOLLOW_ACCUMULATE_in_synpred723458 );
            if ( this.failed ) {
                return;
            }

        }
    }

    // $ANTLR end synpred72

    // $ANTLR start synpred73
    public void synpred73_fragment() throws RecognitionException {
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1229:14: ( COLLECT )
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1229:16: COLLECT
        {
            match( this.input,
                   COLLECT,
                   FOLLOW_COLLECT_in_synpred733487 );
            if ( this.failed ) {
                return;
            }

        }
    }

    // $ANTLR end synpred73

    // $ANTLR start synpred74
    public void synpred74_fragment() throws RecognitionException {
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1230:14: (~ (ACCUMULATE|COLLECT))
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1230:16: ~ (ACCUMULATE|COLLECT)
        {
            if ( (this.input.LA( 1 ) >= PACKAGE && this.input.LA( 1 ) <= LOCK_ON_ACTIVE) || (this.input.LA( 1 ) >= INIT && this.input.LA( 1 ) <= RESULT) || (this.input.LA( 1 ) >= ID && this.input.LA( 1 ) <= 78) ) {
                this.input.consume();
                this.errorRecovery = false;
                this.failed = false;
            } else {
                if ( this.backtracking > 0 ) {
                    this.failed = true;
                    return;
                }
                final MismatchedSetException mse = new MismatchedSetException( null,
                                                                         this.input );
                recoverFromMismatchedSet( this.input,
                                          mse,
                                          FOLLOW_set_in_synpred743517 );
                throw mse;
            }

        }
    }

    // $ANTLR end synpred74

    public boolean synpred44() {
        this.backtracking++;
        final int start = this.input.mark();
        try {
            synpred44_fragment(); // can never throw exception
        } catch ( final RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        final boolean success = !this.failed;
        this.input.rewind( start );
        this.backtracking--;
        this.failed = false;
        return success;
    }

    public boolean synpred74() {
        this.backtracking++;
        final int start = this.input.mark();
        try {
            synpred74_fragment(); // can never throw exception
        } catch ( final RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        final boolean success = !this.failed;
        this.input.rewind( start );
        this.backtracking--;
        this.failed = false;
        return success;
    }

    public boolean synpred38() {
        this.backtracking++;
        final int start = this.input.mark();
        try {
            synpred38_fragment(); // can never throw exception
        } catch ( final RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        final boolean success = !this.failed;
        this.input.rewind( start );
        this.backtracking--;
        this.failed = false;
        return success;
    }

    public boolean synpred72() {
        this.backtracking++;
        final int start = this.input.mark();
        try {
            synpred72_fragment(); // can never throw exception
        } catch ( final RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        final boolean success = !this.failed;
        this.input.rewind( start );
        this.backtracking--;
        this.failed = false;
        return success;
    }

    public boolean synpred73() {
        this.backtracking++;
        final int start = this.input.mark();
        try {
            synpred73_fragment(); // can never throw exception
        } catch ( final RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        final boolean success = !this.failed;
        this.input.rewind( start );
        this.backtracking--;
        this.failed = false;
        return success;
    }

    public boolean synpred5() {
        this.backtracking++;
        final int start = this.input.mark();
        try {
            synpred5_fragment(); // can never throw exception
        } catch ( final RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        final boolean success = !this.failed;
        this.input.rewind( start );
        this.backtracking--;
        this.failed = false;
        return success;
    }

    public boolean synpred41() {
        this.backtracking++;
        final int start = this.input.mark();
        try {
            synpred41_fragment(); // can never throw exception
        } catch ( final RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        final boolean success = !this.failed;
        this.input.rewind( start );
        this.backtracking--;
        this.failed = false;
        return success;
    }

    public boolean synpred4() {
        this.backtracking++;
        final int start = this.input.mark();
        try {
            synpred4_fragment(); // can never throw exception
        } catch ( final RecognitionException re ) {
            System.err.println( "impossible: " + re );
        }
        final boolean success = !this.failed;
        this.input.rewind( start );
        this.backtracking--;
        this.failed = false;
        return success;
    }

    protected DFA8               dfa8            = new DFA8( this );
    protected DFA9               dfa9            = new DFA9( this );
    public static final String   DFA8_eotS       = "\6\uffff";
    public static final String   DFA8_eofS       = "\6\uffff";
    public static final String   DFA8_minS       = "\2\4\2\uffff\1\55\1\4";
    public static final String   DFA8_maxS       = "\1\64\1\102\2\uffff\1\55\1\102";
    public static final String   DFA8_acceptS    = "\2\uffff\1\2\1\1\2\uffff";
    public static final String   DFA8_specialS   = "\6\uffff}>";
    public static final String[] DFA8_transition = {"\12\2\3\uffff\1\2\1\uffff\1\2\6\uffff\1\2\1\uffff\5\2\1\1\1\2\2" + "\uffff\3\2\1\uffff\1\2\4\uffff\7\2",
            "\12\3\3\uffff\1\3\1\uffff\1\3\6\uffff\1\3\1\uffff\7\3\1\uffff\1" + "\2\3\3\1\uffff\1\3\2\uffff\1\4\1\uffff\7\3\13\uffff\1\3\1\uffff" + "\1\2", "", "", "\1\5",
            "\12\3\3\uffff\1\3\1\uffff\1\3\6\uffff\1\3\1\uffff\7\3\1\uffff\1" + "\2\3\3\1\uffff\1\3\2\uffff\1\4\1\uffff\7\3\15\uffff\1\2"};

    class DFA8 extends DFA {
        public DFA8(final BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA.unpackEncodedString( DFA8_eotS );
            this.eof = DFA.unpackEncodedString( DFA8_eofS );
            this.min = DFA.unpackEncodedStringToUnsignedChars( DFA8_minS );
            this.max = DFA.unpackEncodedStringToUnsignedChars( DFA8_maxS );
            this.accept = DFA.unpackEncodedString( DFA8_acceptS );
            this.special = DFA.unpackEncodedString( DFA8_specialS );
            final int numStates = DFA8_transition.length;
            this.transition = new short[numStates][];
            for ( int i = 0; i < numStates; i++ ) {
                this.transition[i] = DFA.unpackEncodedString( DFA8_transition[i] );
            }
        }

        public String getDescription() {
            return "307:6: ( ( dotted_name[null] )=>paramType= dotted_name[null] )?";
        }
    }

    public static final String   DFA9_eotS       = "\6\uffff";
    public static final String   DFA9_eofS       = "\6\uffff";
    public static final String   DFA9_minS       = "\2\4\2\uffff\1\55\1\4";
    public static final String   DFA9_maxS       = "\1\64\1\102\2\uffff\1\55\1\102";
    public static final String   DFA9_acceptS    = "\2\uffff\1\2\1\1\2\uffff";
    public static final String   DFA9_specialS   = "\6\uffff}>";
    public static final String[] DFA9_transition = {"\12\2\3\uffff\1\2\1\uffff\1\2\6\uffff\1\2\1\uffff\5\2\1\1\1\2\2" + "\uffff\3\2\1\uffff\1\2\4\uffff\7\2",
            "\12\3\3\uffff\1\3\1\uffff\1\3\6\uffff\1\3\1\uffff\7\3\1\uffff\1" + "\2\3\3\1\uffff\1\3\2\uffff\1\4\1\uffff\7\3\13\uffff\1\3\1\uffff" + "\1\2", "", "", "\1\5",
            "\12\3\3\uffff\1\3\1\uffff\1\3\6\uffff\1\3\1\uffff\7\3\1\uffff\1" + "\2\3\3\1\uffff\1\3\2\uffff\1\4\1\uffff\7\3\15\uffff\1\2"};

    class DFA9 extends DFA {
        public DFA9(final BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA.unpackEncodedString( DFA9_eotS );
            this.eof = DFA.unpackEncodedString( DFA9_eofS );
            this.min = DFA.unpackEncodedStringToUnsignedChars( DFA9_minS );
            this.max = DFA.unpackEncodedStringToUnsignedChars( DFA9_maxS );
            this.accept = DFA.unpackEncodedString( DFA9_acceptS );
            this.special = DFA.unpackEncodedString( DFA9_specialS );
            final int numStates = DFA9_transition.length;
            this.transition = new short[numStates][];
            for ( int i = 0; i < numStates; i++ ) {
                this.transition[i] = DFA.unpackEncodedString( DFA9_transition[i] );
            }
        }

        public String getDescription() {
            return "311:11: ( ( dotted_name[null] )=>paramType= dotted_name[null] )?";
        }
    }

    public static final BitSet FOLLOW_63_in_opt_semicolon46                           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_prolog_in_compilation_unit58                    = new BitSet( new long[]{0x0000000000000DE0L} );
    public static final BitSet FOLLOW_statement_in_compilation_unit65                 = new BitSet( new long[]{0x0000000000000DE2L} );
    public static final BitSet FOLLOW_package_statement_in_prolog90                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_function_import_statement_in_statement114       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_import_statement_in_statement120                = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_global_in_statement126                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_function_in_statement132                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_template_in_statement146                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_rule_in_statement155                            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_query_in_statement167                           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_PACKAGE_in_package_statement196                 = new BitSet( new long[]{0x0000000200000000L} );
    public static final BitSet FOLLOW_dotted_name_in_package_statement200             = new BitSet( new long[]{0x8000000000000002L} );
    public static final BitSet FOLLOW_opt_semicolon_in_package_statement203           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_IMPORT_in_import_statement235                   = new BitSet( new long[]{0x001FC2E7F40A3FF0L} );
    public static final BitSet FOLLOW_import_name_in_import_statement258              = new BitSet( new long[]{0x8000000000000002L} );
    public static final BitSet FOLLOW_opt_semicolon_in_import_statement261            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_IMPORT_in_function_import_statement287          = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_FUNCTION_in_function_import_statement289        = new BitSet( new long[]{0x001FC2E7F40A3FF0L} );
    public static final BitSet FOLLOW_import_name_in_function_import_statement312     = new BitSet( new long[]{0x8000000000000002L} );
    public static final BitSet FOLLOW_opt_semicolon_in_function_import_statement315   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_identifier_in_import_name343                    = new BitSet( new long[]{0x0000000000000002L, 0x0000000000000003L} );
    public static final BitSet FOLLOW_64_in_import_name355                            = new BitSet( new long[]{0x001FC2E7F40A3FF0L} );
    public static final BitSet FOLLOW_identifier_in_import_name359                    = new BitSet( new long[]{0x0000000000000002L, 0x0000000000000003L} );
    public static final BitSet FOLLOW_65_in_import_name383                            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_GLOBAL_in_global419                             = new BitSet( new long[]{0x0000000200000000L} );
    public static final BitSet FOLLOW_dotted_name_in_global430                        = new BitSet( new long[]{0x001FC2E7F40A3FF0L} );
    public static final BitSet FOLLOW_identifier_in_global442                         = new BitSet( new long[]{0x8000000000000002L} );
    public static final BitSet FOLLOW_opt_semicolon_in_global444                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_FUNCTION_in_function471                         = new BitSet( new long[]{0x001FC2E7F40A3FF0L} );
    public static final BitSet FOLLOW_dotted_name_in_function476                      = new BitSet( new long[]{0x001FC2E7F40A3FF0L} );
    public static final BitSet FOLLOW_identifier_in_function483                       = new BitSet( new long[]{0x0000000800000000L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_function492                       = new BitSet( new long[]{0x001FC2F7F40A3FF0L} );
    public static final BitSet FOLLOW_dotted_name_in_function502                      = new BitSet( new long[]{0x001FC2E7F40A3FF0L} );
    public static final BitSet FOLLOW_argument_in_function509                         = new BitSet( new long[]{0x0000001000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_66_in_function523                               = new BitSet( new long[]{0x001FC2E7F40A3FF0L} );
    public static final BitSet FOLLOW_dotted_name_in_function528                      = new BitSet( new long[]{0x001FC2E7F40A3FF0L} );
    public static final BitSet FOLLOW_argument_in_function535                         = new BitSet( new long[]{0x0000001000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function559                      = new BitSet( new long[]{0x0000040000000000L} );
    public static final BitSet FOLLOW_curly_chunk_in_function565                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_QUERY_in_query597                               = new BitSet( new long[]{0x0000000200008000L} );
    public static final BitSet FOLLOW_name_in_query601                                = new BitSet( new long[]{0x000F000A00000200L} );
    public static final BitSet FOLLOW_normal_lhs_block_in_query614                    = new BitSet( new long[]{0x0000000000000200L} );
    public static final BitSet FOLLOW_END_in_query631                                 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_TEMPLATE_in_template661                         = new BitSet( new long[]{0x001FC2E7F40A3FF0L} );
    public static final BitSet FOLLOW_identifier_in_template665                       = new BitSet( new long[]{0x8000000200000000L} );
    public static final BitSet FOLLOW_opt_semicolon_in_template667                    = new BitSet( new long[]{0x0000000200000000L} );
    public static final BitSet FOLLOW_template_slot_in_template682                    = new BitSet( new long[]{0x0000000200000200L} );
    public static final BitSet FOLLOW_END_in_template699                              = new BitSet( new long[]{0x8000000000000002L} );
    public static final BitSet FOLLOW_opt_semicolon_in_template701                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_dotted_name_in_template_slot747                 = new BitSet( new long[]{0x001FC2E7F40A3FF0L} );
    public static final BitSet FOLLOW_identifier_in_template_slot765                  = new BitSet( new long[]{0x8000000000000002L} );
    public static final BitSet FOLLOW_opt_semicolon_in_template_slot767               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_RULE_in_rule800                                 = new BitSet( new long[]{0x0000000200008000L} );
    public static final BitSet FOLLOW_name_in_rule804                                 = new BitSet( new long[]{0x001000000FEB7000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_rule_attributes_in_rule813                      = new BitSet( new long[]{0x0010000000001000L} );
    public static final BitSet FOLLOW_WHEN_in_rule822                                 = new BitSet( new long[]{0x001F000A00000000L, 0x0000000000000008L} );
    public static final BitSet FOLLOW_67_in_rule824                                   = new BitSet( new long[]{0x001F000A00000000L} );
    public static final BitSet FOLLOW_normal_lhs_block_in_rule842                     = new BitSet( new long[]{0x0010000000000000L} );
    public static final BitSet FOLLOW_rhs_chunk_in_rule863                            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ATTRIBUTES_in_rule_attributes884                = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000008L} );
    public static final BitSet FOLLOW_67_in_rule_attributes886                        = new BitSet( new long[]{0x000000000FEB4002L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_66_in_rule_attributes895                        = new BitSet( new long[]{0x000000000FEB4000L} );
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes900            = new BitSet( new long[]{0x000000000FEB4002L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_salience_in_rule_attribute941                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_no_loop_in_rule_attribute951                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute962               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_duration_in_rule_attribute975                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_activation_group_in_rule_attribute989           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1000                = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1011            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1021              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_enabled_in_rule_attribute1031                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ruleflow_group_in_rule_attribute1041            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_lock_on_active_in_rule_attribute1051            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_DATE_EFFECTIVE_in_date_effective1082            = new BitSet( new long[]{0x0000000000008000L} );
    public static final BitSet FOLLOW_STRING_in_date_effective1086                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_DATE_EXPIRES_in_date_expires1119                = new BitSet( new long[]{0x0000000000008000L} );
    public static final BitSet FOLLOW_STRING_in_date_expires1123                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ENABLED_in_enabled1158                          = new BitSet( new long[]{0x0000000000040000L} );
    public static final BitSet FOLLOW_BOOL_in_enabled1162                             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_SALIENCE_in_salience1207                        = new BitSet( new long[]{0x0000000000100000L} );
    public static final BitSet FOLLOW_INT_in_salience1211                             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1249                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1277                          = new BitSet( new long[]{0x0000000000040000L} );
    public static final BitSet FOLLOW_BOOL_in_no_loop1281                             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1330                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1358                    = new BitSet( new long[]{0x0000000000040000L} );
    public static final BitSet FOLLOW_BOOL_in_auto_focus1362                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ACTIVATION_GROUP_in_activation_group1407        = new BitSet( new long[]{0x0000000000008000L} );
    public static final BitSet FOLLOW_STRING_in_activation_group1411                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1443            = new BitSet( new long[]{0x0000000000008000L} );
    public static final BitSet FOLLOW_STRING_in_ruleflow_group1447                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_AGENDA_GROUP_in_agenda_group1479                = new BitSet( new long[]{0x0000000000008000L} );
    public static final BitSet FOLLOW_STRING_in_agenda_group1483                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_DURATION_in_duration1518                        = new BitSet( new long[]{0x0000000000100000L} );
    public static final BitSet FOLLOW_INT_in_duration1522                             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1564            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1592            = new BitSet( new long[]{0x0000000000040000L} );
    public static final BitSet FOLLOW_BOOL_in_lock_on_active1596                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1634                     = new BitSet( new long[]{0x000F000A00000002L} );
    public static final BitSet FOLLOW_lhs_or_in_lhs1671                               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_fact_binding_in_lhs_column1699                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_fact_in_lhs_column1708                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_from_source_in_from_statement1735               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_identifier_in_from_source1777                   = new BitSet( new long[]{0x0000000800000002L, 0x0000000000000001L} );
    public static final BitSet FOLLOW_paren_chunk_in_from_source1788                  = new BitSet( new long[]{0x0000000000000002L, 0x0000000000000001L} );
    public static final BitSet FOLLOW_expression_chain_in_from_source1802             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_64_in_expression_chain1827                      = new BitSet( new long[]{0x001FC2E7F40A3FF0L} );
    public static final BitSet FOLLOW_identifier_in_expression_chain1831              = new BitSet( new long[]{0x0000100800000002L, 0x0000000000000001L} );
    public static final BitSet FOLLOW_square_chunk_in_expression_chain1862            = new BitSet( new long[]{0x0000000000000002L, 0x0000000000000001L} );
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain1896             = new BitSet( new long[]{0x0000000000000002L, 0x0000000000000001L} );
    public static final BitSet FOLLOW_expression_chain_in_expression_chain1917        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ACCUMULATE_in_accumulate_statement1958          = new BitSet( new long[]{0x0000000800000000L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement1968          = new BitSet( new long[]{0x0000000200000000L} );
    public static final BitSet FOLLOW_lhs_column_in_accumulate_statement1972          = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_66_in_accumulate_statement1974                  = new BitSet( new long[]{0x0000000020000000L} );
    public static final BitSet FOLLOW_INIT_in_accumulate_statement1983                = new BitSet( new long[]{0x0000000800000000L} );
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1987         = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_66_in_accumulate_statement1990                  = new BitSet( new long[]{0x0000000040000000L} );
    public static final BitSet FOLLOW_ACTION_in_accumulate_statement1999              = new BitSet( new long[]{0x0000000800000000L} );
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2003         = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_66_in_accumulate_statement2006                  = new BitSet( new long[]{0x0000000080000000L} );
    public static final BitSet FOLLOW_RESULT_in_accumulate_statement2015              = new BitSet( new long[]{0x0000000800000000L} );
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2019         = new BitSet( new long[]{0x0000001000000000L} );
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement2024         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_COLLECT_in_collect_statement2067                = new BitSet( new long[]{0x0000000800000000L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement2077             = new BitSet( new long[]{0x0000000200000000L} );
    public static final BitSet FOLLOW_lhs_column_in_collect_statement2081             = new BitSet( new long[]{0x0000001000000000L} );
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement2085            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_fact_binding2119                          = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000008L} );
    public static final BitSet FOLLOW_67_in_fact_binding2121                          = new BitSet( new long[]{0x0000000A00000000L} );
    public static final BitSet FOLLOW_fact_expression_in_fact_binding2134             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_expression2166               = new BitSet( new long[]{0x0000000A00000000L} );
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2170          = new BitSet( new long[]{0x0000001000000000L} );
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_expression2173              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_fact_in_fact_expression2184                     = new BitSet( new long[]{0x0000000400000002L, 0x0000000000000010L} );
    public static final BitSet FOLLOW_set_in_fact_expression2197                      = new BitSet( new long[]{0x0000000200000000L} );
    public static final BitSet FOLLOW_fact_in_fact_expression2214                     = new BitSet( new long[]{0x0000000400000002L, 0x0000000000000010L} );
    public static final BitSet FOLLOW_dotted_name_in_fact2275                         = new BitSet( new long[]{0x0000000800000000L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact2289                          = new BitSet( new long[]{0x001FC2FFF40A3FF0L} );
    public static final BitSet FOLLOW_constraints_in_fact2299                         = new BitSet( new long[]{0x0000001000000000L} );
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact2312                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_constraint_in_constraints2333                   = new BitSet( new long[]{0x0000000000000002L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_predicate_in_constraints2336                    = new BitSet( new long[]{0x0000000000000002L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_66_in_constraints2344                           = new BitSet( new long[]{0x001FC2EFF40A3FF0L} );
    public static final BitSet FOLLOW_constraint_in_constraints2347                   = new BitSet( new long[]{0x0000000000000002L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_predicate_in_constraints2350                    = new BitSet( new long[]{0x0000000000000002L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_ID_in_constraint2379                            = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000008L} );
    public static final BitSet FOLLOW_67_in_constraint2381                            = new BitSet( new long[]{0x001FC2E7F40A3FF0L} );
    public static final BitSet FOLLOW_identifier_in_constraint2402                    = new BitSet( new long[]{0x000000E000000002L, 0x0000000000003F80L} );
    public static final BitSet FOLLOW_constraint_expression_in_constraint2420         = new BitSet( new long[]{0x0000000000000002L, 0x0000000000000060L} );
    public static final BitSet FOLLOW_set_in_constraint2442                           = new BitSet( new long[]{0x000000E000000000L, 0x0000000000003F00L} );
    public static final BitSet FOLLOW_constraint_expression_in_constraint2461         = new BitSet( new long[]{0x0000000000000002L, 0x0000000000000060L} );
    public static final BitSet FOLLOW_71_in_constraint2489                            = new BitSet( new long[]{0x0000000800000000L} );
    public static final BitSet FOLLOW_predicate_in_constraint2491                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_set_in_constraint_expression2528                = new BitSet( new long[]{0x0000030A00148000L} );
    public static final BitSet FOLLOW_ID_in_constraint_expression2595                 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_enum_constraint_in_constraint_expression2611    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_literal_constraint_in_constraint_expression2634 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_retval_constraint_in_constraint_expression2648  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_STRING_in_literal_constraint2687                = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_INT_in_literal_constraint2698                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint2711                 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_BOOL_in_literal_constraint2722                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_NULL_in_literal_constraint2734                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_enum_constraint2769                       = new BitSet( new long[]{0x0000000000000000L, 0x0000000000000001L} );
    public static final BitSet FOLLOW_64_in_enum_constraint2775                       = new BitSet( new long[]{0x001FC2E7F40A3FF0L} );
    public static final BitSet FOLLOW_identifier_in_enum_constraint2779               = new BitSet( new long[]{0x0000000000000002L, 0x0000000000000001L} );
    public static final BitSet FOLLOW_paren_chunk_in_predicate2821                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk2870                   = new BitSet( new long[]{0xFFFFFFFFFFFFFFF0L, 0x0000000000007FFFL} );
    public static final BitSet FOLLOW_set_in_paren_chunk2886                          = new BitSet( new long[]{0xFFFFFFFFFFFFFFF0L, 0x0000000000007FFFL} );
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk2910                  = new BitSet( new long[]{0xFFFFFFFFFFFFFFF0L, 0x0000000000007FFFL} );
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk2947                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk2998                   = new BitSet( new long[]{0xFFFFFFFFFFFFFFF0L, 0x0000000000007FFFL} );
    public static final BitSet FOLLOW_set_in_curly_chunk3014                          = new BitSet( new long[]{0xFFFFFFFFFFFFFFF0L, 0x0000000000007FFFL} );
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk3038                  = new BitSet( new long[]{0xFFFFFFFFFFFFFFF0L, 0x0000000000007FFFL} );
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk3075                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk3138                 = new BitSet( new long[]{0xFFFFFFFFFFFFFFF0L, 0x0000000000007FFFL} );
    public static final BitSet FOLLOW_set_in_square_chunk3154                         = new BitSet( new long[]{0xFFFFFFFFFFFFFFF0L, 0x0000000000007FFFL} );
    public static final BitSet FOLLOW_square_chunk_in_square_chunk3178                = new BitSet( new long[]{0xFFFFFFFFFFFFFFF0L, 0x0000000000007FFFL} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk3215                = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint3260            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3288                           = new BitSet( new long[]{0x0000000400000002L, 0x0000000000000010L} );
    public static final BitSet FOLLOW_set_in_lhs_or3297                               = new BitSet( new long[]{0x000F000A00000000L} );
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3307                           = new BitSet( new long[]{0x0000000400000002L, 0x0000000000000010L} );
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3343                        = new BitSet( new long[]{0x0000400000000002L, 0x0000000000004000L} );
    public static final BitSet FOLLOW_set_in_lhs_and3352                              = new BitSet( new long[]{0x000F000A00000000L} );
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3362                        = new BitSet( new long[]{0x0000400000000002L, 0x0000000000004000L} );
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary3399                      = new BitSet( new long[]{0x8000000000000002L} );
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary3407                        = new BitSet( new long[]{0x8000000000000002L} );
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary3415                       = new BitSet( new long[]{0x8000000000000002L} );
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary3423                     = new BitSet( new long[]{0x8000800000000002L} );
    public static final BitSet FOLLOW_FROM_in_lhs_unary3439                           = new BitSet( new long[]{0x001FC2E7F40A3FF0L} );
    public static final BitSet FOLLOW_accumulate_statement_in_lhs_unary3467           = new BitSet( new long[]{0x8000000000000002L} );
    public static final BitSet FOLLOW_collect_statement_in_lhs_unary3496              = new BitSet( new long[]{0x8000000000000002L} );
    public static final BitSet FOLLOW_from_statement_in_lhs_unary3531                 = new BitSet( new long[]{0x8000000000000002L} );
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary3570                     = new BitSet( new long[]{0x8000000000000002L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary3578                     = new BitSet( new long[]{0x000F000A00000000L} );
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary3582                         = new BitSet( new long[]{0x0000001000000000L} );
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary3584                    = new BitSet( new long[]{0x8000000000000002L} );
    public static final BitSet FOLLOW_opt_semicolon_in_lhs_unary3594                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_EXISTS_in_lhs_exist3618                         = new BitSet( new long[]{0x0000000A00000000L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist3638                     = new BitSet( new long[]{0x000F000A00000000L} );
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist3642                         = new BitSet( new long[]{0x0000001000000000L} );
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist3674                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist3724                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_NOT_in_lhs_not3778                              = new BitSet( new long[]{0x0000000A00000000L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not3791                       = new BitSet( new long[]{0x000F000A00000000L} );
    public static final BitSet FOLLOW_lhs_or_in_lhs_not3795                           = new BitSet( new long[]{0x0000001000000000L} );
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not3828                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_lhs_column_in_lhs_not3865                       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_EVAL_in_lhs_eval3913                            = new BitSet( new long[]{0x0000000800000000L} );
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval3917                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_FORALL_in_lhs_forall3946                        = new BitSet( new long[]{0x0000000800000000L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall3948                    = new BitSet( new long[]{0x0000000200000000L} );
    public static final BitSet FOLLOW_lhs_column_in_lhs_forall3952                    = new BitSet( new long[]{0x0000000200000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_66_in_lhs_forall3966                            = new BitSet( new long[]{0x0000000200000000L} );
    public static final BitSet FOLLOW_lhs_column_in_lhs_forall3972                    = new BitSet( new long[]{0x0000001200000000L, 0x0000000000000004L} );
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall3987                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_dotted_name4018                           = new BitSet( new long[]{0x0000100000000002L, 0x0000000000000001L} );
    public static final BitSet FOLLOW_64_in_dotted_name4030                           = new BitSet( new long[]{0x001FC2E7F40A3FF0L} );
    public static final BitSet FOLLOW_identifier_in_dotted_name4034                   = new BitSet( new long[]{0x0000100000000002L, 0x0000000000000001L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dotted_name4056                  = new BitSet( new long[]{0x0000200000000000L} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dotted_name4060                 = new BitSet( new long[]{0x0000100000000002L} );
    public static final BitSet FOLLOW_identifier_in_argument4099                      = new BitSet( new long[]{0x0000100000000002L} );
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument4105                     = new BitSet( new long[]{0x0000200000000000L} );
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument4107                    = new BitSet( new long[]{0x0000100000000002L} );
    public static final BitSet FOLLOW_THEN_in_rhs_chunk4151                           = new BitSet( new long[]{0xFFFFFFFFFFFFFFF0L, 0x0000000000007FFFL} );
    public static final BitSet FOLLOW_set_in_rhs_chunk4163                            = new BitSet( new long[]{0xFFFFFFFFFFFFFFF0L, 0x0000000000007FFFL} );
    public static final BitSet FOLLOW_END_in_rhs_chunk4200                            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_name4244                                  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_STRING_in_name4263                              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_identifier4301                            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_PACKAGE_in_identifier4314                       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_FUNCTION_in_identifier4321                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_GLOBAL_in_identifier4328                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_IMPORT_in_identifier4335                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_RULE_in_identifier4344                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_QUERY_in_identifier4351                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_TEMPLATE_in_identifier4372                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ATTRIBUTES_in_identifier4400                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ENABLED_in_identifier4426                       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_SALIENCE_in_identifier4455                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_DURATION_in_identifier4477                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_FROM_in_identifier4499                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ACCUMULATE_in_identifier4528                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_INIT_in_identifier4550                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ACTION_in_identifier4579                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_RESULT_in_identifier4608                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_COLLECT_in_identifier4637                       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_OR_in_identifier4666                            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_AND_in_identifier4695                           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_CONTAINS_in_identifier4724                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_EXCLUDES_in_identifier4746                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_MATCHES_in_identifier4768                       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_NULL_in_identifier4797                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_EXISTS_in_identifier4826                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_NOT_in_identifier4855                           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_EVAL_in_identifier4884                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_FORALL_in_identifier4913                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_WHEN_in_identifier4951                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_THEN_in_identifier4983                          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_END_in_identifier5012                           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_function_import_statement_in_synpred4114        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_import_statement_in_synpred5120                 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_paren_chunk_in_synpred381788                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred411888                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_set_in_synpred442197                            = new BitSet( new long[]{0x0000000200000000L} );
    public static final BitSet FOLLOW_fact_in_synpred442214                           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ACCUMULATE_in_synpred723458                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_COLLECT_in_synpred733487                        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_set_in_synpred743517                            = new BitSet( new long[]{0x0000000000000002L} );

}