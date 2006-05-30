// $ANTLR 3.0ea8 C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g 2006-05-15 21:35:31

package org.drools.lang;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.FailedPredicateException;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.MismatchedNotSetException;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.MismatchedTreeNodeException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.BoundVariableDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.LiteralDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.ReturnValueDescr;
import org.drools.lang.descr.RuleDescr;

public class RuleParser extends Parser {
    public static final String[] tokenNames                   = new String[]{"<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "ID", "INT", "BOOL", "STRING", "FLOAT", "MISC", "WS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT",
            "MULTI_LINE_COMMENT", "\';\'", "\'package\'", "\'import\'", "\'.\'", "\'.*\'", "\'expander\'", "\'global\'", "\'function\'", "\'(\'", "\',\'", "\')\'", "\'{\'", "\'}\'", "\'query\'", "\'end\'", "\'rule\'", "\'when\'", "\':\'",
            "\'then\'", "\'attributes\'", "\'salience\'", "\'no-loop\'", "\'auto-focus\'", "\'activation-group\'", "\'agenda-group\'", "\'duration\'", "\'or\'", "\'==\'", "\'>\'", "\'>=\'", "\'<\'", "\'<=\'", "\'!=\'", "\'contains\'", "\'matches\'",
            "\'excludes\'", "\'null\'", "\'->\'", "\'||\'", "\'and\'", "\'&&\'", "\'exists\'", "\'not\'", "\'eval\'", "\'[\'", "\']\'", "\'use\'"};
    public static final int      BOOL                         = 7;
    public static final int      INT                          = 6;
    public static final int      WS                           = 11;
    public static final int      EOF                          = -1;
    public static final int      MISC                         = 10;
    public static final int      STRING                       = 8;
    public static final int      EOL                          = 4;
    public static final int      FLOAT                        = 9;
    public static final int      SH_STYLE_SINGLE_LINE_COMMENT = 12;
    public static final int      MULTI_LINE_COMMENT           = 14;
    public static final int      C_STYLE_SINGLE_LINE_COMMENT  = 13;
    public static final int      ID                           = 5;

    public RuleParser(final TokenStream input) {
        super( input );
    }

    public String[] getTokenNames() {
        return RuleParser.tokenNames;
    }

    private ExpanderResolver expanderResolver;
    private Expander         expander;
    private boolean          expanderDebug = false;
    private PackageDescr     packageDescr;
    private final List       errors        = new ArrayList();
    private String           source        = "unknown";
    private int              lineOffset    = 0;

    private boolean          parserDebug   = false;

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

    /**
     * This may be set to enable debuggin of DSLs/expanders.
     * If set to true, expander stuff will be sent to the Std out.
     */
    public void setExpanderDebug(final boolean status) {
        this.expanderDebug = status;
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

    public void setExpanderResolver(final ExpanderResolver expanderResolver) {
        this.expanderResolver = expanderResolver;
    }

    public ExpanderResolver getExpanderResolver() {
        return this.expanderResolver;
    }

    /** Expand the LHS */
    private String runWhenExpander(final String text,
                                   final int line) throws RecognitionException {
        String expanded = text.trim();
        if ( expanded.startsWith( ">" ) ) {
            expanded = expanded.substring( 1 ); //escape !!
        } else {
            try {
                expanded = this.expander.expand( "when",
                                                 text );
            } catch ( final Exception e ) {
                this.errors.add( new ExpanderException( "Unable to expand: " + text + ". Due to " + e.getMessage(),
                                                        line ) );
                return "";
            }
        }
        if ( this.expanderDebug ) {
            System.out.println( "Expanding LHS: " + text + " ----> " + expanded + " --> from line: " + line );
        }
        return expanded;

    }

    /** Reparse the results of the expansion */
    private void reparseLhs(final String text,
                            final AndDescr descrs) throws RecognitionException {
        final CharStream charStream = new ANTLRStringStream( text );
        final RuleParserLexer lexer = new RuleParserLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final RuleParser parser = new RuleParser( tokenStream );
        parser.setLineOffset( descrs.getLine() );
        parser.normal_lhs_block( descrs );

        if ( parser.hasErrors() ) {
            this.errors.addAll( parser.getErrors() );
        }
        if ( this.expanderDebug ) {
            System.out.println( "Reparsing LHS: " + text + " --> successful:" + !parser.hasErrors() );
        }

    }

    /** Expand a line on the RHS */
    private String runThenExpander(final String text,
                                   int startLine) {
        //System.err.println( "expand THEN [" + text + "]" );
        final StringTokenizer lines = new StringTokenizer( text,
                                                           "\n\r" );

        final StringBuffer expanded = new StringBuffer();

        final String eol = System.getProperty( "line.separator" );

        while ( lines.hasMoreTokens() ) {
            startLine++;
            String line = lines.nextToken();
            line = line.trim();
            if ( line.length() > 0 ) {
                if ( line.startsWith( ">" ) ) {
                    expanded.append( line.substring( 1 ) );
                    expanded.append( eol );
                } else {
                    try {
                        expanded.append( this.expander.expand( "then",
                                                               line ) );
                        expanded.append( eol );
                    } catch ( final Exception e ) {
                        this.errors.add( new ExpanderException( "Unable to expand: " + line + ". Due to " + e.getMessage(),
                                                                startLine ) );
                    }
                }
            }
        }

        if ( this.expanderDebug ) {
            System.out.println( "Expanding RHS: " + text + " ----> " + expanded.toString() + " --> from line starting: " + startLine );
        }

        return expanded.toString();
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
            message.append( "mismatched token: " + e.token + "; expecting type " + RuleParser.tokenNames[mte.expecting] );
        } else if ( e instanceof MismatchedTreeNodeException ) {
            final MismatchedTreeNodeException mtne = (MismatchedTreeNodeException) e;
            message.append( "mismatched tree node: " + mtne.foundNode + "; expecting type " + RuleParser.tokenNames[mtne.expecting] );
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

    // $ANTLR start opt_eol
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:252:1: opt_eol : ( (';'|EOL))* ;
    public void opt_eol() throws RecognitionException {
        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:253:17: ( ( (';'|EOL))* )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:253:17: ( (';'|EOL))*
            {
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:253:17: ( (';'|EOL))*
                loop1 : do {
                    int alt1 = 2;
                    final int LA1_0 = this.input.LA( 1 );
                    if ( LA1_0 == RuleParser.EOL ) {
                        alt1 = 1;
                    } else if ( LA1_0 == 15 ) {
                        alt1 = 1;
                    }

                    switch ( alt1 ) {
                        case 1 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:253:18: (';'|EOL)
                        {
                            if ( this.input.LA( 1 ) == RuleParser.EOL || this.input.LA( 1 ) == 15 ) {
                                this.input.consume();
                                this.errorRecovery = false;
                            } else {
                                final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                               this.input );
                                recoverFromMismatchedSet( this.input,
                                                          mse,
                                                          RuleParser.FOLLOW_set_in_opt_eol41 );
                                throw mse;
                            }

                        }
                            break;

                        default :
                            break loop1;
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

    // $ANTLR end opt_eol

    // $ANTLR start compilation_unit
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:256:1: compilation_unit : opt_eol prolog (r= rule | q= query | extra_statement )* ;
    public void compilation_unit() throws RecognitionException {
        RuleDescr r = null;

        QueryDescr q = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:257:17: ( opt_eol prolog (r= rule | q= query | extra_statement )* )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:257:17: opt_eol prolog (r= rule | q= query | extra_statement )*
            {
                this.following.push( RuleParser.FOLLOW_opt_eol_in_compilation_unit57 );
                opt_eol();
                this.following.pop();

                this.following.push( RuleParser.FOLLOW_prolog_in_compilation_unit61 );
                prolog();
                this.following.pop();

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:259:17: (r= rule | q= query | extra_statement )*
                loop2 : do {
                    int alt2 = 4;
                    alt2 = this.dfa2.predict( this.input );
                    switch ( alt2 ) {
                        case 1 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:259:25: r= rule
                        {
                            this.following.push( RuleParser.FOLLOW_rule_in_compilation_unit70 );
                            r = rule();
                            this.following.pop();

                            this.packageDescr.addRule( r );

                        }
                            break;
                        case 2 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:260:25: q= query
                        {
                            this.following.push( RuleParser.FOLLOW_query_in_compilation_unit83 );
                            q = query();
                            this.following.pop();

                            this.packageDescr.addRule( q );

                        }
                            break;
                        case 3 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:261:25: extra_statement
                        {
                            this.following.push( RuleParser.FOLLOW_extra_statement_in_compilation_unit91 );
                            extra_statement();
                            this.following.pop();

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

    // $ANTLR end compilation_unit

    // $ANTLR start prolog
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:265:1: prolog : opt_eol (name= package_statement )? ( extra_statement | expander )* opt_eol ;
    public void prolog() throws RecognitionException {
        String name = null;

        String packageName = "";

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:269:17: ( opt_eol (name= package_statement )? ( extra_statement | expander )* opt_eol )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:269:17: opt_eol (name= package_statement )? ( extra_statement | expander )* opt_eol
            {
                this.following.push( RuleParser.FOLLOW_opt_eol_in_prolog115 );
                opt_eol();
                this.following.pop();

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:270:17: (name= package_statement )?
                int alt3 = 2;
                final int LA3_0 = this.input.LA( 1 );
                if ( LA3_0 == 16 ) {
                    alt3 = 1;
                } else if ( LA3_0 == -1 || LA3_0 == RuleParser.EOL || LA3_0 == 15 || LA3_0 == 17 || (LA3_0 >= 20 && LA3_0 <= 22) || LA3_0 == 28 || LA3_0 == 30 ) {
                    alt3 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "270:17: (name= package_statement )?",
                                                                                3,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt3 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:270:19: name= package_statement
                    {
                        this.following.push( RuleParser.FOLLOW_package_statement_in_prolog123 );
                        name = package_statement();
                        this.following.pop();

                        packageName = name;

                    }
                        break;

                }

                this.packageDescr = new PackageDescr( name );

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:274:17: ( extra_statement | expander )*
                loop4 : do {
                    int alt4 = 3;
                    alt4 = this.dfa4.predict( this.input );
                    switch ( alt4 ) {
                        case 1 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:274:25: extra_statement
                        {
                            this.following.push( RuleParser.FOLLOW_extra_statement_in_prolog138 );
                            extra_statement();
                            this.following.pop();

                        }
                            break;
                        case 2 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:275:25: expander
                        {
                            this.following.push( RuleParser.FOLLOW_expander_in_prolog144 );
                            expander();
                            this.following.pop();

                        }
                            break;

                        default :
                            break loop4;
                    }
                } while ( true );

                this.following.push( RuleParser.FOLLOW_opt_eol_in_prolog156 );
                opt_eol();
                this.following.pop();

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

    // $ANTLR start package_statement
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:281:1: package_statement returns [String packageName] : 'package' opt_eol name= dotted_name ( ';' )? opt_eol ;
    public String package_statement() throws RecognitionException {
        String packageName;
        String name = null;

        packageName = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:286:17: ( 'package' opt_eol name= dotted_name ( ';' )? opt_eol )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:286:17: 'package' opt_eol name= dotted_name ( ';' )? opt_eol
            {
                match( this.input,
                       16,
                       RuleParser.FOLLOW_16_in_package_statement180 );
                this.following.push( RuleParser.FOLLOW_opt_eol_in_package_statement182 );
                opt_eol();
                this.following.pop();

                this.following.push( RuleParser.FOLLOW_dotted_name_in_package_statement186 );
                name = dotted_name();
                this.following.pop();

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:286:52: ( ';' )?
                int alt5 = 2;
                final int LA5_0 = this.input.LA( 1 );
                if ( LA5_0 == 15 ) {
                    alt5 = 1;
                } else if ( LA5_0 == -1 || LA5_0 == RuleParser.EOL || LA5_0 == 17 || (LA5_0 >= 20 && LA5_0 <= 22) || LA5_0 == 28 || LA5_0 == 30 ) {
                    alt5 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "286:52: ( \';\' )?",
                                                                                5,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt5 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:286:52: ';'
                    {
                        match( this.input,
                               15,
                               RuleParser.FOLLOW_15_in_package_statement188 );

                    }
                        break;

                }

                this.following.push( RuleParser.FOLLOW_opt_eol_in_package_statement191 );
                opt_eol();
                this.following.pop();

                packageName = name;

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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:292:1: import_statement : 'import' opt_eol name= import_name ( ';' )? opt_eol ;
    public void import_statement() throws RecognitionException {
        String name = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:293:17: ( 'import' opt_eol name= import_name ( ';' )? opt_eol )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:293:17: 'import' opt_eol name= import_name ( ';' )? opt_eol
            {
                match( this.input,
                       17,
                       RuleParser.FOLLOW_17_in_import_statement207 );
                this.following.push( RuleParser.FOLLOW_opt_eol_in_import_statement209 );
                opt_eol();
                this.following.pop();

                this.following.push( RuleParser.FOLLOW_import_name_in_import_statement213 );
                name = import_name();
                this.following.pop();

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:293:51: ( ';' )?
                int alt6 = 2;
                final int LA6_0 = this.input.LA( 1 );
                if ( LA6_0 == 15 ) {
                    alt6 = 1;
                } else if ( LA6_0 == -1 || LA6_0 == RuleParser.EOL || LA6_0 == 17 || (LA6_0 >= 20 && LA6_0 <= 22) || LA6_0 == 28 || LA6_0 == 30 ) {
                    alt6 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "293:51: ( \';\' )?",
                                                                                6,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt6 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:293:51: ';'
                    {
                        match( this.input,
                               15,
                               RuleParser.FOLLOW_15_in_import_statement215 );

                    }
                        break;

                }

                this.following.push( RuleParser.FOLLOW_opt_eol_in_import_statement218 );
                opt_eol();
                this.following.pop();

                if ( this.packageDescr != null ) {
                    this.packageDescr.addImport( name );
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

    // $ANTLR start import_name
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:300:1: import_name returns [String name] : id= ID ( '.' id= ID )* (star= '.*' )? ;
    public String import_name() throws RecognitionException {
        String name;
        Token id = null;
        Token star = null;

        name = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:305:17: (id= ID ( '.' id= ID )* (star= '.*' )? )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:305:17: id= ID ( '.' id= ID )* (star= '.*' )?
            {
                id = (Token) this.input.LT( 1 );
                match( this.input,
                       RuleParser.ID,
                       RuleParser.FOLLOW_ID_in_import_name249 );
                name = id.getText();
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:305:46: ( '.' id= ID )*
                loop7 : do {
                    int alt7 = 2;
                    final int LA7_0 = this.input.LA( 1 );
                    if ( LA7_0 == 18 ) {
                        alt7 = 1;
                    }

                    switch ( alt7 ) {
                        case 1 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:305:48: '.' id= ID
                        {
                            match( this.input,
                                   18,
                                   RuleParser.FOLLOW_18_in_import_name255 );
                            id = (Token) this.input.LT( 1 );
                            match( this.input,
                                   RuleParser.ID,
                                   RuleParser.FOLLOW_ID_in_import_name259 );
                            name = name + "." + id.getText();

                        }
                            break;

                        default :
                            break loop7;
                    }
                } while ( true );

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:305:99: (star= '.*' )?
                int alt8 = 2;
                final int LA8_0 = this.input.LA( 1 );
                if ( LA8_0 == 19 ) {
                    alt8 = 1;
                } else if ( LA8_0 == -1 || LA8_0 == RuleParser.EOL || LA8_0 == 15 || LA8_0 == 17 || (LA8_0 >= 20 && LA8_0 <= 22) || LA8_0 == 28 || LA8_0 == 30 ) {
                    alt8 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "305:99: (star= \'.*\' )?",
                                                                                8,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt8 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:305:100: star= '.*'
                    {
                        star = (Token) this.input.LT( 1 );
                        match( this.input,
                               19,
                               RuleParser.FOLLOW_19_in_import_name269 );
                        name = name + star.getText();

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

    // $ANTLR start expander
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:307:1: expander : 'expander' (name= dotted_name )? ( ';' )? opt_eol ;
    public void expander() throws RecognitionException {
        String name = null;

        final String config = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:311:17: ( 'expander' (name= dotted_name )? ( ';' )? opt_eol )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:311:17: 'expander' (name= dotted_name )? ( ';' )? opt_eol
            {
                match( this.input,
                       20,
                       RuleParser.FOLLOW_20_in_expander289 );
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:311:28: (name= dotted_name )?
                int alt9 = 2;
                final int LA9_0 = this.input.LA( 1 );
                if ( LA9_0 == RuleParser.ID ) {
                    alt9 = 1;
                } else if ( LA9_0 == -1 || LA9_0 == RuleParser.EOL || LA9_0 == 15 || LA9_0 == 17 || (LA9_0 >= 20 && LA9_0 <= 22) || LA9_0 == 28 || LA9_0 == 30 ) {
                    alt9 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "311:28: (name= dotted_name )?",
                                                                                9,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt9 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:311:29: name= dotted_name
                    {
                        this.following.push( RuleParser.FOLLOW_dotted_name_in_expander294 );
                        name = dotted_name();
                        this.following.pop();

                    }
                        break;

                }

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:311:48: ( ';' )?
                int alt10 = 2;
                final int LA10_0 = this.input.LA( 1 );
                if ( LA10_0 == 15 ) {
                    alt10 = 1;
                } else if ( LA10_0 == -1 || LA10_0 == RuleParser.EOL || LA10_0 == 17 || (LA10_0 >= 20 && LA10_0 <= 22) || LA10_0 == 28 || LA10_0 == 30 ) {
                    alt10 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "311:48: ( \';\' )?",
                                                                                10,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt10 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:311:48: ';'
                    {
                        match( this.input,
                               15,
                               RuleParser.FOLLOW_15_in_expander298 );

                    }
                        break;

                }

                this.following.push( RuleParser.FOLLOW_opt_eol_in_expander301 );
                opt_eol();
                this.following.pop();

                if ( this.expanderResolver == null ) {
                    throw new IllegalArgumentException( "Unable to use expander. Make sure a expander or dsl config is being passed to the parser. [ExpanderResolver was not set]." );
                }
                if ( this.expander != null ) {
                    throw new IllegalArgumentException( "Only one 'expander' statement per file is allowed" );
                }
                this.expander = this.expanderResolver.get( name,
                                                           config );

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return;
    }

    // $ANTLR end expander

    // $ANTLR start global
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:321:1: global : 'global' type= dotted_name id= ID ( ';' )? opt_eol ;
    public void global() throws RecognitionException {
        Token id = null;
        String type = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:325:17: ( 'global' type= dotted_name id= ID ( ';' )? opt_eol )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:325:17: 'global' type= dotted_name id= ID ( ';' )? opt_eol
            {
                match( this.input,
                       21,
                       RuleParser.FOLLOW_21_in_global325 );
                this.following.push( RuleParser.FOLLOW_dotted_name_in_global329 );
                type = dotted_name();
                this.following.pop();

                id = (Token) this.input.LT( 1 );
                match( this.input,
                       RuleParser.ID,
                       RuleParser.FOLLOW_ID_in_global333 );
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:325:49: ( ';' )?
                int alt11 = 2;
                final int LA11_0 = this.input.LA( 1 );
                if ( LA11_0 == 15 ) {
                    alt11 = 1;
                } else if ( LA11_0 == -1 || LA11_0 == RuleParser.EOL || LA11_0 == 17 || (LA11_0 >= 20 && LA11_0 <= 22) || LA11_0 == 28 || LA11_0 == 30 ) {
                    alt11 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "325:49: ( \';\' )?",
                                                                                11,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt11 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:325:49: ';'
                    {
                        match( this.input,
                               15,
                               RuleParser.FOLLOW_15_in_global335 );

                    }
                        break;

                }

                this.following.push( RuleParser.FOLLOW_opt_eol_in_global338 );
                opt_eol();
                this.following.pop();

                this.packageDescr.addGlobal( id.getText(),
                                             type );

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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:331:1: function : 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= argument opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol ;
    public void function() throws RecognitionException {
        Token name = null;
        String retType = null;

        String paramType = null;

        String paramName = null;

        String body = null;

        FunctionDescr f = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:336:17: ( 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= argument opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:336:17: 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= argument opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol
            {
                match( this.input,
                       22,
                       RuleParser.FOLLOW_22_in_function362 );
                this.following.push( RuleParser.FOLLOW_opt_eol_in_function364 );
                opt_eol();
                this.following.pop();

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:336:36: (retType= dotted_name )?
                int alt12 = 2;
                alt12 = this.dfa12.predict( this.input );
                switch ( alt12 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:336:37: retType= dotted_name
                    {
                        this.following.push( RuleParser.FOLLOW_dotted_name_in_function369 );
                        retType = dotted_name();
                        this.following.pop();

                    }
                        break;

                }

                this.following.push( RuleParser.FOLLOW_opt_eol_in_function373 );
                opt_eol();
                this.following.pop();

                name = (Token) this.input.LT( 1 );
                match( this.input,
                       RuleParser.ID,
                       RuleParser.FOLLOW_ID_in_function377 );
                this.following.push( RuleParser.FOLLOW_opt_eol_in_function379 );
                opt_eol();
                this.following.pop();

                //System.err.println( "function :: " + name.getText() );
                f = new FunctionDescr( name.getText(),
                                       retType );

                match( this.input,
                       23,
                       RuleParser.FOLLOW_23_in_function388 );
                this.following.push( RuleParser.FOLLOW_opt_eol_in_function390 );
                opt_eol();
                this.following.pop();

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:342:25: ( (paramType= dotted_name )? opt_eol paramName= argument opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument opt_eol )* )?
                int alt16 = 2;
                final int LA16_0 = this.input.LA( 1 );
                if ( (LA16_0 >= RuleParser.EOL && LA16_0 <= RuleParser.ID) || LA16_0 == 15 ) {
                    alt16 = 1;
                } else if ( LA16_0 == 25 ) {
                    alt16 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "342:25: ( (paramType= dotted_name )? opt_eol paramName= argument opt_eol ( \',\' opt_eol (paramType= dotted_name )? opt_eol paramName= argument opt_eol )* )?",
                                                                                16,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt16 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:342:33: (paramType= dotted_name )? opt_eol paramName= argument opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument opt_eol )*
                    {
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:342:33: (paramType= dotted_name )?
                        int alt13 = 2;
                        alt13 = this.dfa13.predict( this.input );
                        switch ( alt13 ) {
                            case 1 :
                                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:342:34: paramType= dotted_name
                            {
                                this.following.push( RuleParser.FOLLOW_dotted_name_in_function400 );
                                paramType = dotted_name();
                                this.following.pop();

                            }
                                break;

                        }

                        this.following.push( RuleParser.FOLLOW_opt_eol_in_function404 );
                        opt_eol();
                        this.following.pop();

                        this.following.push( RuleParser.FOLLOW_argument_in_function408 );
                        paramName = argument();
                        this.following.pop();

                        this.following.push( RuleParser.FOLLOW_opt_eol_in_function410 );
                        opt_eol();
                        this.following.pop();

                        f.addParameter( paramType,
                                        paramName );

                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:346:33: ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument opt_eol )*
                        loop15 : do {
                            int alt15 = 2;
                            final int LA15_0 = this.input.LA( 1 );
                            if ( LA15_0 == 24 ) {
                                alt15 = 1;
                            }

                            switch ( alt15 ) {
                                case 1 :
                                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:346:41: ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument opt_eol
                                {
                                    match( this.input,
                                           24,
                                           RuleParser.FOLLOW_24_in_function424 );
                                    this.following.push( RuleParser.FOLLOW_opt_eol_in_function426 );
                                    opt_eol();
                                    this.following.pop();

                                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:346:53: (paramType= dotted_name )?
                                    int alt14 = 2;
                                    alt14 = this.dfa14.predict( this.input );
                                    switch ( alt14 ) {
                                        case 1 :
                                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:346:54: paramType= dotted_name
                                        {
                                            this.following.push( RuleParser.FOLLOW_dotted_name_in_function431 );
                                            paramType = dotted_name();
                                            this.following.pop();

                                        }
                                            break;

                                    }

                                    this.following.push( RuleParser.FOLLOW_opt_eol_in_function435 );
                                    opt_eol();
                                    this.following.pop();

                                    this.following.push( RuleParser.FOLLOW_argument_in_function439 );
                                    paramName = argument();
                                    this.following.pop();

                                    this.following.push( RuleParser.FOLLOW_opt_eol_in_function441 );
                                    opt_eol();
                                    this.following.pop();

                                    f.addParameter( paramType,
                                                    paramName );

                                }
                                    break;

                                default :
                                    break loop15;
                            }
                        } while ( true );

                    }
                        break;

                }

                match( this.input,
                       25,
                       RuleParser.FOLLOW_25_in_function466 );
                this.following.push( RuleParser.FOLLOW_opt_eol_in_function470 );
                opt_eol();
                this.following.pop();

                match( this.input,
                       26,
                       RuleParser.FOLLOW_26_in_function474 );
                this.following.push( RuleParser.FOLLOW_curly_chunk_in_function481 );
                body = curly_chunk();
                this.following.pop();

                f.setText( body );

                match( this.input,
                       27,
                       RuleParser.FOLLOW_27_in_function490 );

                this.packageDescr.addFunction( f );

                this.following.push( RuleParser.FOLLOW_opt_eol_in_function498 );
                opt_eol();
                this.following.pop();

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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:367:1: query returns [QueryDescr query] : opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol ;
    public QueryDescr query() throws RecognitionException {
        QueryDescr query;
        Token loc = null;
        String queryName = null;

        query = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:372:17: ( opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:372:17: opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol
            {
                this.following.push( RuleParser.FOLLOW_opt_eol_in_query522 );
                opt_eol();
                this.following.pop();

                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       28,
                       RuleParser.FOLLOW_28_in_query528 );
                this.following.push( RuleParser.FOLLOW_word_in_query532 );
                queryName = word();
                this.following.pop();

                this.following.push( RuleParser.FOLLOW_opt_eol_in_query534 );
                opt_eol();
                this.following.pop();

                query = new QueryDescr( queryName,
                                        null );
                query.setLocation( offset( loc.getLine() ),
                                   loc.getCharPositionInLine() );
                final AndDescr lhs = new AndDescr();
                query.setLhs( lhs );
                lhs.setLocation( offset( loc.getLine() ),
                                 loc.getCharPositionInLine() );

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:380:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                int alt17 = 2;
                switch ( this.input.LA( 1 ) ) {
                    case 23 :
                        final int LA17_1 = this.input.LA( 2 );
                        if ( this.expander != null ) {
                            alt17 = 1;
                        } else if ( true ) {
                            alt17 = 2;
                        } else {
                            final NoViableAltException nvae = new NoViableAltException( "380:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                        17,
                                                                                        1,
                                                                                        this.input );

                            throw nvae;
                        }
                        break;
                    case EOL :
                        final int LA17_2 = this.input.LA( 2 );
                        if ( this.expander != null ) {
                            alt17 = 1;
                        } else if ( true ) {
                            alt17 = 2;
                        } else {
                            final NoViableAltException nvae = new NoViableAltException( "380:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                        17,
                                                                                        2,
                                                                                        this.input );

                            throw nvae;
                        }
                        break;
                    case 29 :
                        final int LA17_3 = this.input.LA( 2 );
                        if ( this.expander != null ) {
                            alt17 = 1;
                        } else if ( true ) {
                            alt17 = 2;
                        } else {
                            final NoViableAltException nvae = new NoViableAltException( "380:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                        17,
                                                                                        3,
                                                                                        this.input );

                            throw nvae;
                        }
                        break;
                    case 56 :
                        final int LA17_4 = this.input.LA( 2 );
                        if ( this.expander != null ) {
                            alt17 = 1;
                        } else if ( true ) {
                            alt17 = 2;
                        } else {
                            final NoViableAltException nvae = new NoViableAltException( "380:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                        17,
                                                                                        4,
                                                                                        this.input );

                            throw nvae;
                        }
                        break;
                    case 57 :
                        final int LA17_5 = this.input.LA( 2 );
                        if ( this.expander != null ) {
                            alt17 = 1;
                        } else if ( true ) {
                            alt17 = 2;
                        } else {
                            final NoViableAltException nvae = new NoViableAltException( "380:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                        17,
                                                                                        5,
                                                                                        this.input );

                            throw nvae;
                        }
                        break;
                    case 58 :
                        final int LA17_6 = this.input.LA( 2 );
                        if ( this.expander != null ) {
                            alt17 = 1;
                        } else if ( true ) {
                            alt17 = 2;
                        } else {
                            final NoViableAltException nvae = new NoViableAltException( "380:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                        17,
                                                                                        6,
                                                                                        this.input );

                            throw nvae;
                        }
                        break;
                    case ID :
                        final int LA17_7 = this.input.LA( 2 );
                        if ( this.expander != null ) {
                            alt17 = 1;
                        } else if ( true ) {
                            alt17 = 2;
                        } else {
                            final NoViableAltException nvae = new NoViableAltException( "380:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                        17,
                                                                                        7,
                                                                                        this.input );

                            throw nvae;
                        }
                        break;
                    case 15 :
                        final int LA17_8 = this.input.LA( 2 );
                        if ( this.expander != null ) {
                            alt17 = 1;
                        } else if ( true ) {
                            alt17 = 2;
                        } else {
                            final NoViableAltException nvae = new NoViableAltException( "380:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                        17,
                                                                                        8,
                                                                                        this.input );

                            throw nvae;
                        }
                        break;
                    case INT :
                    case BOOL :
                    case STRING :
                    case FLOAT :
                    case MISC :
                    case WS :
                    case SH_STYLE_SINGLE_LINE_COMMENT :
                    case C_STYLE_SINGLE_LINE_COMMENT :
                    case MULTI_LINE_COMMENT :
                    case 16 :
                    case 17 :
                    case 18 :
                    case 19 :
                    case 20 :
                    case 21 :
                    case 22 :
                    case 24 :
                    case 25 :
                    case 26 :
                    case 27 :
                    case 28 :
                    case 30 :
                    case 31 :
                    case 32 :
                    case 33 :
                    case 34 :
                    case 35 :
                    case 36 :
                    case 37 :
                    case 38 :
                    case 39 :
                    case 40 :
                    case 41 :
                    case 42 :
                    case 43 :
                    case 44 :
                    case 45 :
                    case 46 :
                    case 47 :
                    case 48 :
                    case 49 :
                    case 50 :
                    case 51 :
                    case 52 :
                    case 53 :
                    case 54 :
                    case 55 :
                    case 59 :
                    case 60 :
                    case 61 :
                        alt17 = 1;
                        break;
                    default :
                        final NoViableAltException nvae = new NoViableAltException( "380:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                    17,
                                                                                    0,
                                                                                    this.input );

                        throw nvae;
                }

                switch ( alt17 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:381:25: {...}? expander_lhs_block[lhs]
                    {
                        if ( !(this.expander != null) ) {
                            throw new FailedPredicateException( this.input,
                                                                "query",
                                                                " expander != null " );
                        }
                        this.following.push( RuleParser.FOLLOW_expander_lhs_block_in_query550 );
                        expander_lhs_block( lhs );
                        this.following.pop();

                    }
                        break;
                    case 2 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:382:27: normal_lhs_block[lhs]
                    {
                        this.following.push( RuleParser.FOLLOW_normal_lhs_block_in_query558 );
                        normal_lhs_block( lhs );
                        this.following.pop();

                    }
                        break;

                }

                match( this.input,
                       29,
                       RuleParser.FOLLOW_29_in_query573 );
                this.following.push( RuleParser.FOLLOW_opt_eol_in_query575 );
                opt_eol();
                this.following.pop();

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

    // $ANTLR start rule
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:388:1: rule returns [RuleDescr rule] : opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? opt_eol ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* )? )? 'end' opt_eol ;
    public RuleDescr rule() throws RecognitionException {
        RuleDescr rule;
        Token loc = null;
        Token any = null;
        String ruleName = null;

        rule = null;
        String consequence = "";

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:394:17: ( opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? opt_eol ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* )? )? 'end' opt_eol )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:394:17: opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? opt_eol ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* )? )? 'end' opt_eol
            {
                this.following.push( RuleParser.FOLLOW_opt_eol_in_rule598 );
                opt_eol();
                this.following.pop();

                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       30,
                       RuleParser.FOLLOW_30_in_rule604 );
                this.following.push( RuleParser.FOLLOW_word_in_rule608 );
                ruleName = word();
                this.following.pop();

                this.following.push( RuleParser.FOLLOW_opt_eol_in_rule610 );
                opt_eol();
                this.following.pop();

                debug( "start rule: " + ruleName );
                rule = new RuleDescr( ruleName,
                                      null );
                rule.setLocation( offset( loc.getLine() ),
                                  loc.getCharPositionInLine() );

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:401:17: ( rule_attributes[rule] )?
                int alt18 = 2;
                switch ( this.input.LA( 1 ) ) {
                    case 32 :
                    case 34 :
                        alt18 = 1;
                        break;
                    case EOL :
                    case 15 :
                    case 24 :
                    case 35 :
                    case 36 :
                    case 37 :
                    case 38 :
                    case 39 :
                    case 40 :
                        alt18 = 1;
                        break;
                    case 31 :
                        alt18 = 1;
                        break;
                    case 33 :
                        alt18 = 1;
                        break;
                    case 29 :
                        alt18 = 1;
                        break;
                    default :
                        final NoViableAltException nvae = new NoViableAltException( "401:17: ( rule_attributes[rule] )?",
                                                                                    18,
                                                                                    0,
                                                                                    this.input );

                        throw nvae;
                }

                switch ( alt18 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:401:25: rule_attributes[rule]
                    {
                        this.following.push( RuleParser.FOLLOW_rule_attributes_in_rule621 );
                        rule_attributes( rule );
                        this.following.pop();

                    }
                        break;

                }

                this.following.push( RuleParser.FOLLOW_opt_eol_in_rule631 );
                opt_eol();
                this.following.pop();

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:404:17: ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* )? )?
                int alt25 = 2;
                final int LA25_0 = this.input.LA( 1 );
                if ( LA25_0 == RuleParser.EOL || LA25_0 == 15 || LA25_0 == 31 || LA25_0 == 33 ) {
                    alt25 = 1;
                } else if ( LA25_0 == 29 ) {
                    alt25 = 1;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "404:17: ( (loc= \'when\' ( \':\' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= \'then\' ( \':\' )? opt_eol ( options {greedy=false; } : any= . )* )? )?",
                                                                                25,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt25 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:404:18: (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* )?
                    {
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:404:18: (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?
                        int alt21 = 2;
                        final int LA21_0 = this.input.LA( 1 );
                        if ( LA21_0 == 31 ) {
                            alt21 = 1;
                        } else if ( LA21_0 == RuleParser.EOL || LA21_0 == 15 || LA21_0 == 29 || LA21_0 == 33 ) {
                            alt21 = 2;
                        } else {
                            final NoViableAltException nvae = new NoViableAltException( "404:18: (loc= \'when\' ( \':\' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?",
                                                                                        21,
                                                                                        0,
                                                                                        this.input );

                            throw nvae;
                        }
                        switch ( alt21 ) {
                            case 1 :
                                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:404:25: loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                            {
                                loc = (Token) this.input.LT( 1 );
                                match( this.input,
                                       31,
                                       RuleParser.FOLLOW_31_in_rule640 );
                                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:404:36: ( ':' )?
                                int alt19 = 2;
                                final int LA19_0 = this.input.LA( 1 );
                                if ( LA19_0 == 32 ) {
                                    final int LA19_1 = this.input.LA( 2 );
                                    if ( !(this.expander != null) ) {
                                        alt19 = 1;
                                    } else if ( this.expander != null ) {
                                        alt19 = 2;
                                    } else {
                                        final NoViableAltException nvae = new NoViableAltException( "404:36: ( \':\' )?",
                                                                                                    19,
                                                                                                    1,
                                                                                                    this.input );

                                        throw nvae;
                                    }
                                } else if ( (LA19_0 >= RuleParser.EOL && LA19_0 <= 31) || (LA19_0 >= 33 && LA19_0 <= 61) ) {
                                    alt19 = 2;
                                } else {
                                    final NoViableAltException nvae = new NoViableAltException( "404:36: ( \':\' )?",
                                                                                                19,
                                                                                                0,
                                                                                                this.input );

                                    throw nvae;
                                }
                                switch ( alt19 ) {
                                    case 1 :
                                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:404:36: ':'
                                    {
                                        match( this.input,
                                               32,
                                               RuleParser.FOLLOW_32_in_rule642 );

                                    }
                                        break;

                                }

                                this.following.push( RuleParser.FOLLOW_opt_eol_in_rule645 );
                                opt_eol();
                                this.following.pop();

                                final AndDescr lhs = new AndDescr();
                                rule.setLhs( lhs );
                                lhs.setLocation( offset( loc.getLine() ),
                                                 loc.getCharPositionInLine() );

                                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:409:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                                int alt20 = 2;
                                switch ( this.input.LA( 1 ) ) {
                                    case 23 :
                                        final int LA20_1 = this.input.LA( 2 );
                                        if ( this.expander != null ) {
                                            alt20 = 1;
                                        } else if ( true ) {
                                            alt20 = 2;
                                        } else {
                                            final NoViableAltException nvae = new NoViableAltException( "409:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                                        20,
                                                                                                        1,
                                                                                                        this.input );

                                            throw nvae;
                                        }
                                        break;
                                    case EOL :
                                        final int LA20_2 = this.input.LA( 2 );
                                        if ( this.expander != null ) {
                                            alt20 = 1;
                                        } else if ( true ) {
                                            alt20 = 2;
                                        } else {
                                            final NoViableAltException nvae = new NoViableAltException( "409:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                                        20,
                                                                                                        2,
                                                                                                        this.input );

                                            throw nvae;
                                        }
                                        break;
                                    case 15 :
                                        final int LA20_3 = this.input.LA( 2 );
                                        if ( this.expander != null ) {
                                            alt20 = 1;
                                        } else if ( true ) {
                                            alt20 = 2;
                                        } else {
                                            final NoViableAltException nvae = new NoViableAltException( "409:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                                        20,
                                                                                                        3,
                                                                                                        this.input );

                                            throw nvae;
                                        }
                                        break;
                                    case 33 :
                                        final int LA20_4 = this.input.LA( 2 );
                                        if ( this.expander != null ) {
                                            alt20 = 1;
                                        } else if ( true ) {
                                            alt20 = 2;
                                        } else {
                                            final NoViableAltException nvae = new NoViableAltException( "409:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                                        20,
                                                                                                        4,
                                                                                                        this.input );

                                            throw nvae;
                                        }
                                        break;
                                    case 29 :
                                        final int LA20_5 = this.input.LA( 2 );
                                        if ( this.expander != null ) {
                                            alt20 = 1;
                                        } else if ( true ) {
                                            alt20 = 2;
                                        } else {
                                            final NoViableAltException nvae = new NoViableAltException( "409:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                                        20,
                                                                                                        5,
                                                                                                        this.input );

                                            throw nvae;
                                        }
                                        break;
                                    case 56 :
                                        final int LA20_6 = this.input.LA( 2 );
                                        if ( this.expander != null ) {
                                            alt20 = 1;
                                        } else if ( true ) {
                                            alt20 = 2;
                                        } else {
                                            final NoViableAltException nvae = new NoViableAltException( "409:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                                        20,
                                                                                                        6,
                                                                                                        this.input );

                                            throw nvae;
                                        }
                                        break;
                                    case 57 :
                                        final int LA20_7 = this.input.LA( 2 );
                                        if ( this.expander != null ) {
                                            alt20 = 1;
                                        } else if ( true ) {
                                            alt20 = 2;
                                        } else {
                                            final NoViableAltException nvae = new NoViableAltException( "409:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                                        20,
                                                                                                        7,
                                                                                                        this.input );

                                            throw nvae;
                                        }
                                        break;
                                    case 58 :
                                        final int LA20_8 = this.input.LA( 2 );
                                        if ( this.expander != null ) {
                                            alt20 = 1;
                                        } else if ( true ) {
                                            alt20 = 2;
                                        } else {
                                            final NoViableAltException nvae = new NoViableAltException( "409:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                                        20,
                                                                                                        8,
                                                                                                        this.input );

                                            throw nvae;
                                        }
                                        break;
                                    case ID :
                                        final int LA20_9 = this.input.LA( 2 );
                                        if ( this.expander != null ) {
                                            alt20 = 1;
                                        } else if ( true ) {
                                            alt20 = 2;
                                        } else {
                                            final NoViableAltException nvae = new NoViableAltException( "409:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                                        20,
                                                                                                        9,
                                                                                                        this.input );

                                            throw nvae;
                                        }
                                        break;
                                    case INT :
                                    case BOOL :
                                    case STRING :
                                    case FLOAT :
                                    case MISC :
                                    case WS :
                                    case SH_STYLE_SINGLE_LINE_COMMENT :
                                    case C_STYLE_SINGLE_LINE_COMMENT :
                                    case MULTI_LINE_COMMENT :
                                    case 16 :
                                    case 17 :
                                    case 18 :
                                    case 19 :
                                    case 20 :
                                    case 21 :
                                    case 22 :
                                    case 24 :
                                    case 25 :
                                    case 26 :
                                    case 27 :
                                    case 28 :
                                    case 30 :
                                    case 31 :
                                    case 32 :
                                    case 34 :
                                    case 35 :
                                    case 36 :
                                    case 37 :
                                    case 38 :
                                    case 39 :
                                    case 40 :
                                    case 41 :
                                    case 42 :
                                    case 43 :
                                    case 44 :
                                    case 45 :
                                    case 46 :
                                    case 47 :
                                    case 48 :
                                    case 49 :
                                    case 50 :
                                    case 51 :
                                    case 52 :
                                    case 53 :
                                    case 54 :
                                    case 55 :
                                    case 59 :
                                    case 60 :
                                    case 61 :
                                        alt20 = 1;
                                        break;
                                    default :
                                        final NoViableAltException nvae = new NoViableAltException( "409:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )",
                                                                                                    20,
                                                                                                    0,
                                                                                                    this.input );

                                        throw nvae;
                                }

                                switch ( alt20 ) {
                                    case 1 :
                                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:410:33: {...}? expander_lhs_block[lhs]
                                    {
                                        if ( !(this.expander != null) ) {
                                            throw new FailedPredicateException( this.input,
                                                                                "rule",
                                                                                " expander != null " );
                                        }
                                        this.following.push( RuleParser.FOLLOW_expander_lhs_block_in_rule663 );
                                        expander_lhs_block( lhs );
                                        this.following.pop();

                                    }
                                        break;
                                    case 2 :
                                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:411:35: normal_lhs_block[lhs]
                                    {
                                        this.following.push( RuleParser.FOLLOW_normal_lhs_block_in_rule672 );
                                        normal_lhs_block( lhs );
                                        this.following.pop();

                                    }
                                        break;

                                }

                            }
                                break;

                        }

                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:415:17: ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* )?
                        int alt24 = 2;
                        final int LA24_0 = this.input.LA( 1 );
                        if ( LA24_0 == RuleParser.EOL || LA24_0 == 15 || LA24_0 == 33 ) {
                            alt24 = 1;
                        } else if ( LA24_0 == 29 ) {
                            alt24 = 2;
                        } else {
                            final NoViableAltException nvae = new NoViableAltException( "415:17: ( opt_eol loc= \'then\' ( \':\' )? opt_eol ( options {greedy=false; } : any= . )* )?",
                                                                                        24,
                                                                                        0,
                                                                                        this.input );

                            throw nvae;
                        }
                        switch ( alt24 ) {
                            case 1 :
                                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:415:19: opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )*
                            {
                                this.following.push( RuleParser.FOLLOW_opt_eol_in_rule695 );
                                opt_eol();
                                this.following.pop();

                                loc = (Token) this.input.LT( 1 );
                                match( this.input,
                                       33,
                                       RuleParser.FOLLOW_33_in_rule699 );
                                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:415:38: ( ':' )?
                                int alt22 = 2;
                                final int LA22_0 = this.input.LA( 1 );
                                if ( LA22_0 == 32 ) {
                                    alt22 = 1;
                                } else if ( (LA22_0 >= RuleParser.EOL && LA22_0 <= 31) || (LA22_0 >= 33 && LA22_0 <= 61) ) {
                                    alt22 = 2;
                                } else {
                                    final NoViableAltException nvae = new NoViableAltException( "415:38: ( \':\' )?",
                                                                                                22,
                                                                                                0,
                                                                                                this.input );

                                    throw nvae;
                                }
                                switch ( alt22 ) {
                                    case 1 :
                                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:415:38: ':'
                                    {
                                        match( this.input,
                                               32,
                                               RuleParser.FOLLOW_32_in_rule701 );

                                    }
                                        break;

                                }

                                this.following.push( RuleParser.FOLLOW_opt_eol_in_rule705 );
                                opt_eol();
                                this.following.pop();

                                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:416:25: ( options {greedy=false; } : any= . )*
                                loop23 : do {
                                    int alt23 = 2;
                                    final int LA23_0 = this.input.LA( 1 );
                                    if ( LA23_0 == 29 ) {
                                        alt23 = 2;
                                    } else if ( (LA23_0 >= RuleParser.EOL && LA23_0 <= 28) || (LA23_0 >= 30 && LA23_0 <= 61) ) {
                                        alt23 = 1;
                                    }

                                    switch ( alt23 ) {
                                        case 1 :
                                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:416:52: any= .
                                        {
                                            any = (Token) this.input.LT( 1 );
                                            matchAny( this.input );

                                            consequence = consequence + " " + any.getText();

                                        }
                                            break;

                                        default :
                                            break loop23;
                                    }
                                } while ( true );

                                if ( this.expander != null ) {
                                    final String expanded = runThenExpander( consequence,
                                                                             offset( loc.getLine() ) );
                                    rule.setConsequence( expanded );
                                } else {
                                    rule.setConsequence( consequence );
                                }
                                rule.setConsequenceLocation( offset( loc.getLine() ),
                                                             loc.getCharPositionInLine() );

                            }
                                break;

                        }

                    }
                        break;

                }

                match( this.input,
                       29,
                       RuleParser.FOLLOW_29_in_rule751 );
                this.following.push( RuleParser.FOLLOW_opt_eol_in_rule753 );
                opt_eol();
                this.following.pop();

                debug( "end rule: " + ruleName );

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

    // $ANTLR start extra_statement
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:437:1: extra_statement : ( import_statement | global | function ) ;
    public void extra_statement() throws RecognitionException {
        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:439:9: ( ( import_statement | global | function ) )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:439:9: ( import_statement | global | function )
            {
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:439:9: ( import_statement | global | function )
                int alt26 = 3;
                switch ( this.input.LA( 1 ) ) {
                    case 17 :
                        alt26 = 1;
                        break;
                    case 21 :
                        alt26 = 2;
                        break;
                    case 22 :
                        alt26 = 3;
                        break;
                    default :
                        final NoViableAltException nvae = new NoViableAltException( "439:9: ( import_statement | global | function )",
                                                                                    26,
                                                                                    0,
                                                                                    this.input );

                        throw nvae;
                }

                switch ( alt26 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:439:17: import_statement
                    {
                        this.following.push( RuleParser.FOLLOW_import_statement_in_extra_statement773 );
                        import_statement();
                        this.following.pop();

                    }
                        break;
                    case 2 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:440:17: global
                    {
                        this.following.push( RuleParser.FOLLOW_global_in_extra_statement778 );
                        global();
                        this.following.pop();

                    }
                        break;
                    case 3 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:441:17: function
                    {
                        this.following.push( RuleParser.FOLLOW_function_in_extra_statement783 );
                        function();
                        this.following.pop();

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

    // $ANTLR end extra_statement

    // $ANTLR start rule_attributes
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:445:1: rule_attributes[RuleDescr rule] : ( 'attributes' )? ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )* ;
    public void rule_attributes(final RuleDescr rule) throws RecognitionException {
        AttributeDescr a = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:447:25: ( ( 'attributes' )? ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )* )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:447:25: ( 'attributes' )? ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )*
            {
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:447:25: ( 'attributes' )?
                int alt27 = 2;
                final int LA27_0 = this.input.LA( 1 );
                if ( LA27_0 == 34 ) {
                    alt27 = 1;
                } else if ( LA27_0 == RuleParser.EOL || LA27_0 == 15 || LA27_0 == 24 || LA27_0 == 29 || (LA27_0 >= 31 && LA27_0 <= 33) || (LA27_0 >= 35 && LA27_0 <= 40) ) {
                    alt27 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "447:25: ( \'attributes\' )?",
                                                                                27,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt27 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:447:25: 'attributes'
                    {
                        match( this.input,
                               34,
                               RuleParser.FOLLOW_34_in_rule_attributes802 );

                    }
                        break;

                }

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:447:39: ( ':' )?
                int alt28 = 2;
                final int LA28_0 = this.input.LA( 1 );
                if ( LA28_0 == 32 ) {
                    alt28 = 1;
                } else if ( LA28_0 == RuleParser.EOL || LA28_0 == 15 || LA28_0 == 24 || LA28_0 == 29 || LA28_0 == 31 || LA28_0 == 33 || (LA28_0 >= 35 && LA28_0 <= 40) ) {
                    alt28 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "447:39: ( \':\' )?",
                                                                                28,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt28 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:447:39: ':'
                    {
                        match( this.input,
                               32,
                               RuleParser.FOLLOW_32_in_rule_attributes805 );

                    }
                        break;

                }

                this.following.push( RuleParser.FOLLOW_opt_eol_in_rule_attributes808 );
                opt_eol();
                this.following.pop();

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:448:25: ( ( ',' )? a= rule_attribute opt_eol )*
                loop30 : do {
                    int alt30 = 2;
                    final int LA30_0 = this.input.LA( 1 );
                    if ( LA30_0 == 24 || (LA30_0 >= 35 && LA30_0 <= 40) ) {
                        alt30 = 1;
                    }

                    switch ( alt30 ) {
                        case 1 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:448:33: ( ',' )? a= rule_attribute opt_eol
                        {
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:448:33: ( ',' )?
                            int alt29 = 2;
                            final int LA29_0 = this.input.LA( 1 );
                            if ( LA29_0 == 24 ) {
                                alt29 = 1;
                            } else if ( (LA29_0 >= 35 && LA29_0 <= 40) ) {
                                alt29 = 2;
                            } else {
                                final NoViableAltException nvae = new NoViableAltException( "448:33: ( \',\' )?",
                                                                                            29,
                                                                                            0,
                                                                                            this.input );

                                throw nvae;
                            }
                            switch ( alt29 ) {
                                case 1 :
                                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:448:33: ','
                                {
                                    match( this.input,
                                           24,
                                           RuleParser.FOLLOW_24_in_rule_attributes815 );

                                }
                                    break;

                            }

                            this.following.push( RuleParser.FOLLOW_rule_attribute_in_rule_attributes820 );
                            a = rule_attribute();
                            this.following.pop();

                            this.following.push( RuleParser.FOLLOW_opt_eol_in_rule_attributes822 );
                            opt_eol();
                            this.following.pop();

                            rule.addAttribute( a );

                        }
                            break;

                        default :
                            break loop30;
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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:455:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus );
    public AttributeDescr rule_attribute() throws RecognitionException {
        AttributeDescr d;
        AttributeDescr a = null;

        d = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:460:25: (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus )
            int alt31 = 6;
            switch ( this.input.LA( 1 ) ) {
                case 35 :
                    alt31 = 1;
                    break;
                case 36 :
                    alt31 = 2;
                    break;
                case 39 :
                    alt31 = 3;
                    break;
                case 40 :
                    alt31 = 4;
                    break;
                case 38 :
                    alt31 = 5;
                    break;
                case 37 :
                    alt31 = 6;
                    break;
                default :
                    final NoViableAltException nvae = new NoViableAltException( "455:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus );",
                                                                                31,
                                                                                0,
                                                                                this.input );

                    throw nvae;
            }

            switch ( alt31 ) {
                case 1 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:460:25: a= salience
                {
                    this.following.push( RuleParser.FOLLOW_salience_in_rule_attribute861 );
                    a = salience();
                    this.following.pop();

                    d = a;

                }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:461:25: a= no_loop
                {
                    this.following.push( RuleParser.FOLLOW_no_loop_in_rule_attribute871 );
                    a = no_loop();
                    this.following.pop();

                    d = a;

                }
                    break;
                case 3 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:462:25: a= agenda_group
                {
                    this.following.push( RuleParser.FOLLOW_agenda_group_in_rule_attribute882 );
                    a = agenda_group();
                    this.following.pop();

                    d = a;

                }
                    break;
                case 4 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:463:25: a= duration
                {
                    this.following.push( RuleParser.FOLLOW_duration_in_rule_attribute895 );
                    a = duration();
                    this.following.pop();

                    d = a;

                }
                    break;
                case 5 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:464:25: a= activation_group
                {
                    this.following.push( RuleParser.FOLLOW_activation_group_in_rule_attribute909 );
                    a = activation_group();
                    this.following.pop();

                    d = a;

                }
                    break;
                case 6 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:465:25: a= auto_focus
                {
                    this.following.push( RuleParser.FOLLOW_auto_focus_in_rule_attribute920 );
                    a = auto_focus();
                    this.following.pop();

                    d = a;

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

    // $ANTLR start salience
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:469:1: salience returns [AttributeDescr d ] : loc= 'salience' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr salience() throws RecognitionException {
        AttributeDescr d;
        Token loc = null;
        Token i = null;

        d = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:474:17: (loc= 'salience' opt_eol i= INT ( ';' )? opt_eol )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:474:17: loc= 'salience' opt_eol i= INT ( ';' )? opt_eol
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       35,
                       RuleParser.FOLLOW_35_in_salience953 );
                this.following.push( RuleParser.FOLLOW_opt_eol_in_salience955 );
                opt_eol();
                this.following.pop();

                i = (Token) this.input.LT( 1 );
                match( this.input,
                       RuleParser.INT,
                       RuleParser.FOLLOW_INT_in_salience959 );
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:474:46: ( ';' )?
                int alt32 = 2;
                final int LA32_0 = this.input.LA( 1 );
                if ( LA32_0 == 15 ) {
                    alt32 = 1;
                } else if ( LA32_0 == RuleParser.EOL || LA32_0 == 24 || LA32_0 == 29 || LA32_0 == 31 || LA32_0 == 33 || (LA32_0 >= 35 && LA32_0 <= 40) ) {
                    alt32 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "474:46: ( \';\' )?",
                                                                                32,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt32 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:474:46: ';'
                    {
                        match( this.input,
                               15,
                               RuleParser.FOLLOW_15_in_salience961 );

                    }
                        break;

                }

                this.following.push( RuleParser.FOLLOW_opt_eol_in_salience964 );
                opt_eol();
                this.following.pop();

                d = new AttributeDescr( "salience",
                                        i.getText() );
                d.setLocation( offset( loc.getLine() ),
                               loc.getCharPositionInLine() );

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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:481:1: no_loop returns [AttributeDescr d] : ( (loc= 'no-loop' opt_eol ( ';' )? opt_eol ) | (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol ) );
    public AttributeDescr no_loop() throws RecognitionException {
        AttributeDescr d;
        Token loc = null;
        Token t = null;

        d = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:486:17: ( (loc= 'no-loop' opt_eol ( ';' )? opt_eol ) | (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol ) )
            int alt35 = 2;
            final int LA35_0 = this.input.LA( 1 );
            if ( LA35_0 == 36 ) {
                final int LA35_1 = this.input.LA( 2 );
                if ( LA35_1 == RuleParser.BOOL ) {
                    alt35 = 2;
                } else if ( LA35_1 == RuleParser.EOL || LA35_1 == 15 || LA35_1 == 24 || LA35_1 == 29 || LA35_1 == 31 || LA35_1 == 33 || (LA35_1 >= 35 && LA35_1 <= 40) ) {
                    alt35 = 1;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "481:1: no_loop returns [AttributeDescr d] : ( (loc= \'no-loop\' opt_eol ( \';\' )? opt_eol ) | (loc= \'no-loop\' t= BOOL opt_eol ( \';\' )? opt_eol ) );",
                                                                                35,
                                                                                1,
                                                                                this.input );

                    throw nvae;
                }
            } else {
                final NoViableAltException nvae = new NoViableAltException( "481:1: no_loop returns [AttributeDescr d] : ( (loc= \'no-loop\' opt_eol ( \';\' )? opt_eol ) | (loc= \'no-loop\' t= BOOL opt_eol ( \';\' )? opt_eol ) );",
                                                                            35,
                                                                            0,
                                                                            this.input );

                throw nvae;
            }
            switch ( alt35 ) {
                case 1 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:486:17: (loc= 'no-loop' opt_eol ( ';' )? opt_eol )
                {
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:486:17: (loc= 'no-loop' opt_eol ( ';' )? opt_eol )
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:487:25: loc= 'no-loop' opt_eol ( ';' )? opt_eol
                    {
                        loc = (Token) this.input.LT( 1 );
                        match( this.input,
                               36,
                               RuleParser.FOLLOW_36_in_no_loop999 );
                        this.following.push( RuleParser.FOLLOW_opt_eol_in_no_loop1001 );
                        opt_eol();
                        this.following.pop();

                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:487:47: ( ';' )?
                        int alt33 = 2;
                        final int LA33_0 = this.input.LA( 1 );
                        if ( LA33_0 == 15 ) {
                            alt33 = 1;
                        } else if ( LA33_0 == RuleParser.EOL || LA33_0 == 24 || LA33_0 == 29 || LA33_0 == 31 || LA33_0 == 33 || (LA33_0 >= 35 && LA33_0 <= 40) ) {
                            alt33 = 2;
                        } else {
                            final NoViableAltException nvae = new NoViableAltException( "487:47: ( \';\' )?",
                                                                                        33,
                                                                                        0,
                                                                                        this.input );

                            throw nvae;
                        }
                        switch ( alt33 ) {
                            case 1 :
                                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:487:47: ';'
                            {
                                match( this.input,
                                       15,
                                       RuleParser.FOLLOW_15_in_no_loop1003 );

                            }
                                break;

                        }

                        this.following.push( RuleParser.FOLLOW_opt_eol_in_no_loop1006 );
                        opt_eol();
                        this.following.pop();

                        d = new AttributeDescr( "no-loop",
                                                "true" );
                        d.setLocation( offset( loc.getLine() ),
                                       loc.getCharPositionInLine() );

                    }

                }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:494:17: (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol )
                {
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:494:17: (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol )
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:495:25: loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol
                    {
                        loc = (Token) this.input.LT( 1 );
                        match( this.input,
                               36,
                               RuleParser.FOLLOW_36_in_no_loop1031 );
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               RuleParser.BOOL,
                               RuleParser.FOLLOW_BOOL_in_no_loop1035 );
                        this.following.push( RuleParser.FOLLOW_opt_eol_in_no_loop1037 );
                        opt_eol();
                        this.following.pop();

                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:495:54: ( ';' )?
                        int alt34 = 2;
                        final int LA34_0 = this.input.LA( 1 );
                        if ( LA34_0 == 15 ) {
                            alt34 = 1;
                        } else if ( LA34_0 == RuleParser.EOL || LA34_0 == 24 || LA34_0 == 29 || LA34_0 == 31 || LA34_0 == 33 || (LA34_0 >= 35 && LA34_0 <= 40) ) {
                            alt34 = 2;
                        } else {
                            final NoViableAltException nvae = new NoViableAltException( "495:54: ( \';\' )?",
                                                                                        34,
                                                                                        0,
                                                                                        this.input );

                            throw nvae;
                        }
                        switch ( alt34 ) {
                            case 1 :
                                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:495:54: ';'
                            {
                                match( this.input,
                                       15,
                                       RuleParser.FOLLOW_15_in_no_loop1039 );

                            }
                                break;

                        }

                        this.following.push( RuleParser.FOLLOW_opt_eol_in_no_loop1042 );
                        opt_eol();
                        this.following.pop();

                        d = new AttributeDescr( "no-loop",
                                                t.getText() );
                        d.setLocation( offset( loc.getLine() ),
                                       loc.getCharPositionInLine() );

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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:505:1: auto_focus returns [AttributeDescr d] : ( (loc= 'auto-focus' opt_eol ( ';' )? opt_eol ) | (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol ) );
    public AttributeDescr auto_focus() throws RecognitionException {
        AttributeDescr d;
        Token loc = null;
        Token t = null;

        d = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:510:17: ( (loc= 'auto-focus' opt_eol ( ';' )? opt_eol ) | (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol ) )
            int alt38 = 2;
            final int LA38_0 = this.input.LA( 1 );
            if ( LA38_0 == 37 ) {
                final int LA38_1 = this.input.LA( 2 );
                if ( LA38_1 == RuleParser.BOOL ) {
                    alt38 = 2;
                } else if ( LA38_1 == RuleParser.EOL || LA38_1 == 15 || LA38_1 == 24 || LA38_1 == 29 || LA38_1 == 31 || LA38_1 == 33 || (LA38_1 >= 35 && LA38_1 <= 40) ) {
                    alt38 = 1;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "505:1: auto_focus returns [AttributeDescr d] : ( (loc= \'auto-focus\' opt_eol ( \';\' )? opt_eol ) | (loc= \'auto-focus\' t= BOOL opt_eol ( \';\' )? opt_eol ) );",
                                                                                38,
                                                                                1,
                                                                                this.input );

                    throw nvae;
                }
            } else {
                final NoViableAltException nvae = new NoViableAltException( "505:1: auto_focus returns [AttributeDescr d] : ( (loc= \'auto-focus\' opt_eol ( \';\' )? opt_eol ) | (loc= \'auto-focus\' t= BOOL opt_eol ( \';\' )? opt_eol ) );",
                                                                            38,
                                                                            0,
                                                                            this.input );

                throw nvae;
            }
            switch ( alt38 ) {
                case 1 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:510:17: (loc= 'auto-focus' opt_eol ( ';' )? opt_eol )
                {
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:510:17: (loc= 'auto-focus' opt_eol ( ';' )? opt_eol )
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:511:25: loc= 'auto-focus' opt_eol ( ';' )? opt_eol
                    {
                        loc = (Token) this.input.LT( 1 );
                        match( this.input,
                               37,
                               RuleParser.FOLLOW_37_in_auto_focus1088 );
                        this.following.push( RuleParser.FOLLOW_opt_eol_in_auto_focus1090 );
                        opt_eol();
                        this.following.pop();

                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:511:50: ( ';' )?
                        int alt36 = 2;
                        final int LA36_0 = this.input.LA( 1 );
                        if ( LA36_0 == 15 ) {
                            alt36 = 1;
                        } else if ( LA36_0 == RuleParser.EOL || LA36_0 == 24 || LA36_0 == 29 || LA36_0 == 31 || LA36_0 == 33 || (LA36_0 >= 35 && LA36_0 <= 40) ) {
                            alt36 = 2;
                        } else {
                            final NoViableAltException nvae = new NoViableAltException( "511:50: ( \';\' )?",
                                                                                        36,
                                                                                        0,
                                                                                        this.input );

                            throw nvae;
                        }
                        switch ( alt36 ) {
                            case 1 :
                                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:511:50: ';'
                            {
                                match( this.input,
                                       15,
                                       RuleParser.FOLLOW_15_in_auto_focus1092 );

                            }
                                break;

                        }

                        this.following.push( RuleParser.FOLLOW_opt_eol_in_auto_focus1095 );
                        opt_eol();
                        this.following.pop();

                        d = new AttributeDescr( "auto-focus",
                                                "true" );
                        d.setLocation( offset( loc.getLine() ),
                                       loc.getCharPositionInLine() );

                    }

                }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:518:17: (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol )
                {
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:518:17: (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol )
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:519:25: loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol
                    {
                        loc = (Token) this.input.LT( 1 );
                        match( this.input,
                               37,
                               RuleParser.FOLLOW_37_in_auto_focus1120 );
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               RuleParser.BOOL,
                               RuleParser.FOLLOW_BOOL_in_auto_focus1124 );
                        this.following.push( RuleParser.FOLLOW_opt_eol_in_auto_focus1126 );
                        opt_eol();
                        this.following.pop();

                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:519:57: ( ';' )?
                        int alt37 = 2;
                        final int LA37_0 = this.input.LA( 1 );
                        if ( LA37_0 == 15 ) {
                            alt37 = 1;
                        } else if ( LA37_0 == RuleParser.EOL || LA37_0 == 24 || LA37_0 == 29 || LA37_0 == 31 || LA37_0 == 33 || (LA37_0 >= 35 && LA37_0 <= 40) ) {
                            alt37 = 2;
                        } else {
                            final NoViableAltException nvae = new NoViableAltException( "519:57: ( \';\' )?",
                                                                                        37,
                                                                                        0,
                                                                                        this.input );

                            throw nvae;
                        }
                        switch ( alt37 ) {
                            case 1 :
                                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:519:57: ';'
                            {
                                match( this.input,
                                       15,
                                       RuleParser.FOLLOW_15_in_auto_focus1128 );

                            }
                                break;

                        }

                        this.following.push( RuleParser.FOLLOW_opt_eol_in_auto_focus1131 );
                        opt_eol();
                        this.following.pop();

                        d = new AttributeDescr( "auto-focus",
                                                t.getText() );
                        d.setLocation( offset( loc.getLine() ),
                                       loc.getCharPositionInLine() );

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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:529:1: activation_group returns [AttributeDescr d] : loc= 'activation-group' opt_eol name= STRING ( ';' )? opt_eol ;
    public AttributeDescr activation_group() throws RecognitionException {
        AttributeDescr d;
        Token loc = null;
        Token name = null;

        d = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:534:17: (loc= 'activation-group' opt_eol name= STRING ( ';' )? opt_eol )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:534:17: loc= 'activation-group' opt_eol name= STRING ( ';' )? opt_eol
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       38,
                       RuleParser.FOLLOW_38_in_activation_group1173 );
                this.following.push( RuleParser.FOLLOW_opt_eol_in_activation_group1175 );
                opt_eol();
                this.following.pop();

                name = (Token) this.input.LT( 1 );
                match( this.input,
                       RuleParser.STRING,
                       RuleParser.FOLLOW_STRING_in_activation_group1179 );
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:534:60: ( ';' )?
                int alt39 = 2;
                final int LA39_0 = this.input.LA( 1 );
                if ( LA39_0 == 15 ) {
                    alt39 = 1;
                } else if ( LA39_0 == RuleParser.EOL || LA39_0 == 24 || LA39_0 == 29 || LA39_0 == 31 || LA39_0 == 33 || (LA39_0 >= 35 && LA39_0 <= 40) ) {
                    alt39 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "534:60: ( \';\' )?",
                                                                                39,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt39 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:534:60: ';'
                    {
                        match( this.input,
                               15,
                               RuleParser.FOLLOW_15_in_activation_group1181 );

                    }
                        break;

                }

                this.following.push( RuleParser.FOLLOW_opt_eol_in_activation_group1184 );
                opt_eol();
                this.following.pop();

                d = new AttributeDescr( "activation-group",
                                        getString( name ) );
                d.setLocation( offset( loc.getLine() ),
                               loc.getCharPositionInLine() );

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

    // $ANTLR start agenda_group
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:541:1: agenda_group returns [AttributeDescr d] : loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol ;
    public AttributeDescr agenda_group() throws RecognitionException {
        AttributeDescr d;
        Token loc = null;
        Token name = null;

        d = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:546:17: (loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:546:17: loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       39,
                       RuleParser.FOLLOW_39_in_agenda_group1213 );
                this.following.push( RuleParser.FOLLOW_opt_eol_in_agenda_group1215 );
                opt_eol();
                this.following.pop();

                name = (Token) this.input.LT( 1 );
                match( this.input,
                       RuleParser.STRING,
                       RuleParser.FOLLOW_STRING_in_agenda_group1219 );
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:546:56: ( ';' )?
                int alt40 = 2;
                final int LA40_0 = this.input.LA( 1 );
                if ( LA40_0 == 15 ) {
                    alt40 = 1;
                } else if ( LA40_0 == RuleParser.EOL || LA40_0 == 24 || LA40_0 == 29 || LA40_0 == 31 || LA40_0 == 33 || (LA40_0 >= 35 && LA40_0 <= 40) ) {
                    alt40 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "546:56: ( \';\' )?",
                                                                                40,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt40 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:546:56: ';'
                    {
                        match( this.input,
                               15,
                               RuleParser.FOLLOW_15_in_agenda_group1221 );

                    }
                        break;

                }

                this.following.push( RuleParser.FOLLOW_opt_eol_in_agenda_group1224 );
                opt_eol();
                this.following.pop();

                d = new AttributeDescr( "agenda-group",
                                        getString( name ) );
                d.setLocation( offset( loc.getLine() ),
                               loc.getCharPositionInLine() );

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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:554:1: duration returns [AttributeDescr d] : loc= 'duration' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr duration() throws RecognitionException {
        AttributeDescr d;
        Token loc = null;
        Token i = null;

        d = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:559:17: (loc= 'duration' opt_eol i= INT ( ';' )? opt_eol )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:559:17: loc= 'duration' opt_eol i= INT ( ';' )? opt_eol
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       40,
                       RuleParser.FOLLOW_40_in_duration1256 );
                this.following.push( RuleParser.FOLLOW_opt_eol_in_duration1258 );
                opt_eol();
                this.following.pop();

                i = (Token) this.input.LT( 1 );
                match( this.input,
                       RuleParser.INT,
                       RuleParser.FOLLOW_INT_in_duration1262 );
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:559:46: ( ';' )?
                int alt41 = 2;
                final int LA41_0 = this.input.LA( 1 );
                if ( LA41_0 == 15 ) {
                    alt41 = 1;
                } else if ( LA41_0 == RuleParser.EOL || LA41_0 == 24 || LA41_0 == 29 || LA41_0 == 31 || LA41_0 == 33 || (LA41_0 >= 35 && LA41_0 <= 40) ) {
                    alt41 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "559:46: ( \';\' )?",
                                                                                41,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt41 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:559:46: ';'
                    {
                        match( this.input,
                               15,
                               RuleParser.FOLLOW_15_in_duration1264 );

                    }
                        break;

                }

                this.following.push( RuleParser.FOLLOW_opt_eol_in_duration1267 );
                opt_eol();
                this.following.pop();

                d = new AttributeDescr( "duration",
                                        i.getText() );
                d.setLocation( offset( loc.getLine() ),
                               loc.getCharPositionInLine() );

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

    // $ANTLR start normal_lhs_block
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:567:1: normal_lhs_block[AndDescr descrs] : (d= lhs opt_eol )* opt_eol ;
    public void normal_lhs_block(final AndDescr descrs) throws RecognitionException {
        PatternDescr d = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:569:17: ( (d= lhs opt_eol )* opt_eol )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:569:17: (d= lhs opt_eol )* opt_eol
            {
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:569:17: (d= lhs opt_eol )*
                loop42 : do {
                    int alt42 = 2;
                    final int LA42_0 = this.input.LA( 1 );
                    if ( LA42_0 == RuleParser.ID || LA42_0 == 23 || (LA42_0 >= 56 && LA42_0 <= 58) ) {
                        alt42 = 1;
                    }

                    switch ( alt42 ) {
                        case 1 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:569:25: d= lhs opt_eol
                        {
                            this.following.push( RuleParser.FOLLOW_lhs_in_normal_lhs_block1293 );
                            d = lhs();
                            this.following.pop();

                            this.following.push( RuleParser.FOLLOW_opt_eol_in_normal_lhs_block1295 );
                            opt_eol();
                            this.following.pop();

                            descrs.addDescr( d );

                        }
                            break;

                        default :
                            break loop42;
                    }
                } while ( true );

                this.following.push( RuleParser.FOLLOW_opt_eol_in_normal_lhs_block1307 );
                opt_eol();
                this.following.pop();

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

    // $ANTLR start expander_lhs_block
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:577:1: expander_lhs_block[AndDescr descrs] : ( options {greedy=false; } : text= paren_chunk loc= EOL )* ;
    public void expander_lhs_block(final AndDescr descrs) throws RecognitionException {
        Token loc = null;
        String text = null;

        String lhsBlock = null;
        final String eol = System.getProperty( "line.separator" );

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:583:17: ( ( options {greedy=false; } : text= paren_chunk loc= EOL )* )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:583:17: ( options {greedy=false; } : text= paren_chunk loc= EOL )*
            {
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:583:17: ( options {greedy=false; } : text= paren_chunk loc= EOL )*
                loop43 : do {
                    int alt43 = 2;
                    switch ( this.input.LA( 1 ) ) {
                        case 29 :
                            alt43 = 2;
                            break;
                        case EOL :
                            alt43 = 2;
                            break;
                        case 33 :
                            alt43 = 2;
                            break;
                        case ID :
                        case INT :
                        case BOOL :
                        case STRING :
                        case FLOAT :
                        case MISC :
                        case WS :
                        case SH_STYLE_SINGLE_LINE_COMMENT :
                        case C_STYLE_SINGLE_LINE_COMMENT :
                        case MULTI_LINE_COMMENT :
                        case 16 :
                        case 17 :
                        case 18 :
                        case 19 :
                        case 20 :
                        case 21 :
                        case 22 :
                        case 23 :
                        case 24 :
                        case 25 :
                        case 26 :
                        case 27 :
                        case 28 :
                        case 30 :
                        case 31 :
                        case 32 :
                        case 34 :
                        case 35 :
                        case 36 :
                        case 37 :
                        case 38 :
                        case 39 :
                        case 40 :
                        case 41 :
                        case 42 :
                        case 43 :
                        case 44 :
                        case 45 :
                        case 46 :
                        case 47 :
                        case 48 :
                        case 49 :
                        case 50 :
                        case 51 :
                        case 52 :
                        case 53 :
                        case 54 :
                        case 55 :
                        case 56 :
                        case 57 :
                        case 58 :
                        case 59 :
                        case 60 :
                        case 61 :
                            alt43 = 1;
                            break;
                        case 15 :
                            alt43 = 2;
                            break;

                    }

                    switch ( alt43 ) {
                        case 1 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:584:25: text= paren_chunk loc= EOL
                        {
                            this.following.push( RuleParser.FOLLOW_paren_chunk_in_expander_lhs_block1348 );
                            text = paren_chunk();
                            this.following.pop();

                            loc = (Token) this.input.LT( 1 );
                            match( this.input,
                                   RuleParser.EOL,
                                   RuleParser.FOLLOW_EOL_in_expander_lhs_block1352 );

                            //only expand non null
                            if ( text != null ) {
                                if ( lhsBlock == null ) {
                                    lhsBlock = runWhenExpander( text,
                                                                offset( loc.getLine() ) );
                                } else {
                                    lhsBlock = lhsBlock + eol + runWhenExpander( text,
                                                                                 offset( loc.getLine() ) );
                                }
                                text = null;
                            }

                        }
                            break;

                        default :
                            break loop43;
                    }
                } while ( true );

                if ( lhsBlock != null ) {
                    reparseLhs( lhsBlock,
                                descrs );
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

    // $ANTLR end expander_lhs_block

    // $ANTLR start lhs
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:609:1: lhs returns [PatternDescr d] : l= lhs_or ;
    public PatternDescr lhs() throws RecognitionException {
        PatternDescr d;
        PatternDescr l = null;

        d = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:613:17: (l= lhs_or )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:613:17: l= lhs_or
            {
                this.following.push( RuleParser.FOLLOW_lhs_or_in_lhs1404 );
                l = lhs_or();
                this.following.pop();

                d = l;

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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:617:1: lhs_column returns [PatternDescr d] : (f= fact_binding | f= fact );
    public PatternDescr lhs_column() throws RecognitionException {
        PatternDescr d;
        PatternDescr f = null;

        d = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:621:17: (f= fact_binding | f= fact )
            int alt44 = 2;
            alt44 = this.dfa44.predict( this.input );
            switch ( alt44 ) {
                case 1 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:621:17: f= fact_binding
                {
                    this.following.push( RuleParser.FOLLOW_fact_binding_in_lhs_column1432 );
                    f = fact_binding();
                    this.following.pop();

                    d = f;

                }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:622:17: f= fact
                {
                    this.following.push( RuleParser.FOLLOW_fact_in_lhs_column1441 );
                    f = fact();
                    this.following.pop();

                    d = f;

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

    // $ANTLR start fact_binding
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:625:1: fact_binding returns [PatternDescr d] : id= ID opt_eol ':' opt_eol fe= fact_expression[id.getText()] ;
    public PatternDescr fact_binding() throws RecognitionException {
        PatternDescr d;
        Token id = null;
        PatternDescr fe = null;

        d = null;
        final boolean multi = false;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:631:17: (id= ID opt_eol ':' opt_eol fe= fact_expression[id.getText()] )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:631:17: id= ID opt_eol ':' opt_eol fe= fact_expression[id.getText()]
            {
                id = (Token) this.input.LT( 1 );
                match( this.input,
                       RuleParser.ID,
                       RuleParser.FOLLOW_ID_in_fact_binding1473 );
                this.following.push( RuleParser.FOLLOW_opt_eol_in_fact_binding1483 );
                opt_eol();
                this.following.pop();

                match( this.input,
                       32,
                       RuleParser.FOLLOW_32_in_fact_binding1485 );
                this.following.push( RuleParser.FOLLOW_opt_eol_in_fact_binding1487 );
                opt_eol();
                this.following.pop();

                this.following.push( RuleParser.FOLLOW_fact_expression_in_fact_binding1491 );
                fe = fact_expression( id.getText() );
                this.following.pop();

                d = fe;

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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:639:2: fact_expression[String id] returns [PatternDescr pd] : ( '(' fe= fact_expression[id] ')' | f= fact opt_eol ( 'or' f= fact )* );
    public PatternDescr fact_expression(final String id) throws RecognitionException {
        PatternDescr pd;
        PatternDescr fe = null;

        PatternDescr f = null;

        pd = null;
        boolean multi = false;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:644:17: ( '(' fe= fact_expression[id] ')' | f= fact opt_eol ( 'or' f= fact )* )
            int alt46 = 2;
            final int LA46_0 = this.input.LA( 1 );
            if ( LA46_0 == 23 ) {
                alt46 = 1;
            } else if ( LA46_0 == RuleParser.ID ) {
                alt46 = 2;
            } else {
                final NoViableAltException nvae = new NoViableAltException( "639:2: fact_expression[String id] returns [PatternDescr pd] : ( \'(\' fe= fact_expression[id] \')\' | f= fact opt_eol ( \'or\' f= fact )* );",
                                                                            46,
                                                                            0,
                                                                            this.input );

                throw nvae;
            }
            switch ( alt46 ) {
                case 1 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:644:17: '(' fe= fact_expression[id] ')'
                {
                    match( this.input,
                           23,
                           RuleParser.FOLLOW_23_in_fact_expression1523 );
                    this.following.push( RuleParser.FOLLOW_fact_expression_in_fact_expression1527 );
                    fe = fact_expression( id );
                    this.following.pop();

                    match( this.input,
                           25,
                           RuleParser.FOLLOW_25_in_fact_expression1530 );
                    pd = fe;

                }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:645:17: f= fact opt_eol ( 'or' f= fact )*
                {
                    this.following.push( RuleParser.FOLLOW_fact_in_fact_expression1541 );
                    f = fact();
                    this.following.pop();

                    this.following.push( RuleParser.FOLLOW_opt_eol_in_fact_expression1543 );
                    opt_eol();
                    this.following.pop();

                    ((ColumnDescr) f).setIdentifier( id );
                    pd = f;

                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:650:17: ( 'or' f= fact )*
                    loop45 : do {
                        int alt45 = 2;
                        final int LA45_0 = this.input.LA( 1 );
                        if ( LA45_0 == 41 ) {
                            alt45 = 1;
                        }

                        switch ( alt45 ) {
                            case 1 :
                                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:650:25: 'or' f= fact
                            {
                                match( this.input,
                                       41,
                                       RuleParser.FOLLOW_41_in_fact_expression1555 );
                                if ( !multi ) {
                                    final PatternDescr first = pd;
                                    pd = new OrDescr();
                                    ((OrDescr) pd).addDescr( first );
                                    multi = true;
                                }

                                this.following.push( RuleParser.FOLLOW_fact_in_fact_expression1569 );
                                f = fact();
                                this.following.pop();

                                ((ColumnDescr) f).setIdentifier( id );
                                ((OrDescr) pd).addDescr( f );

                            }
                                break;

                            default :
                                break loop45;
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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:666:1: fact returns [PatternDescr d] : id= dotted_name opt_eol loc= '(' opt_eol (c= constraints )? opt_eol ')' opt_eol ;
    public PatternDescr fact() throws RecognitionException {
        PatternDescr d;
        Token loc = null;
        String id = null;

        List c = null;

        d = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:670:17: (id= dotted_name opt_eol loc= '(' opt_eol (c= constraints )? opt_eol ')' opt_eol )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:670:17: id= dotted_name opt_eol loc= '(' opt_eol (c= constraints )? opt_eol ')' opt_eol
            {
                this.following.push( RuleParser.FOLLOW_dotted_name_in_fact1608 );
                id = dotted_name();
                this.following.pop();

                d = new ColumnDescr( id );

                this.following.push( RuleParser.FOLLOW_opt_eol_in_fact1616 );
                opt_eol();
                this.following.pop();

                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       23,
                       RuleParser.FOLLOW_23_in_fact1624 );

                d.setLocation( offset( loc.getLine() ),
                               loc.getCharPositionInLine() );

                this.following.push( RuleParser.FOLLOW_opt_eol_in_fact1627 );
                opt_eol();
                this.following.pop();

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:676:34: (c= constraints )?
                int alt47 = 2;
                alt47 = this.dfa47.predict( this.input );
                switch ( alt47 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:676:41: c= constraints
                    {
                        this.following.push( RuleParser.FOLLOW_constraints_in_fact1633 );
                        c = constraints();
                        this.following.pop();

                        for ( final Iterator cIter = c.iterator(); cIter.hasNext(); ) {
                            ((ColumnDescr) d).addDescr( (PatternDescr) cIter.next() );
                        }

                    }
                        break;

                }

                this.following.push( RuleParser.FOLLOW_opt_eol_in_fact1652 );
                opt_eol();
                this.following.pop();

                match( this.input,
                       25,
                       RuleParser.FOLLOW_25_in_fact1654 );
                this.following.push( RuleParser.FOLLOW_opt_eol_in_fact1656 );
                opt_eol();
                this.following.pop();
                Token end = input.LT( -1 );
                d.setEndLocation( offset( end.getLine() ),
                                  end.getCharPositionInLine() );
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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:687:1: constraints returns [List constraints] : opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol ;
    public List constraints() throws RecognitionException {
        List constraints;

        constraints = new ArrayList();

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:691:17: ( opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:691:17: opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol
            {
                this.following.push( RuleParser.FOLLOW_opt_eol_in_constraints1681 );
                opt_eol();
                this.following.pop();

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:692:17: ( constraint[constraints] | predicate[constraints] )
                int alt48 = 2;
                final int LA48_0 = this.input.LA( 1 );
                if ( LA48_0 == RuleParser.EOL || LA48_0 == 15 ) {
                    alt48 = 1;
                } else if ( LA48_0 == RuleParser.ID ) {
                    final int LA48_2 = this.input.LA( 2 );
                    if ( LA48_2 == 32 ) {
                        final int LA48_3 = this.input.LA( 3 );
                        if ( LA48_3 == RuleParser.ID ) {
                            final int LA48_8 = this.input.LA( 4 );
                            if ( LA48_8 == 52 ) {
                                alt48 = 2;
                            } else if ( LA48_8 == RuleParser.EOL || LA48_8 == 15 || (LA48_8 >= 24 && LA48_8 <= 25) || (LA48_8 >= 42 && LA48_8 <= 50) ) {
                                alt48 = 1;
                            } else {
                                final NoViableAltException nvae = new NoViableAltException( "692:17: ( constraint[constraints] | predicate[constraints] )",
                                                                                            48,
                                                                                            8,
                                                                                            this.input );

                                throw nvae;
                            }
                        } else if ( LA48_3 == RuleParser.EOL || LA48_3 == 15 ) {
                            alt48 = 1;
                        } else {
                            final NoViableAltException nvae = new NoViableAltException( "692:17: ( constraint[constraints] | predicate[constraints] )",
                                                                                        48,
                                                                                        3,
                                                                                        this.input );

                            throw nvae;
                        }
                    } else if ( LA48_2 == RuleParser.EOL || LA48_2 == 15 || (LA48_2 >= 24 && LA48_2 <= 25) || (LA48_2 >= 42 && LA48_2 <= 50) ) {
                        alt48 = 1;
                    } else {
                        final NoViableAltException nvae = new NoViableAltException( "692:17: ( constraint[constraints] | predicate[constraints] )",
                                                                                    48,
                                                                                    2,
                                                                                    this.input );

                        throw nvae;
                    }
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "692:17: ( constraint[constraints] | predicate[constraints] )",
                                                                                48,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt48 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:692:18: constraint[constraints]
                    {
                        this.following.push( RuleParser.FOLLOW_constraint_in_constraints1686 );
                        constraint( constraints );
                        this.following.pop();

                    }
                        break;
                    case 2 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:692:42: predicate[constraints]
                    {
                        this.following.push( RuleParser.FOLLOW_predicate_in_constraints1689 );
                        predicate( constraints );
                        this.following.pop();

                    }
                        break;

                }

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:693:17: ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )*
                loop50 : do {
                    int alt50 = 2;
                    alt50 = this.dfa50.predict( this.input );
                    switch ( alt50 ) {
                        case 1 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:693:19: opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] )
                        {
                            this.following.push( RuleParser.FOLLOW_opt_eol_in_constraints1697 );
                            opt_eol();
                            this.following.pop();

                            match( this.input,
                                   24,
                                   RuleParser.FOLLOW_24_in_constraints1699 );
                            this.following.push( RuleParser.FOLLOW_opt_eol_in_constraints1701 );
                            opt_eol();
                            this.following.pop();

                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:693:39: ( constraint[constraints] | predicate[constraints] )
                            int alt49 = 2;
                            final int LA49_0 = this.input.LA( 1 );
                            if ( LA49_0 == RuleParser.EOL || LA49_0 == 15 ) {
                                alt49 = 1;
                            } else if ( LA49_0 == RuleParser.ID ) {
                                final int LA49_2 = this.input.LA( 2 );
                                if ( LA49_2 == 32 ) {
                                    final int LA49_3 = this.input.LA( 3 );
                                    if ( LA49_3 == RuleParser.ID ) {
                                        final int LA49_8 = this.input.LA( 4 );
                                        if ( LA49_8 == 52 ) {
                                            alt49 = 2;
                                        } else if ( LA49_8 == RuleParser.EOL || LA49_8 == 15 || (LA49_8 >= 24 && LA49_8 <= 25) || (LA49_8 >= 42 && LA49_8 <= 50) ) {
                                            alt49 = 1;
                                        } else {
                                            final NoViableAltException nvae = new NoViableAltException( "693:39: ( constraint[constraints] | predicate[constraints] )",
                                                                                                        49,
                                                                                                        8,
                                                                                                        this.input );

                                            throw nvae;
                                        }
                                    } else if ( LA49_3 == RuleParser.EOL || LA49_3 == 15 ) {
                                        alt49 = 1;
                                    } else {
                                        final NoViableAltException nvae = new NoViableAltException( "693:39: ( constraint[constraints] | predicate[constraints] )",
                                                                                                    49,
                                                                                                    3,
                                                                                                    this.input );

                                        throw nvae;
                                    }
                                } else if ( LA49_2 == RuleParser.EOL || LA49_2 == 15 || (LA49_2 >= 24 && LA49_2 <= 25) || (LA49_2 >= 42 && LA49_2 <= 50) ) {
                                    alt49 = 1;
                                } else {
                                    final NoViableAltException nvae = new NoViableAltException( "693:39: ( constraint[constraints] | predicate[constraints] )",
                                                                                                49,
                                                                                                2,
                                                                                                this.input );

                                    throw nvae;
                                }
                            } else {
                                final NoViableAltException nvae = new NoViableAltException( "693:39: ( constraint[constraints] | predicate[constraints] )",
                                                                                            49,
                                                                                            0,
                                                                                            this.input );

                                throw nvae;
                            }
                            switch ( alt49 ) {
                                case 1 :
                                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:693:40: constraint[constraints]
                                {
                                    this.following.push( RuleParser.FOLLOW_constraint_in_constraints1704 );
                                    constraint( constraints );
                                    this.following.pop();

                                }
                                    break;
                                case 2 :
                                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:693:64: predicate[constraints]
                                {
                                    this.following.push( RuleParser.FOLLOW_predicate_in_constraints1707 );
                                    predicate( constraints );
                                    this.following.pop();

                                }
                                    break;

                            }

                        }
                            break;

                        default :
                            break loop50;
                    }
                } while ( true );

                this.following.push( RuleParser.FOLLOW_opt_eol_in_constraints1715 );
                opt_eol();
                this.following.pop();

            }

        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return constraints;
    }

    // $ANTLR end constraints

    // $ANTLR start constraint
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:697:1: constraint[List constraints] : opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol (op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )? opt_eol ;
    public void constraint(final List constraints) throws RecognitionException {
        Token fb = null;
        Token f = null;
        Token op = null;
        Token bvc = null;
        String lc = null;

        String rvc = null;

        PatternDescr d = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:701:17: ( opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol (op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )? opt_eol )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:701:17: opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol (op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )? opt_eol
            {
                this.following.push( RuleParser.FOLLOW_opt_eol_in_constraint1734 );
                opt_eol();
                this.following.pop();

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:702:17: (fb= ID opt_eol ':' opt_eol )?
                int alt51 = 2;
                alt51 = this.dfa51.predict( this.input );
                switch ( alt51 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:702:19: fb= ID opt_eol ':' opt_eol
                    {
                        fb = (Token) this.input.LT( 1 );
                        match( this.input,
                               RuleParser.ID,
                               RuleParser.FOLLOW_ID_in_constraint1742 );
                        this.following.push( RuleParser.FOLLOW_opt_eol_in_constraint1744 );
                        opt_eol();
                        this.following.pop();

                        match( this.input,
                               32,
                               RuleParser.FOLLOW_32_in_constraint1746 );
                        this.following.push( RuleParser.FOLLOW_opt_eol_in_constraint1748 );
                        opt_eol();
                        this.following.pop();

                    }
                        break;

                }

                f = (Token) this.input.LT( 1 );
                match( this.input,
                       RuleParser.ID,
                       RuleParser.FOLLOW_ID_in_constraint1758 );

                if ( fb != null ) {
                    //System.err.println( "fb: " + fb.getText() );
                    //System.err.println( " f: " + f.getText() );
                    d = new FieldBindingDescr( f.getText(),
                                               fb.getText() );
                    //System.err.println( "fbd: " + d );

                    d.setLocation( offset( f.getLine() ),
                                   f.getCharPositionInLine() );
                    constraints.add( d );
                }

                this.following.push( RuleParser.FOLLOW_opt_eol_in_constraint1768 );
                opt_eol();
                this.following.pop();

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:715:33: (op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )?
                int alt53 = 2;
                final int LA53_0 = this.input.LA( 1 );
                if ( (LA53_0 >= 42 && LA53_0 <= 50) ) {
                    alt53 = 1;
                } else if ( LA53_0 == RuleParser.EOL || LA53_0 == 15 || (LA53_0 >= 24 && LA53_0 <= 25) ) {
                    alt53 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "715:33: (op= (\'==\'|\'>\'|\'>=\'|\'<\'|\'<=\'|\'!=\'|\'contains\'|\'matches\'|\'excludes\') opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )?",
                                                                                53,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt53 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:715:41: op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
                    {
                        op = (Token) this.input.LT( 1 );
                        if ( (this.input.LA( 1 ) >= 42 && this.input.LA( 1 ) <= 50) ) {
                            this.input.consume();
                            this.errorRecovery = false;
                        } else {
                            final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                           this.input );
                            recoverFromMismatchedSet( this.input,
                                                      mse,
                                                      RuleParser.FOLLOW_set_in_constraint1776 );
                            throw mse;
                        }

                        this.following.push( RuleParser.FOLLOW_opt_eol_in_constraint1863 );
                        opt_eol();
                        this.following.pop();

                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:726:41: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
                        int alt52 = 4;
                        switch ( this.input.LA( 1 ) ) {
                            case ID :
                                final int LA52_1 = this.input.LA( 2 );
                                if ( LA52_1 == 18 ) {
                                    alt52 = 2;
                                } else if ( LA52_1 == RuleParser.EOL || LA52_1 == 15 || (LA52_1 >= 24 && LA52_1 <= 25) ) {
                                    alt52 = 1;
                                } else {
                                    final NoViableAltException nvae = new NoViableAltException( "726:41: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )",
                                                                                                52,
                                                                                                1,
                                                                                                this.input );

                                    throw nvae;
                                }
                                break;
                            case INT :
                            case BOOL :
                            case STRING :
                            case FLOAT :
                            case 51 :
                                alt52 = 3;
                                break;
                            case 23 :
                                alt52 = 4;
                                break;
                            default :
                                final NoViableAltException nvae = new NoViableAltException( "726:41: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )",
                                                                                            52,
                                                                                            0,
                                                                                            this.input );

                                throw nvae;
                        }

                        switch ( alt52 ) {
                            case 1 :
                                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:726:49: bvc= ID
                            {
                                bvc = (Token) this.input.LT( 1 );
                                match( this.input,
                                       RuleParser.ID,
                                       RuleParser.FOLLOW_ID_in_constraint1881 );

                                d = new BoundVariableDescr( f.getText(),
                                                            op.getText(),
                                                            bvc.getText() );
                                d.setLocation( offset( f.getLine() ),
                                               f.getCharPositionInLine() );
                                constraints.add( d );

                            }
                                break;
                            case 2 :
                                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:733:49: lc= enum_constraint
                            {
                                this.following.push( RuleParser.FOLLOW_enum_constraint_in_constraint1906 );
                                lc = enum_constraint();
                                this.following.pop();

                                d = new LiteralDescr( f.getText(),
                                                      op.getText(),
                                                      lc,
                                                      true );
                                d.setLocation( offset( f.getLine() ),
                                               f.getCharPositionInLine() );
                                constraints.add( d );

                            }
                                break;
                            case 3 :
                                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:740:49: lc= literal_constraint
                            {
                                this.following.push( RuleParser.FOLLOW_literal_constraint_in_constraint1938 );
                                lc = literal_constraint();
                                this.following.pop();

                                d = new LiteralDescr( f.getText(),
                                                      op.getText(),
                                                      lc );
                                d.setLocation( offset( f.getLine() ),
                                               f.getCharPositionInLine() );
                                constraints.add( d );

                            }
                                break;
                            case 4 :
                                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:746:49: rvc= retval_constraint
                            {
                                this.following.push( RuleParser.FOLLOW_retval_constraint_in_constraint1958 );
                                rvc = retval_constraint();
                                this.following.pop();

                                d = new ReturnValueDescr( f.getText(),
                                                          op.getText(),
                                                          rvc );
                                d.setLocation( offset( f.getLine() ),
                                               f.getCharPositionInLine() );
                                constraints.add( d );

                            }
                                break;

                        }

                    }
                        break;

                }

                this.following.push( RuleParser.FOLLOW_opt_eol_in_constraint1991 );
                opt_eol();
                this.following.pop();

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

    // $ANTLR start literal_constraint
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:757:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' ) ;
    public String literal_constraint() throws RecognitionException {
        String text;
        Token t = null;

        text = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:761:17: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' ) )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:761:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' )
            {
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:761:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' )
                int alt54 = 5;
                switch ( this.input.LA( 1 ) ) {
                    case STRING :
                        alt54 = 1;
                        break;
                    case INT :
                        alt54 = 2;
                        break;
                    case FLOAT :
                        alt54 = 3;
                        break;
                    case BOOL :
                        alt54 = 4;
                        break;
                    case 51 :
                        alt54 = 5;
                        break;
                    default :
                        final NoViableAltException nvae = new NoViableAltException( "761:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= \'null\' )",
                                                                                    54,
                                                                                    0,
                                                                                    this.input );

                        throw nvae;
                }

                switch ( alt54 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:761:25: t= STRING
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               RuleParser.STRING,
                               RuleParser.FOLLOW_STRING_in_literal_constraint2018 );
                        text = getString( t );

                    }
                        break;
                    case 2 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:762:25: t= INT
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               RuleParser.INT,
                               RuleParser.FOLLOW_INT_in_literal_constraint2029 );
                        text = t.getText();

                    }
                        break;
                    case 3 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:763:25: t= FLOAT
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               RuleParser.FLOAT,
                               RuleParser.FOLLOW_FLOAT_in_literal_constraint2042 );
                        text = t.getText();

                    }
                        break;
                    case 4 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:764:25: t= BOOL
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               RuleParser.BOOL,
                               RuleParser.FOLLOW_BOOL_in_literal_constraint2053 );
                        text = t.getText();

                    }
                        break;
                    case 5 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:765:25: t= 'null'
                    {
                        t = (Token) this.input.LT( 1 );
                        match( this.input,
                               51,
                               RuleParser.FOLLOW_51_in_literal_constraint2065 );
                        text = null;

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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:769:1: enum_constraint returns [String text] : (cls= ID '.' en= ID ) ;
    public String enum_constraint() throws RecognitionException {
        String text;
        Token cls = null;
        Token en = null;

        text = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:773:17: ( (cls= ID '.' en= ID ) )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:773:17: (cls= ID '.' en= ID )
            {
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:773:17: (cls= ID '.' en= ID )
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:773:18: cls= ID '.' en= ID
                {
                    cls = (Token) this.input.LT( 1 );
                    match( this.input,
                           RuleParser.ID,
                           RuleParser.FOLLOW_ID_in_enum_constraint2096 );
                    match( this.input,
                           18,
                           RuleParser.FOLLOW_18_in_enum_constraint2098 );
                    en = (Token) this.input.LT( 1 );
                    match( this.input,
                           RuleParser.ID,
                           RuleParser.FOLLOW_ID_in_enum_constraint2102 );

                }

                text = cls.getText() + "." + en.getText();

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

    // $ANTLR start retval_constraint
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:776:1: retval_constraint returns [String text] : '(' c= paren_chunk ')' ;
    public String retval_constraint() throws RecognitionException {
        String text;
        String c = null;

        text = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:781:17: ( '(' c= paren_chunk ')' )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:781:17: '(' c= paren_chunk ')'
            {
                match( this.input,
                       23,
                       RuleParser.FOLLOW_23_in_retval_constraint2131 );
                this.following.push( RuleParser.FOLLOW_paren_chunk_in_retval_constraint2135 );
                c = paren_chunk();
                this.following.pop();

                match( this.input,
                       25,
                       RuleParser.FOLLOW_25_in_retval_constraint2137 );
                text = c;

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

    // $ANTLR start predicate
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:784:1: predicate[List constraints] : decl= ID ':' field= ID '->' '(' text= paren_chunk ')' ;
    public void predicate(final List constraints) throws RecognitionException {
        Token decl = null;
        Token field = null;
        String text = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:786:17: (decl= ID ':' field= ID '->' '(' text= paren_chunk ')' )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:786:17: decl= ID ':' field= ID '->' '(' text= paren_chunk ')'
            {
                decl = (Token) this.input.LT( 1 );
                match( this.input,
                       RuleParser.ID,
                       RuleParser.FOLLOW_ID_in_predicate2155 );
                match( this.input,
                       32,
                       RuleParser.FOLLOW_32_in_predicate2157 );
                field = (Token) this.input.LT( 1 );
                match( this.input,
                       RuleParser.ID,
                       RuleParser.FOLLOW_ID_in_predicate2161 );
                match( this.input,
                       52,
                       RuleParser.FOLLOW_52_in_predicate2163 );
                match( this.input,
                       23,
                       RuleParser.FOLLOW_23_in_predicate2165 );
                this.following.push( RuleParser.FOLLOW_paren_chunk_in_predicate2169 );
                text = paren_chunk();
                this.following.pop();

                match( this.input,
                       25,
                       RuleParser.FOLLOW_25_in_predicate2171 );

                final PredicateDescr d = new PredicateDescr( field.getText(),
                                                             decl.getText(),
                                                             text );
                constraints.add( d );

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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:793:1: paren_chunk returns [String text] : ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )* ;
    public String paren_chunk() throws RecognitionException {
        String text;
        Token any = null;
        String c = null;

        text = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:799:18: ( ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )* )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:799:18: ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )*
            {
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:799:18: ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )*
                loop55 : do {
                    int alt55 = 3;
                    switch ( this.input.LA( 1 ) ) {
                        case EOL :
                            alt55 = 3;
                            break;
                        case 25 :
                            alt55 = 3;
                            break;
                        case 23 :
                            alt55 = 1;
                            break;
                        case ID :
                        case INT :
                        case BOOL :
                        case STRING :
                        case FLOAT :
                        case MISC :
                        case WS :
                        case SH_STYLE_SINGLE_LINE_COMMENT :
                        case C_STYLE_SINGLE_LINE_COMMENT :
                        case MULTI_LINE_COMMENT :
                        case 15 :
                        case 16 :
                        case 17 :
                        case 18 :
                        case 19 :
                        case 20 :
                        case 21 :
                        case 22 :
                        case 24 :
                        case 26 :
                        case 27 :
                        case 28 :
                        case 29 :
                        case 30 :
                        case 31 :
                        case 32 :
                        case 33 :
                        case 34 :
                        case 35 :
                        case 36 :
                        case 37 :
                        case 38 :
                        case 39 :
                        case 40 :
                        case 41 :
                        case 42 :
                        case 43 :
                        case 44 :
                        case 45 :
                        case 46 :
                        case 47 :
                        case 48 :
                        case 49 :
                        case 50 :
                        case 51 :
                        case 52 :
                        case 53 :
                        case 54 :
                        case 55 :
                        case 56 :
                        case 57 :
                        case 58 :
                        case 59 :
                        case 60 :
                        case 61 :
                            alt55 = 2;
                            break;

                    }

                    switch ( alt55 ) {
                        case 1 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:800:25: '(' c= paren_chunk ')'
                        {
                            match( this.input,
                                   23,
                                   RuleParser.FOLLOW_23_in_paren_chunk2217 );
                            this.following.push( RuleParser.FOLLOW_paren_chunk_in_paren_chunk2221 );
                            c = paren_chunk();
                            this.following.pop();

                            match( this.input,
                                   25,
                                   RuleParser.FOLLOW_25_in_paren_chunk2223 );

                            if ( c == null ) {
                                c = "";
                            }
                            if ( text == null ) {
                                text = "( " + c + " )";
                            } else {
                                text = text + " ( " + c + " )";
                            }

                        }
                            break;
                        case 2 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:811:19: any= .
                        {
                            any = (Token) this.input.LT( 1 );
                            matchAny( this.input );

                            if ( text == null ) {
                                text = any.getText();
                            } else {
                                text = text + " " + any.getText();
                            }

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
        return text;
    }

    // $ANTLR end paren_chunk

    // $ANTLR start paren_chunk2
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:823:1: paren_chunk2 returns [String text] : ( options {greedy=false; } : '(' c= paren_chunk2 ')' | any= . )* ;
    public String paren_chunk2() throws RecognitionException {
        String text;
        Token any = null;
        String c = null;

        text = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:829:18: ( ( options {greedy=false; } : '(' c= paren_chunk2 ')' | any= . )* )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:829:18: ( options {greedy=false; } : '(' c= paren_chunk2 ')' | any= . )*
            {
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:829:18: ( options {greedy=false; } : '(' c= paren_chunk2 ')' | any= . )*
                loop56 : do {
                    int alt56 = 3;
                    switch ( this.input.LA( 1 ) ) {
                        case 25 :
                            alt56 = 3;
                            break;
                        case 23 :
                            alt56 = 1;
                            break;
                        case EOL :
                        case ID :
                        case INT :
                        case BOOL :
                        case STRING :
                        case FLOAT :
                        case MISC :
                        case WS :
                        case SH_STYLE_SINGLE_LINE_COMMENT :
                        case C_STYLE_SINGLE_LINE_COMMENT :
                        case MULTI_LINE_COMMENT :
                        case 15 :
                        case 16 :
                        case 17 :
                        case 18 :
                        case 19 :
                        case 20 :
                        case 21 :
                        case 22 :
                        case 24 :
                        case 26 :
                        case 27 :
                        case 28 :
                        case 29 :
                        case 30 :
                        case 31 :
                        case 32 :
                        case 33 :
                        case 34 :
                        case 35 :
                        case 36 :
                        case 37 :
                        case 38 :
                        case 39 :
                        case 40 :
                        case 41 :
                        case 42 :
                        case 43 :
                        case 44 :
                        case 45 :
                        case 46 :
                        case 47 :
                        case 48 :
                        case 49 :
                        case 50 :
                        case 51 :
                        case 52 :
                        case 53 :
                        case 54 :
                        case 55 :
                        case 56 :
                        case 57 :
                        case 58 :
                        case 59 :
                        case 60 :
                        case 61 :
                            alt56 = 2;
                            break;

                    }

                    switch ( alt56 ) {
                        case 1 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:830:25: '(' c= paren_chunk2 ')'
                        {
                            match( this.input,
                                   23,
                                   RuleParser.FOLLOW_23_in_paren_chunk22294 );
                            this.following.push( RuleParser.FOLLOW_paren_chunk2_in_paren_chunk22298 );
                            c = paren_chunk2();
                            this.following.pop();

                            match( this.input,
                                   25,
                                   RuleParser.FOLLOW_25_in_paren_chunk22300 );

                            if ( c == null ) {
                                c = "";
                            }
                            if ( text == null ) {
                                text = "( " + c + " )";
                            } else {
                                text = text + " ( " + c + " )";
                            }

                        }
                            break;
                        case 2 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:841:19: any= .
                        {
                            any = (Token) this.input.LT( 1 );
                            matchAny( this.input );

                            if ( text == null ) {
                                text = any.getText();
                            } else {
                                text = text + " " + any.getText();
                            }

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
        return text;
    }

    // $ANTLR end paren_chunk2

    // $ANTLR start curly_chunk
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:852:1: curly_chunk returns [String text] : ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )* ;
    public String curly_chunk() throws RecognitionException {
        String text;
        Token any = null;
        String c = null;

        text = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:858:17: ( ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )* )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:858:17: ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )*
            {
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:858:17: ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )*
                loop57 : do {
                    int alt57 = 3;
                    switch ( this.input.LA( 1 ) ) {
                        case 27 :
                            alt57 = 3;
                            break;
                        case 26 :
                            alt57 = 1;
                            break;
                        case EOL :
                        case ID :
                        case INT :
                        case BOOL :
                        case STRING :
                        case FLOAT :
                        case MISC :
                        case WS :
                        case SH_STYLE_SINGLE_LINE_COMMENT :
                        case C_STYLE_SINGLE_LINE_COMMENT :
                        case MULTI_LINE_COMMENT :
                        case 15 :
                        case 16 :
                        case 17 :
                        case 18 :
                        case 19 :
                        case 20 :
                        case 21 :
                        case 22 :
                        case 23 :
                        case 24 :
                        case 25 :
                        case 28 :
                        case 29 :
                        case 30 :
                        case 31 :
                        case 32 :
                        case 33 :
                        case 34 :
                        case 35 :
                        case 36 :
                        case 37 :
                        case 38 :
                        case 39 :
                        case 40 :
                        case 41 :
                        case 42 :
                        case 43 :
                        case 44 :
                        case 45 :
                        case 46 :
                        case 47 :
                        case 48 :
                        case 49 :
                        case 50 :
                        case 51 :
                        case 52 :
                        case 53 :
                        case 54 :
                        case 55 :
                        case 56 :
                        case 57 :
                        case 58 :
                        case 59 :
                        case 60 :
                        case 61 :
                            alt57 = 2;
                            break;

                    }

                    switch ( alt57 ) {
                        case 1 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:859:25: '{' c= curly_chunk '}'
                        {
                            match( this.input,
                                   26,
                                   RuleParser.FOLLOW_26_in_curly_chunk2369 );
                            this.following.push( RuleParser.FOLLOW_curly_chunk_in_curly_chunk2373 );
                            c = curly_chunk();
                            this.following.pop();

                            match( this.input,
                                   27,
                                   RuleParser.FOLLOW_27_in_curly_chunk2375 );

                            //System.err.println( "chunk [" + c + "]" );
                            if ( c == null ) {
                                c = "";
                            }
                            if ( text == null ) {
                                text = "{ " + c + " }";
                            } else {
                                text = text + " { " + c + " }";
                            }

                        }
                            break;
                        case 2 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:871:19: any= .
                        {
                            any = (Token) this.input.LT( 1 );
                            matchAny( this.input );

                            //System.err.println( "any [" + any.getText() + "]" );
                            if ( text == null ) {
                                text = any.getText();
                            } else {
                                text = text + " " + any.getText();
                            }

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
        return text;
    }

    // $ANTLR end curly_chunk

    // $ANTLR start lhs_or
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:883:1: lhs_or returns [PatternDescr d] : left= lhs_and ( ('or'|'||') opt_eol right= lhs_and )* ;
    public PatternDescr lhs_or() throws RecognitionException {
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;

        d = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:888:17: (left= lhs_and ( ('or'|'||') opt_eol right= lhs_and )* )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:888:17: left= lhs_and ( ('or'|'||') opt_eol right= lhs_and )*
            {
                OrDescr or = null;
                this.following.push( RuleParser.FOLLOW_lhs_and_in_lhs_or2433 );
                left = lhs_and();
                this.following.pop();

                d = left;
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:890:17: ( ('or'|'||') opt_eol right= lhs_and )*
                loop58 : do {
                    int alt58 = 2;
                    final int LA58_0 = this.input.LA( 1 );
                    if ( LA58_0 == 41 || LA58_0 == 53 ) {
                        alt58 = 1;
                    }

                    switch ( alt58 ) {
                        case 1 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:890:19: ('or'|'||') opt_eol right= lhs_and
                        {
                            if ( this.input.LA( 1 ) == 41 || this.input.LA( 1 ) == 53 ) {
                                this.input.consume();
                                this.errorRecovery = false;
                            } else {
                                final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                               this.input );
                                recoverFromMismatchedSet( this.input,
                                                          mse,
                                                          RuleParser.FOLLOW_set_in_lhs_or2442 );
                                throw mse;
                            }

                            this.following.push( RuleParser.FOLLOW_opt_eol_in_lhs_or2447 );
                            opt_eol();
                            this.following.pop();

                            this.following.push( RuleParser.FOLLOW_lhs_and_in_lhs_or2454 );
                            right = lhs_and();
                            this.following.pop();

                            if ( or == null ) {
                                or = new OrDescr();
                                or.addDescr( left );
                                d = or;
                            }

                            or.addDescr( right );

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
        return d;
    }

    // $ANTLR end lhs_or

    // $ANTLR start lhs_and
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:904:1: lhs_and returns [PatternDescr d] : left= lhs_unary ( ('and'|'&&') opt_eol right= lhs_unary )* ;
    public PatternDescr lhs_and() throws RecognitionException {
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;

        d = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:909:17: (left= lhs_unary ( ('and'|'&&') opt_eol right= lhs_unary )* )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:909:17: left= lhs_unary ( ('and'|'&&') opt_eol right= lhs_unary )*
            {
                AndDescr and = null;
                this.following.push( RuleParser.FOLLOW_lhs_unary_in_lhs_and2494 );
                left = lhs_unary();
                this.following.pop();

                d = left;
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:911:17: ( ('and'|'&&') opt_eol right= lhs_unary )*
                loop59 : do {
                    int alt59 = 2;
                    final int LA59_0 = this.input.LA( 1 );
                    if ( (LA59_0 >= 54 && LA59_0 <= 55) ) {
                        alt59 = 1;
                    }

                    switch ( alt59 ) {
                        case 1 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:911:19: ('and'|'&&') opt_eol right= lhs_unary
                        {
                            if ( (this.input.LA( 1 ) >= 54 && this.input.LA( 1 ) <= 55) ) {
                                this.input.consume();
                                this.errorRecovery = false;
                            } else {
                                final MismatchedSetException mse = new MismatchedSetException( null,
                                                                                               this.input );
                                recoverFromMismatchedSet( this.input,
                                                          mse,
                                                          RuleParser.FOLLOW_set_in_lhs_and2503 );
                                throw mse;
                            }

                            this.following.push( RuleParser.FOLLOW_opt_eol_in_lhs_and2508 );
                            opt_eol();
                            this.following.pop();

                            this.following.push( RuleParser.FOLLOW_lhs_unary_in_lhs_and2515 );
                            right = lhs_unary();
                            this.following.pop();

                            if ( and == null ) {
                                and = new AndDescr();
                                and.addDescr( left );
                                d = and;
                            }

                            and.addDescr( right );

                        }
                            break;

                        default :
                            break loop59;
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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:925:1: lhs_unary returns [PatternDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) ;
    public PatternDescr lhs_unary() throws RecognitionException {
        PatternDescr d;
        PatternDescr u = null;

        d = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:929:17: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:929:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            {
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:929:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
                int alt60 = 5;
                switch ( this.input.LA( 1 ) ) {
                    case 56 :
                        alt60 = 1;
                        break;
                    case 57 :
                        alt60 = 2;
                        break;
                    case 58 :
                        alt60 = 3;
                        break;
                    case ID :
                        alt60 = 4;
                        break;
                    case 23 :
                        alt60 = 5;
                        break;
                    default :
                        final NoViableAltException nvae = new NoViableAltException( "929:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | \'(\' u= lhs \')\' )",
                                                                                    60,
                                                                                    0,
                                                                                    this.input );

                        throw nvae;
                }

                switch ( alt60 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:929:25: u= lhs_exist
                    {
                        this.following.push( RuleParser.FOLLOW_lhs_exist_in_lhs_unary2553 );
                        u = lhs_exist();
                        this.following.pop();

                    }
                        break;
                    case 2 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:930:25: u= lhs_not
                    {
                        this.following.push( RuleParser.FOLLOW_lhs_not_in_lhs_unary2561 );
                        u = lhs_not();
                        this.following.pop();

                    }
                        break;
                    case 3 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:931:25: u= lhs_eval
                    {
                        this.following.push( RuleParser.FOLLOW_lhs_eval_in_lhs_unary2569 );
                        u = lhs_eval();
                        this.following.pop();

                    }
                        break;
                    case 4 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:932:25: u= lhs_column
                    {
                        this.following.push( RuleParser.FOLLOW_lhs_column_in_lhs_unary2577 );
                        u = lhs_column();
                        this.following.pop();

                    }
                        break;
                    case 5 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:933:25: '(' u= lhs ')'
                    {
                        match( this.input,
                               23,
                               RuleParser.FOLLOW_23_in_lhs_unary2583 );
                        this.following.push( RuleParser.FOLLOW_lhs_in_lhs_unary2587 );
                        u = lhs();
                        this.following.pop();

                        match( this.input,
                               25,
                               RuleParser.FOLLOW_25_in_lhs_unary2589 );

                    }
                        break;

                }

                d = u;

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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:937:1: lhs_exist returns [PatternDescr d] : loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column ) ;
    public PatternDescr lhs_exist() throws RecognitionException {
        PatternDescr d;
        Token loc = null;
        PatternDescr column = null;

        d = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:941:17: (loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column ) )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:941:17: loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column )
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       56,
                       RuleParser.FOLLOW_56_in_lhs_exist2619 );
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:941:30: ( '(' column= lhs_column ')' | column= lhs_column )
                int alt61 = 2;
                final int LA61_0 = this.input.LA( 1 );
                if ( LA61_0 == 23 ) {
                    alt61 = 1;
                } else if ( LA61_0 == RuleParser.ID ) {
                    alt61 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "941:30: ( \'(\' column= lhs_column \')\' | column= lhs_column )",
                                                                                61,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt61 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:941:31: '(' column= lhs_column ')'
                    {
                        match( this.input,
                               23,
                               RuleParser.FOLLOW_23_in_lhs_exist2622 );
                        this.following.push( RuleParser.FOLLOW_lhs_column_in_lhs_exist2626 );
                        column = lhs_column();
                        this.following.pop();

                        match( this.input,
                               25,
                               RuleParser.FOLLOW_25_in_lhs_exist2628 );

                    }
                        break;
                    case 2 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:941:59: column= lhs_column
                    {
                        this.following.push( RuleParser.FOLLOW_lhs_column_in_lhs_exist2634 );
                        column = lhs_column();
                        this.following.pop();

                    }
                        break;

                }

                d = new ExistsDescr( (ColumnDescr) column );
                d.setLocation( offset( loc.getLine() ),
                               loc.getCharPositionInLine() );

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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:948:1: lhs_not returns [NotDescr d] : loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column ) ;
    public NotDescr lhs_not() throws RecognitionException {
        NotDescr d;
        Token loc = null;
        PatternDescr column = null;

        d = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:952:17: (loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column ) )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:952:17: loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column )
            {
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       57,
                       RuleParser.FOLLOW_57_in_lhs_not2664 );
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:952:27: ( '(' column= lhs_column ')' | column= lhs_column )
                int alt62 = 2;
                final int LA62_0 = this.input.LA( 1 );
                if ( LA62_0 == 23 ) {
                    alt62 = 1;
                } else if ( LA62_0 == RuleParser.ID ) {
                    alt62 = 2;
                } else {
                    final NoViableAltException nvae = new NoViableAltException( "952:27: ( \'(\' column= lhs_column \')\' | column= lhs_column )",
                                                                                62,
                                                                                0,
                                                                                this.input );

                    throw nvae;
                }
                switch ( alt62 ) {
                    case 1 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:952:28: '(' column= lhs_column ')'
                    {
                        match( this.input,
                               23,
                               RuleParser.FOLLOW_23_in_lhs_not2667 );
                        this.following.push( RuleParser.FOLLOW_lhs_column_in_lhs_not2671 );
                        column = lhs_column();
                        this.following.pop();

                        match( this.input,
                               25,
                               RuleParser.FOLLOW_25_in_lhs_not2674 );

                    }
                        break;
                    case 2 :
                        // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:952:57: column= lhs_column
                    {
                        this.following.push( RuleParser.FOLLOW_lhs_column_in_lhs_not2680 );
                        column = lhs_column();
                        this.following.pop();

                    }
                        break;

                }

                d = new NotDescr( (ColumnDescr) column );
                d.setLocation( offset( loc.getLine() ),
                               loc.getCharPositionInLine() );

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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:959:1: lhs_eval returns [PatternDescr d] : 'eval' loc= '(' c= paren_chunk2 ')' ;
    public PatternDescr lhs_eval() throws RecognitionException {
        PatternDescr d;
        Token loc = null;
        String c = null;

        d = null;
        final String text = "";

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:964:17: ( 'eval' loc= '(' c= paren_chunk2 ')' )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:964:17: 'eval' loc= '(' c= paren_chunk2 ')'
            {
                match( this.input,
                       58,
                       RuleParser.FOLLOW_58_in_lhs_eval2706 );
                loc = (Token) this.input.LT( 1 );
                match( this.input,
                       23,
                       RuleParser.FOLLOW_23_in_lhs_eval2710 );
                this.following.push( RuleParser.FOLLOW_paren_chunk2_in_lhs_eval2718 );
                c = paren_chunk2();
                this.following.pop();

                match( this.input,
                       25,
                       RuleParser.FOLLOW_25_in_lhs_eval2722 );

                checkTrailingSemicolon( c,
                                        offset( loc.getLine() ) );
                d = new EvalDescr( c );

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

    // $ANTLR start dotted_name
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:973:1: dotted_name returns [String name] : id= ID ( '.' id= ID )* ( '[' ']' )* ;
    public String dotted_name() throws RecognitionException {
        String name;
        Token id = null;

        name = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:978:17: (id= ID ( '.' id= ID )* ( '[' ']' )* )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:978:17: id= ID ( '.' id= ID )* ( '[' ']' )*
            {
                id = (Token) this.input.LT( 1 );
                match( this.input,
                       RuleParser.ID,
                       RuleParser.FOLLOW_ID_in_dotted_name2754 );
                name = id.getText();
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:978:46: ( '.' id= ID )*
                loop63 : do {
                    int alt63 = 2;
                    final int LA63_0 = this.input.LA( 1 );
                    if ( LA63_0 == 18 ) {
                        alt63 = 1;
                    }

                    switch ( alt63 ) {
                        case 1 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:978:48: '.' id= ID
                        {
                            match( this.input,
                                   18,
                                   RuleParser.FOLLOW_18_in_dotted_name2760 );
                            id = (Token) this.input.LT( 1 );
                            match( this.input,
                                   RuleParser.ID,
                                   RuleParser.FOLLOW_ID_in_dotted_name2764 );
                            name = name + "." + id.getText();

                        }
                            break;

                        default :
                            break loop63;
                    }
                } while ( true );

                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:978:99: ( '[' ']' )*
                loop64 : do {
                    int alt64 = 2;
                    final int LA64_0 = this.input.LA( 1 );
                    if ( LA64_0 == 59 ) {
                        alt64 = 1;
                    }

                    switch ( alt64 ) {
                        case 1 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:978:101: '[' ']'
                        {
                            match( this.input,
                                   59,
                                   RuleParser.FOLLOW_59_in_dotted_name2773 );
                            match( this.input,
                                   60,
                                   RuleParser.FOLLOW_60_in_dotted_name2775 );
                            name = name + "[]";

                        }
                            break;

                        default :
                            break loop64;
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
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:981:1: argument returns [String name] : id= ID ( '[' ']' )* ;
    public String argument() throws RecognitionException {
        String name;
        Token id = null;

        name = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:986:17: (id= ID ( '[' ']' )* )
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:986:17: id= ID ( '[' ']' )*
            {
                id = (Token) this.input.LT( 1 );
                match( this.input,
                       RuleParser.ID,
                       RuleParser.FOLLOW_ID_in_argument2805 );
                name = id.getText();
                // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:986:46: ( '[' ']' )*
                loop65 : do {
                    int alt65 = 2;
                    final int LA65_0 = this.input.LA( 1 );
                    if ( LA65_0 == 59 ) {
                        alt65 = 1;
                    }

                    switch ( alt65 ) {
                        case 1 :
                            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:986:48: '[' ']'
                        {
                            match( this.input,
                                   59,
                                   RuleParser.FOLLOW_59_in_argument2811 );
                            match( this.input,
                                   60,
                                   RuleParser.FOLLOW_60_in_argument2813 );
                            name = name + "[]";

                        }
                            break;

                        default :
                            break loop65;
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

    // $ANTLR start word
    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:990:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );
    public String word() throws RecognitionException {
        String word;
        Token id = null;
        Token str = null;

        word = null;

        try {
            // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:994:17: (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING )
            int alt66 = 11;
            switch ( this.input.LA( 1 ) ) {
                case ID :
                    alt66 = 1;
                    break;
                case 17 :
                    alt66 = 2;
                    break;
                case 61 :
                    alt66 = 3;
                    break;
                case 30 :
                    alt66 = 4;
                    break;
                case 28 :
                    alt66 = 5;
                    break;
                case 35 :
                    alt66 = 6;
                    break;
                case 36 :
                    alt66 = 7;
                    break;
                case 31 :
                    alt66 = 8;
                    break;
                case 33 :
                    alt66 = 9;
                    break;
                case 29 :
                    alt66 = 10;
                    break;
                case STRING :
                    alt66 = 11;
                    break;
                default :
                    final NoViableAltException nvae = new NoViableAltException( "990:1: word returns [String word] : (id= ID | \'import\' | \'use\' | \'rule\' | \'query\' | \'salience\' | \'no-loop\' | \'when\' | \'then\' | \'end\' | str= STRING );",
                                                                                66,
                                                                                0,
                                                                                this.input );

                    throw nvae;
            }

            switch ( alt66 ) {
                case 1 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:994:17: id= ID
                {
                    id = (Token) this.input.LT( 1 );
                    match( this.input,
                           RuleParser.ID,
                           RuleParser.FOLLOW_ID_in_word2841 );
                    word = id.getText();

                }
                    break;
                case 2 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:995:17: 'import'
                {
                    match( this.input,
                           17,
                           RuleParser.FOLLOW_17_in_word2853 );
                    word = "import";

                }
                    break;
                case 3 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:996:17: 'use'
                {
                    match( this.input,
                           61,
                           RuleParser.FOLLOW_61_in_word2862 );
                    word = "use";

                }
                    break;
                case 4 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:997:17: 'rule'
                {
                    match( this.input,
                           30,
                           RuleParser.FOLLOW_30_in_word2874 );
                    word = "rule";

                }
                    break;
                case 5 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:998:17: 'query'
                {
                    match( this.input,
                           28,
                           RuleParser.FOLLOW_28_in_word2885 );
                    word = "query";

                }
                    break;
                case 6 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:999:17: 'salience'
                {
                    match( this.input,
                           35,
                           RuleParser.FOLLOW_35_in_word2895 );
                    word = "salience";

                }
                    break;
                case 7 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:1000:17: 'no-loop'
                {
                    match( this.input,
                           36,
                           RuleParser.FOLLOW_36_in_word2903 );
                    word = "no-loop";

                }
                    break;
                case 8 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:1001:17: 'when'
                {
                    match( this.input,
                           31,
                           RuleParser.FOLLOW_31_in_word2911 );
                    word = "when";

                }
                    break;
                case 9 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:1002:17: 'then'
                {
                    match( this.input,
                           33,
                           RuleParser.FOLLOW_33_in_word2922 );
                    word = "then";

                }
                    break;
                case 10 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:1003:17: 'end'
                {
                    match( this.input,
                           29,
                           RuleParser.FOLLOW_29_in_word2933 );
                    word = "end";

                }
                    break;
                case 11 :
                    // C:\Projects\jboss-rules-new\drools-compiler\src\main\resources\org\drools\lang\drl.g:1004:17: str= STRING
                {
                    str = (Token) this.input.LT( 1 );
                    match( this.input,
                           RuleParser.STRING,
                           RuleParser.FOLLOW_STRING_in_word2947 );
                    word = getString( str );

                }
                    break;

            }
        } catch ( final RecognitionException re ) {
            reportError( re );
            recover( this.input,
                     re );
        } finally {
        }
        return word;
    }

    // $ANTLR end word

    protected DFA2  dfa2  = new DFA2();
    protected DFA4  dfa4  = new DFA4();
    protected DFA12 dfa12 = new DFA12();
    protected DFA13 dfa13 = new DFA13();
    protected DFA14 dfa14 = new DFA14();
    protected DFA44 dfa44 = new DFA44();
    protected DFA47 dfa47 = new DFA47();
    protected DFA50 dfa50 = new DFA50();
    protected DFA51 dfa51 = new DFA51();

    class DFA2 extends DFA {
        public int predict(final IntStream input) throws RecognitionException {
            return predict( input,
                            this.s0 );
        }

        DFA.State s1 = new DFA.State() {
                         {
                             this.alt = 4;
                         }
                     };
        DFA.State s3 = new DFA.State() {
                         {
                             this.alt = 1;
                         }
                     };
        DFA.State s4 = new DFA.State() {
                         {
                             this.alt = 2;
                         }
                     };
        DFA.State s2 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             switch ( input.LA( 1 ) ) {
                                 case 30 :
                                     return DFA2.this.s3;

                                 case EOL :
                                 case 15 :
                                     return DFA2.this.s2;

                                 case 28 :
                                     return DFA2.this.s4;

                                 default :
                                     NoViableAltException nvae = new NoViableAltException( "",
                                                                                           2,
                                                                                           2,
                                                                                           input );

                                     throw nvae;
                             }
                         }
                     };
        DFA.State s5 = new DFA.State() {
                         {
                             this.alt = 3;
                         }
                     };
        DFA.State s0 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             switch ( input.LA( 1 ) ) {
                                 case -1 :
                                     return DFA2.this.s1;

                                 case EOL :
                                 case 15 :
                                     return DFA2.this.s2;

                                 case 30 :
                                     return DFA2.this.s3;

                                 case 28 :
                                     return DFA2.this.s4;

                                 case 17 :
                                 case 21 :
                                 case 22 :
                                     return DFA2.this.s5;

                                 default :
                                     NoViableAltException nvae = new NoViableAltException( "",
                                                                                           2,
                                                                                           0,
                                                                                           input );

                                     throw nvae;
                             }
                         }
                     };

    }

    class DFA4 extends DFA {
        public int predict(final IntStream input) throws RecognitionException {
            return predict( input,
                            this.s0 );
        }

        DFA.State s1   = new DFA.State() {
                           {
                               this.alt = 3;
                           }
                       };
        DFA.State s10  = new DFA.State() {
                           {
                               this.alt = 1;
                           }
                       };
        DFA.State s9   = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               int LA4_9 = input.LA( 1 );
                               if ( LA4_9 == RuleParser.ID ) {
                                   return DFA4.this.s10;
                               }
                               if ( LA4_9 == RuleParser.EOL || LA4_9 == 15 ) {
                                   return DFA4.this.s9;
                               }

                               NoViableAltException nvae = new NoViableAltException( "",
                                                                                     4,
                                                                                     9,
                                                                                     input );

                               throw nvae;
                           }
                       };
        DFA.State s4   = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               int LA4_4 = input.LA( 1 );
                               if ( LA4_4 == RuleParser.EOL || LA4_4 == 15 ) {
                                   return DFA4.this.s9;
                               }
                               if ( LA4_4 == RuleParser.ID ) {
                                   return DFA4.this.s10;
                               }

                               NoViableAltException nvae = new NoViableAltException( "",
                                                                                     4,
                                                                                     4,
                                                                                     input );

                               throw nvae;
                           }
                       };
        DFA.State s18  = new DFA.State() {
                           {
                               this.alt = 1;
                           }
                       };
        DFA.State s27  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               int LA4_27 = input.LA( 1 );
                               if ( LA4_27 == RuleParser.ID ) {
                                   return DFA4.this.s18;
                               }
                               if ( LA4_27 == 59 ) {
                                   return DFA4.this.s17;
                               }

                               NoViableAltException nvae = new NoViableAltException( "",
                                                                                     4,
                                                                                     27,
                                                                                     input );

                               throw nvae;
                           }
                       };
        DFA.State s17  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               int LA4_17 = input.LA( 1 );
                               if ( LA4_17 == 60 ) {
                                   return DFA4.this.s27;
                               }

                               NoViableAltException nvae = new NoViableAltException( "",
                                                                                     4,
                                                                                     17,
                                                                                     input );

                               throw nvae;
                           }
                       };
        DFA.State s26  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 59 :
                                       return DFA4.this.s17;

                                   case ID :
                                       return DFA4.this.s18;

                                   case 18 :
                                       return DFA4.this.s16;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             26,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s16  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               int LA4_16 = input.LA( 1 );
                               if ( LA4_16 == RuleParser.ID ) {
                                   return DFA4.this.s26;
                               }

                               NoViableAltException nvae = new NoViableAltException( "",
                                                                                     4,
                                                                                     16,
                                                                                     input );

                               throw nvae;
                           }
                       };
        DFA.State s11  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 18 :
                                       return DFA4.this.s16;

                                   case 59 :
                                       return DFA4.this.s17;

                                   case ID :
                                       return DFA4.this.s18;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             11,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s5   = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               int LA4_5 = input.LA( 1 );
                               if ( LA4_5 == RuleParser.ID ) {
                                   return DFA4.this.s11;
                               }

                               NoViableAltException nvae = new NoViableAltException( "",
                                                                                     4,
                                                                                     5,
                                                                                     input );

                               throw nvae;
                           }
                       };
        DFA.State s105 = new DFA.State() {
                           {
                               this.alt = 1;
                           }
                       };
        DFA.State s114 = new DFA.State() {
                           {
                               this.alt = 1;
                           }
                       };
        DFA.State s120 = new DFA.State() {
                           {
                               this.alt = 1;
                           }
                       };
        DFA.State s122 = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 27 :
                                       return DFA4.this.s120;

                                   case 26 :
                                       return DFA4.this.s121;

                                   case EOL :
                                   case ID :
                                   case INT :
                                   case BOOL :
                                   case STRING :
                                   case FLOAT :
                                   case MISC :
                                   case WS :
                                   case SH_STYLE_SINGLE_LINE_COMMENT :
                                   case C_STYLE_SINGLE_LINE_COMMENT :
                                   case MULTI_LINE_COMMENT :
                                   case 15 :
                                   case 16 :
                                   case 17 :
                                   case 18 :
                                   case 19 :
                                   case 20 :
                                   case 21 :
                                   case 22 :
                                   case 23 :
                                   case 24 :
                                   case 25 :
                                   case 28 :
                                   case 29 :
                                   case 30 :
                                   case 31 :
                                   case 32 :
                                   case 33 :
                                   case 34 :
                                   case 35 :
                                   case 36 :
                                   case 37 :
                                   case 38 :
                                   case 39 :
                                   case 40 :
                                   case 41 :
                                   case 42 :
                                   case 43 :
                                   case 44 :
                                   case 45 :
                                   case 46 :
                                   case 47 :
                                   case 48 :
                                   case 49 :
                                   case 50 :
                                   case 51 :
                                   case 52 :
                                   case 53 :
                                   case 54 :
                                   case 55 :
                                   case 56 :
                                   case 57 :
                                   case 58 :
                                   case 59 :
                                   case 60 :
                                   case 61 :
                                       return DFA4.this.s122;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             122,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s121 = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 27 :
                                       return DFA4.this.s120;

                                   case 26 :
                                       return DFA4.this.s121;

                                   case EOL :
                                   case ID :
                                   case INT :
                                   case BOOL :
                                   case STRING :
                                   case FLOAT :
                                   case MISC :
                                   case WS :
                                   case SH_STYLE_SINGLE_LINE_COMMENT :
                                   case C_STYLE_SINGLE_LINE_COMMENT :
                                   case MULTI_LINE_COMMENT :
                                   case 15 :
                                   case 16 :
                                   case 17 :
                                   case 18 :
                                   case 19 :
                                   case 20 :
                                   case 21 :
                                   case 22 :
                                   case 23 :
                                   case 24 :
                                   case 25 :
                                   case 28 :
                                   case 29 :
                                   case 30 :
                                   case 31 :
                                   case 32 :
                                   case 33 :
                                   case 34 :
                                   case 35 :
                                   case 36 :
                                   case 37 :
                                   case 38 :
                                   case 39 :
                                   case 40 :
                                   case 41 :
                                   case 42 :
                                   case 43 :
                                   case 44 :
                                   case 45 :
                                   case 46 :
                                   case 47 :
                                   case 48 :
                                   case 49 :
                                   case 50 :
                                   case 51 :
                                   case 52 :
                                   case 53 :
                                   case 54 :
                                   case 55 :
                                   case 56 :
                                   case 57 :
                                   case 58 :
                                   case 59 :
                                   case 60 :
                                   case 61 :
                                       return DFA4.this.s122;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             121,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s115 = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 27 :
                                       return DFA4.this.s120;

                                   case 26 :
                                       return DFA4.this.s121;

                                   case EOL :
                                   case ID :
                                   case INT :
                                   case BOOL :
                                   case STRING :
                                   case FLOAT :
                                   case MISC :
                                   case WS :
                                   case SH_STYLE_SINGLE_LINE_COMMENT :
                                   case C_STYLE_SINGLE_LINE_COMMENT :
                                   case MULTI_LINE_COMMENT :
                                   case 15 :
                                   case 16 :
                                   case 17 :
                                   case 18 :
                                   case 19 :
                                   case 20 :
                                   case 21 :
                                   case 22 :
                                   case 23 :
                                   case 24 :
                                   case 25 :
                                   case 28 :
                                   case 29 :
                                   case 30 :
                                   case 31 :
                                   case 32 :
                                   case 33 :
                                   case 34 :
                                   case 35 :
                                   case 36 :
                                   case 37 :
                                   case 38 :
                                   case 39 :
                                   case 40 :
                                   case 41 :
                                   case 42 :
                                   case 43 :
                                   case 44 :
                                   case 45 :
                                   case 46 :
                                   case 47 :
                                   case 48 :
                                   case 49 :
                                   case 50 :
                                   case 51 :
                                   case 52 :
                                   case 53 :
                                   case 54 :
                                   case 55 :
                                   case 56 :
                                   case 57 :
                                   case 58 :
                                   case 59 :
                                   case 60 :
                                   case 61 :
                                       return DFA4.this.s122;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             115,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s116 = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 27 :
                                       return DFA4.this.s114;

                                   case 26 :
                                       return DFA4.this.s115;

                                   case EOL :
                                   case ID :
                                   case INT :
                                   case BOOL :
                                   case STRING :
                                   case FLOAT :
                                   case MISC :
                                   case WS :
                                   case SH_STYLE_SINGLE_LINE_COMMENT :
                                   case C_STYLE_SINGLE_LINE_COMMENT :
                                   case MULTI_LINE_COMMENT :
                                   case 15 :
                                   case 16 :
                                   case 17 :
                                   case 18 :
                                   case 19 :
                                   case 20 :
                                   case 21 :
                                   case 22 :
                                   case 23 :
                                   case 24 :
                                   case 25 :
                                   case 28 :
                                   case 29 :
                                   case 30 :
                                   case 31 :
                                   case 32 :
                                   case 33 :
                                   case 34 :
                                   case 35 :
                                   case 36 :
                                   case 37 :
                                   case 38 :
                                   case 39 :
                                   case 40 :
                                   case 41 :
                                   case 42 :
                                   case 43 :
                                   case 44 :
                                   case 45 :
                                   case 46 :
                                   case 47 :
                                   case 48 :
                                   case 49 :
                                   case 50 :
                                   case 51 :
                                   case 52 :
                                   case 53 :
                                   case 54 :
                                   case 55 :
                                   case 56 :
                                   case 57 :
                                   case 58 :
                                   case 59 :
                                   case 60 :
                                   case 61 :
                                       return DFA4.this.s116;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             116,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s106 = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 27 :
                                       return DFA4.this.s114;

                                   case 26 :
                                       return DFA4.this.s115;

                                   case EOL :
                                   case ID :
                                   case INT :
                                   case BOOL :
                                   case STRING :
                                   case FLOAT :
                                   case MISC :
                                   case WS :
                                   case SH_STYLE_SINGLE_LINE_COMMENT :
                                   case C_STYLE_SINGLE_LINE_COMMENT :
                                   case MULTI_LINE_COMMENT :
                                   case 15 :
                                   case 16 :
                                   case 17 :
                                   case 18 :
                                   case 19 :
                                   case 20 :
                                   case 21 :
                                   case 22 :
                                   case 23 :
                                   case 24 :
                                   case 25 :
                                   case 28 :
                                   case 29 :
                                   case 30 :
                                   case 31 :
                                   case 32 :
                                   case 33 :
                                   case 34 :
                                   case 35 :
                                   case 36 :
                                   case 37 :
                                   case 38 :
                                   case 39 :
                                   case 40 :
                                   case 41 :
                                   case 42 :
                                   case 43 :
                                   case 44 :
                                   case 45 :
                                   case 46 :
                                   case 47 :
                                   case 48 :
                                   case 49 :
                                   case 50 :
                                   case 51 :
                                   case 52 :
                                   case 53 :
                                   case 54 :
                                   case 55 :
                                   case 56 :
                                   case 57 :
                                   case 58 :
                                   case 59 :
                                   case 60 :
                                   case 61 :
                                       return DFA4.this.s116;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             106,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s107 = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 27 :
                                       return DFA4.this.s105;

                                   case 26 :
                                       return DFA4.this.s106;

                                   case EOL :
                                   case ID :
                                   case INT :
                                   case BOOL :
                                   case STRING :
                                   case FLOAT :
                                   case MISC :
                                   case WS :
                                   case SH_STYLE_SINGLE_LINE_COMMENT :
                                   case C_STYLE_SINGLE_LINE_COMMENT :
                                   case MULTI_LINE_COMMENT :
                                   case 15 :
                                   case 16 :
                                   case 17 :
                                   case 18 :
                                   case 19 :
                                   case 20 :
                                   case 21 :
                                   case 22 :
                                   case 23 :
                                   case 24 :
                                   case 25 :
                                   case 28 :
                                   case 29 :
                                   case 30 :
                                   case 31 :
                                   case 32 :
                                   case 33 :
                                   case 34 :
                                   case 35 :
                                   case 36 :
                                   case 37 :
                                   case 38 :
                                   case 39 :
                                   case 40 :
                                   case 41 :
                                   case 42 :
                                   case 43 :
                                   case 44 :
                                   case 45 :
                                   case 46 :
                                   case 47 :
                                   case 48 :
                                   case 49 :
                                   case 50 :
                                   case 51 :
                                   case 52 :
                                   case 53 :
                                   case 54 :
                                   case 55 :
                                   case 56 :
                                   case 57 :
                                   case 58 :
                                   case 59 :
                                   case 60 :
                                   case 61 :
                                       return DFA4.this.s107;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             107,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s87  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 27 :
                                       return DFA4.this.s105;

                                   case 26 :
                                       return DFA4.this.s106;

                                   case EOL :
                                   case ID :
                                   case INT :
                                   case BOOL :
                                   case STRING :
                                   case FLOAT :
                                   case MISC :
                                   case WS :
                                   case SH_STYLE_SINGLE_LINE_COMMENT :
                                   case C_STYLE_SINGLE_LINE_COMMENT :
                                   case MULTI_LINE_COMMENT :
                                   case 15 :
                                   case 16 :
                                   case 17 :
                                   case 18 :
                                   case 19 :
                                   case 20 :
                                   case 21 :
                                   case 22 :
                                   case 23 :
                                   case 24 :
                                   case 25 :
                                   case 28 :
                                   case 29 :
                                   case 30 :
                                   case 31 :
                                   case 32 :
                                   case 33 :
                                   case 34 :
                                   case 35 :
                                   case 36 :
                                   case 37 :
                                   case 38 :
                                   case 39 :
                                   case 40 :
                                   case 41 :
                                   case 42 :
                                   case 43 :
                                   case 44 :
                                   case 45 :
                                   case 46 :
                                   case 47 :
                                   case 48 :
                                   case 49 :
                                   case 50 :
                                   case 51 :
                                   case 52 :
                                   case 53 :
                                   case 54 :
                                   case 55 :
                                   case 56 :
                                   case 57 :
                                   case 58 :
                                   case 59 :
                                   case 60 :
                                   case 61 :
                                       return DFA4.this.s107;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             87,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s88  = new DFA.State() {
                           {
                               this.alt = 1;
                           }
                       };
        DFA.State s89  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 27 :
                                       return DFA4.this.s88;

                                   case 26 :
                                       return DFA4.this.s87;

                                   case EOL :
                                   case ID :
                                   case INT :
                                   case BOOL :
                                   case STRING :
                                   case FLOAT :
                                   case MISC :
                                   case WS :
                                   case SH_STYLE_SINGLE_LINE_COMMENT :
                                   case C_STYLE_SINGLE_LINE_COMMENT :
                                   case MULTI_LINE_COMMENT :
                                   case 15 :
                                   case 16 :
                                   case 17 :
                                   case 18 :
                                   case 19 :
                                   case 20 :
                                   case 21 :
                                   case 22 :
                                   case 23 :
                                   case 24 :
                                   case 25 :
                                   case 28 :
                                   case 29 :
                                   case 30 :
                                   case 31 :
                                   case 32 :
                                   case 33 :
                                   case 34 :
                                   case 35 :
                                   case 36 :
                                   case 37 :
                                   case 38 :
                                   case 39 :
                                   case 40 :
                                   case 41 :
                                   case 42 :
                                   case 43 :
                                   case 44 :
                                   case 45 :
                                   case 46 :
                                   case 47 :
                                   case 48 :
                                   case 49 :
                                   case 50 :
                                   case 51 :
                                   case 52 :
                                   case 53 :
                                   case 54 :
                                   case 55 :
                                   case 56 :
                                   case 57 :
                                   case 58 :
                                   case 59 :
                                   case 60 :
                                   case 61 :
                                       return DFA4.this.s89;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             89,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s67  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 26 :
                                       return DFA4.this.s87;

                                   case 27 :
                                       return DFA4.this.s88;

                                   case EOL :
                                   case ID :
                                   case INT :
                                   case BOOL :
                                   case STRING :
                                   case FLOAT :
                                   case MISC :
                                   case WS :
                                   case SH_STYLE_SINGLE_LINE_COMMENT :
                                   case C_STYLE_SINGLE_LINE_COMMENT :
                                   case MULTI_LINE_COMMENT :
                                   case 15 :
                                   case 16 :
                                   case 17 :
                                   case 18 :
                                   case 19 :
                                   case 20 :
                                   case 21 :
                                   case 22 :
                                   case 23 :
                                   case 24 :
                                   case 25 :
                                   case 28 :
                                   case 29 :
                                   case 30 :
                                   case 31 :
                                   case 32 :
                                   case 33 :
                                   case 34 :
                                   case 35 :
                                   case 36 :
                                   case 37 :
                                   case 38 :
                                   case 39 :
                                   case 40 :
                                   case 41 :
                                   case 42 :
                                   case 43 :
                                   case 44 :
                                   case 45 :
                                   case 46 :
                                   case 47 :
                                   case 48 :
                                   case 49 :
                                   case 50 :
                                   case 51 :
                                   case 52 :
                                   case 53 :
                                   case 54 :
                                   case 55 :
                                   case 56 :
                                   case 57 :
                                   case 58 :
                                   case 59 :
                                   case 60 :
                                   case 61 :
                                       return DFA4.this.s89;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             67,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s68  = new DFA.State() {
                           {
                               this.alt = 1;
                           }
                       };
        DFA.State s69  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 27 :
                                       return DFA4.this.s68;

                                   case 26 :
                                       return DFA4.this.s67;

                                   case EOL :
                                   case ID :
                                   case INT :
                                   case BOOL :
                                   case STRING :
                                   case FLOAT :
                                   case MISC :
                                   case WS :
                                   case SH_STYLE_SINGLE_LINE_COMMENT :
                                   case C_STYLE_SINGLE_LINE_COMMENT :
                                   case MULTI_LINE_COMMENT :
                                   case 15 :
                                   case 16 :
                                   case 17 :
                                   case 18 :
                                   case 19 :
                                   case 20 :
                                   case 21 :
                                   case 22 :
                                   case 23 :
                                   case 24 :
                                   case 25 :
                                   case 28 :
                                   case 29 :
                                   case 30 :
                                   case 31 :
                                   case 32 :
                                   case 33 :
                                   case 34 :
                                   case 35 :
                                   case 36 :
                                   case 37 :
                                   case 38 :
                                   case 39 :
                                   case 40 :
                                   case 41 :
                                   case 42 :
                                   case 43 :
                                   case 44 :
                                   case 45 :
                                   case 46 :
                                   case 47 :
                                   case 48 :
                                   case 49 :
                                   case 50 :
                                   case 51 :
                                   case 52 :
                                   case 53 :
                                   case 54 :
                                   case 55 :
                                   case 56 :
                                   case 57 :
                                   case 58 :
                                   case 59 :
                                   case 60 :
                                   case 61 :
                                       return DFA4.this.s69;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             69,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s53  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 26 :
                                       return DFA4.this.s67;

                                   case 27 :
                                       return DFA4.this.s68;

                                   case EOL :
                                   case ID :
                                   case INT :
                                   case BOOL :
                                   case STRING :
                                   case FLOAT :
                                   case MISC :
                                   case WS :
                                   case SH_STYLE_SINGLE_LINE_COMMENT :
                                   case C_STYLE_SINGLE_LINE_COMMENT :
                                   case MULTI_LINE_COMMENT :
                                   case 15 :
                                   case 16 :
                                   case 17 :
                                   case 18 :
                                   case 19 :
                                   case 20 :
                                   case 21 :
                                   case 22 :
                                   case 23 :
                                   case 24 :
                                   case 25 :
                                   case 28 :
                                   case 29 :
                                   case 30 :
                                   case 31 :
                                   case 32 :
                                   case 33 :
                                   case 34 :
                                   case 35 :
                                   case 36 :
                                   case 37 :
                                   case 38 :
                                   case 39 :
                                   case 40 :
                                   case 41 :
                                   case 42 :
                                   case 43 :
                                   case 44 :
                                   case 45 :
                                   case 46 :
                                   case 47 :
                                   case 48 :
                                   case 49 :
                                   case 50 :
                                   case 51 :
                                   case 52 :
                                   case 53 :
                                   case 54 :
                                   case 55 :
                                   case 56 :
                                   case 57 :
                                   case 58 :
                                   case 59 :
                                   case 60 :
                                   case 61 :
                                       return DFA4.this.s69;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             53,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s52  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               int LA4_52 = input.LA( 1 );
                               if ( LA4_52 == 26 ) {
                                   return DFA4.this.s53;
                               }
                               if ( LA4_52 == RuleParser.EOL || LA4_52 == 15 ) {
                                   return DFA4.this.s52;
                               }

                               NoViableAltException nvae = new NoViableAltException( "",
                                                                                     4,
                                                                                     52,
                                                                                     input );

                               throw nvae;
                           }
                       };
        DFA.State s35  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               int LA4_35 = input.LA( 1 );
                               if ( LA4_35 == RuleParser.EOL || LA4_35 == 15 ) {
                                   return DFA4.this.s52;
                               }
                               if ( LA4_35 == 26 ) {
                                   return DFA4.this.s53;
                               }

                               NoViableAltException nvae = new NoViableAltException( "",
                                                                                     4,
                                                                                     35,
                                                                                     input );

                               throw nvae;
                           }
                       };
        DFA.State s60  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 24 :
                                       return DFA4.this.s50;

                                   case 25 :
                                       return DFA4.this.s35;

                                   case EOL :
                                   case 15 :
                                       return DFA4.this.s60;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             60,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s75  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case EOL :
                                   case 15 :
                                       return DFA4.this.s60;

                                   case 24 :
                                       return DFA4.this.s50;

                                   case 25 :
                                       return DFA4.this.s35;

                                   case 59 :
                                       return DFA4.this.s59;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             75,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s59  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               int LA4_59 = input.LA( 1 );
                               if ( LA4_59 == 60 ) {
                                   return DFA4.this.s75;
                               }

                               NoViableAltException nvae = new NoViableAltException( "",
                                                                                     4,
                                                                                     59,
                                                                                     input );

                               throw nvae;
                           }
                       };
        DFA.State s102 = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 25 :
                                       return DFA4.this.s35;

                                   case 24 :
                                       return DFA4.this.s50;

                                   case EOL :
                                   case 15 :
                                       return DFA4.this.s102;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             102,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s84  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 59 :
                                       return DFA4.this.s59;

                                   case EOL :
                                   case 15 :
                                       return DFA4.this.s102;

                                   case 25 :
                                       return DFA4.this.s35;

                                   case 24 :
                                       return DFA4.this.s50;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             84,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s83  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 25 :
                                       return DFA4.this.s35;

                                   case 24 :
                                       return DFA4.this.s50;

                                   case EOL :
                                   case 15 :
                                       return DFA4.this.s83;

                                   case ID :
                                       return DFA4.this.s84;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             83,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s64  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 18 :
                                       return DFA4.this.s16;

                                   case 59 :
                                       return DFA4.this.s47;

                                   case EOL :
                                   case 15 :
                                       return DFA4.this.s83;

                                   case ID :
                                       return DFA4.this.s84;

                                   case 25 :
                                       return DFA4.this.s35;

                                   case 24 :
                                       return DFA4.this.s50;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             64,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s63  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               int LA4_63 = input.LA( 1 );
                               if ( LA4_63 == RuleParser.ID ) {
                                   return DFA4.this.s64;
                               }
                               if ( LA4_63 == RuleParser.EOL || LA4_63 == 15 ) {
                                   return DFA4.this.s63;
                               }

                               NoViableAltException nvae = new NoViableAltException( "",
                                                                                     4,
                                                                                     63,
                                                                                     input );

                               throw nvae;
                           }
                       };
        DFA.State s50  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               int LA4_50 = input.LA( 1 );
                               if ( LA4_50 == RuleParser.EOL || LA4_50 == 15 ) {
                                   return DFA4.this.s63;
                               }
                               if ( LA4_50 == RuleParser.ID ) {
                                   return DFA4.this.s64;
                               }

                               NoViableAltException nvae = new NoViableAltException( "",
                                                                                     4,
                                                                                     50,
                                                                                     input );

                               throw nvae;
                           }
                       };
        DFA.State s49  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 59 :
                                       return DFA4.this.s59;

                                   case EOL :
                                   case 15 :
                                       return DFA4.this.s60;

                                   case 24 :
                                       return DFA4.this.s50;

                                   case 25 :
                                       return DFA4.this.s35;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             49,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s48  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 24 :
                                       return DFA4.this.s50;

                                   case 25 :
                                       return DFA4.this.s35;

                                   case EOL :
                                   case 15 :
                                       return DFA4.this.s48;

                                   case ID :
                                       return DFA4.this.s49;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             48,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s54  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case EOL :
                                   case 15 :
                                       return DFA4.this.s48;

                                   case ID :
                                       return DFA4.this.s49;

                                   case 59 :
                                       return DFA4.this.s47;

                                   case 24 :
                                       return DFA4.this.s50;

                                   case 25 :
                                       return DFA4.this.s35;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             54,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s47  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               int LA4_47 = input.LA( 1 );
                               if ( LA4_47 == 60 ) {
                                   return DFA4.this.s54;
                               }

                               NoViableAltException nvae = new NoViableAltException( "",
                                                                                     4,
                                                                                     47,
                                                                                     input );

                               throw nvae;
                           }
                       };
        DFA.State s34  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 18 :
                                       return DFA4.this.s16;

                                   case 59 :
                                       return DFA4.this.s47;

                                   case EOL :
                                   case 15 :
                                       return DFA4.this.s48;

                                   case ID :
                                       return DFA4.this.s49;

                                   case 24 :
                                       return DFA4.this.s50;

                                   case 25 :
                                       return DFA4.this.s35;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             34,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s33  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case ID :
                                       return DFA4.this.s34;

                                   case EOL :
                                   case 15 :
                                       return DFA4.this.s33;

                                   case 25 :
                                       return DFA4.this.s35;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             33,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s25  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case EOL :
                                   case 15 :
                                       return DFA4.this.s33;

                                   case ID :
                                       return DFA4.this.s34;

                                   case 25 :
                                       return DFA4.this.s35;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             25,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s31  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               int LA4_31 = input.LA( 1 );
                               if ( LA4_31 == 23 ) {
                                   return DFA4.this.s25;
                               }
                               if ( LA4_31 == RuleParser.EOL || LA4_31 == 15 ) {
                                   return DFA4.this.s31;
                               }

                               NoViableAltException nvae = new NoViableAltException( "",
                                                                                     4,
                                                                                     31,
                                                                                     input );

                               throw nvae;
                           }
                       };
        DFA.State s24  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               int LA4_24 = input.LA( 1 );
                               if ( LA4_24 == RuleParser.EOL || LA4_24 == 15 ) {
                                   return DFA4.this.s31;
                               }
                               if ( LA4_24 == 23 ) {
                                   return DFA4.this.s25;
                               }

                               NoViableAltException nvae = new NoViableAltException( "",
                                                                                     4,
                                                                                     24,
                                                                                     input );

                               throw nvae;
                           }
                       };
        DFA.State s23  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 23 :
                                       return DFA4.this.s25;

                                   case EOL :
                                   case 15 :
                                       return DFA4.this.s23;

                                   case ID :
                                       return DFA4.this.s24;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             23,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s13  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case 18 :
                                       return DFA4.this.s16;

                                   case 59 :
                                       return DFA4.this.s17;

                                   case EOL :
                                   case 15 :
                                       return DFA4.this.s23;

                                   case ID :
                                       return DFA4.this.s24;

                                   case 23 :
                                       return DFA4.this.s25;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             13,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };
        DFA.State s12  = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               int LA4_12 = input.LA( 1 );
                               if ( LA4_12 == RuleParser.ID ) {
                                   return DFA4.this.s13;
                               }
                               if ( LA4_12 == RuleParser.EOL || LA4_12 == 15 ) {
                                   return DFA4.this.s12;
                               }

                               NoViableAltException nvae = new NoViableAltException( "",
                                                                                     4,
                                                                                     12,
                                                                                     input );

                               throw nvae;
                           }
                       };
        DFA.State s6   = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               int LA4_6 = input.LA( 1 );
                               if ( LA4_6 == RuleParser.EOL || LA4_6 == 15 ) {
                                   return DFA4.this.s12;
                               }
                               if ( LA4_6 == RuleParser.ID ) {
                                   return DFA4.this.s13;
                               }

                               NoViableAltException nvae = new NoViableAltException( "",
                                                                                     4,
                                                                                     6,
                                                                                     input );

                               throw nvae;
                           }
                       };
        DFA.State s8   = new DFA.State() {
                           {
                               this.alt = 2;
                           }
                       };
        DFA.State s0   = new DFA.State() {
                           public DFA.State transition(IntStream input) throws RecognitionException {
                               switch ( input.LA( 1 ) ) {
                                   case -1 :
                                   case EOL :
                                   case 15 :
                                   case 28 :
                                   case 30 :
                                       return DFA4.this.s1;

                                   case 17 :
                                       return DFA4.this.s4;

                                   case 21 :
                                       return DFA4.this.s5;

                                   case 22 :
                                       return DFA4.this.s6;

                                   case 20 :
                                       return DFA4.this.s8;

                                   default :
                                       NoViableAltException nvae = new NoViableAltException( "",
                                                                                             4,
                                                                                             0,
                                                                                             input );

                                       throw nvae;
                               }
                           }
                       };

    }

    class DFA12 extends DFA {
        public int predict(final IntStream input) throws RecognitionException {
            return predict( input,
                            this.s0 );
        }

        DFA.State s5 = new DFA.State() {
                         {
                             this.alt = 1;
                         }
                     };
        DFA.State s2 = new DFA.State() {
                         {
                             this.alt = 2;
                         }
                     };
        DFA.State s3 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             switch ( input.LA( 1 ) ) {
                                 case ID :
                                     return DFA12.this.s5;

                                 case EOL :
                                 case 15 :
                                     return DFA12.this.s3;

                                 case 23 :
                                     return DFA12.this.s2;

                                 default :
                                     NoViableAltException nvae = new NoViableAltException( "",
                                                                                           12,
                                                                                           3,
                                                                                           input );

                                     throw nvae;
                             }
                         }
                     };
        DFA.State s1 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             switch ( input.LA( 1 ) ) {
                                 case EOL :
                                 case 15 :
                                     return DFA12.this.s3;

                                 case 23 :
                                     return DFA12.this.s2;

                                 case ID :
                                 case 18 :
                                 case 59 :
                                     return DFA12.this.s5;

                                 default :
                                     NoViableAltException nvae = new NoViableAltException( "",
                                                                                           12,
                                                                                           1,
                                                                                           input );

                                     throw nvae;
                             }
                         }
                     };
        DFA.State s0 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             int LA12_0 = input.LA( 1 );
                             if ( LA12_0 == RuleParser.ID ) {
                                 return DFA12.this.s1;
                             }
                             if ( LA12_0 == RuleParser.EOL || LA12_0 == 15 ) {
                                 return DFA12.this.s2;
                             }

                             NoViableAltException nvae = new NoViableAltException( "",
                                                                                   12,
                                                                                   0,
                                                                                   input );

                             throw nvae;
                         }
                     };

    }

    class DFA13 extends DFA {
        public int predict(final IntStream input) throws RecognitionException {
            return predict( input,
                            this.s0 );
        }

        DFA.State s3 = new DFA.State() {
                         {
                             this.alt = 1;
                         }
                     };
        DFA.State s2 = new DFA.State() {
                         {
                             this.alt = 2;
                         }
                     };
        DFA.State s5 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             switch ( input.LA( 1 ) ) {
                                 case ID :
                                     return DFA13.this.s3;

                                 case EOL :
                                 case 15 :
                                     return DFA13.this.s5;

                                 case 24 :
                                 case 25 :
                                     return DFA13.this.s2;

                                 default :
                                     NoViableAltException nvae = new NoViableAltException( "",
                                                                                           13,
                                                                                           5,
                                                                                           input );

                                     throw nvae;
                             }
                         }
                     };
        DFA.State s9 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             switch ( input.LA( 1 ) ) {
                                 case EOL :
                                 case 15 :
                                     return DFA13.this.s5;

                                 case 24 :
                                 case 25 :
                                     return DFA13.this.s2;

                                 case 59 :
                                     return DFA13.this.s4;

                                 case ID :
                                     return DFA13.this.s3;

                                 default :
                                     NoViableAltException nvae = new NoViableAltException( "",
                                                                                           13,
                                                                                           9,
                                                                                           input );

                                     throw nvae;
                             }
                         }
                     };
        DFA.State s4 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             int LA13_4 = input.LA( 1 );
                             if ( LA13_4 == 60 ) {
                                 return DFA13.this.s9;
                             }

                             NoViableAltException nvae = new NoViableAltException( "",
                                                                                   13,
                                                                                   4,
                                                                                   input );

                             throw nvae;
                         }
                     };
        DFA.State s1 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             switch ( input.LA( 1 ) ) {
                                 case ID :
                                 case 18 :
                                     return DFA13.this.s3;

                                 case 59 :
                                     return DFA13.this.s4;

                                 case EOL :
                                 case 15 :
                                     return DFA13.this.s5;

                                 case 24 :
                                 case 25 :
                                     return DFA13.this.s2;

                                 default :
                                     NoViableAltException nvae = new NoViableAltException( "",
                                                                                           13,
                                                                                           1,
                                                                                           input );

                                     throw nvae;
                             }
                         }
                     };
        DFA.State s0 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             int LA13_0 = input.LA( 1 );
                             if ( LA13_0 == RuleParser.ID ) {
                                 return DFA13.this.s1;
                             }
                             if ( LA13_0 == RuleParser.EOL || LA13_0 == 15 ) {
                                 return DFA13.this.s2;
                             }

                             NoViableAltException nvae = new NoViableAltException( "",
                                                                                   13,
                                                                                   0,
                                                                                   input );

                             throw nvae;
                         }
                     };

    }

    class DFA14 extends DFA {
        public int predict(final IntStream input) throws RecognitionException {
            return predict( input,
                            this.s0 );
        }

        DFA.State s2 = new DFA.State() {
                         {
                             this.alt = 2;
                         }
                     };
        DFA.State s7 = new DFA.State() {
                         {
                             this.alt = 1;
                         }
                     };
        DFA.State s4 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             switch ( input.LA( 1 ) ) {
                                 case 24 :
                                 case 25 :
                                     return DFA14.this.s2;

                                 case EOL :
                                 case 15 :
                                     return DFA14.this.s4;

                                 case ID :
                                     return DFA14.this.s7;

                                 default :
                                     NoViableAltException nvae = new NoViableAltException( "",
                                                                                           14,
                                                                                           4,
                                                                                           input );

                                     throw nvae;
                             }
                         }
                     };
        DFA.State s9 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             switch ( input.LA( 1 ) ) {
                                 case EOL :
                                 case 15 :
                                     return DFA14.this.s4;

                                 case ID :
                                     return DFA14.this.s7;

                                 case 59 :
                                     return DFA14.this.s3;

                                 case 24 :
                                 case 25 :
                                     return DFA14.this.s2;

                                 default :
                                     NoViableAltException nvae = new NoViableAltException( "",
                                                                                           14,
                                                                                           9,
                                                                                           input );

                                     throw nvae;
                             }
                         }
                     };
        DFA.State s3 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             int LA14_3 = input.LA( 1 );
                             if ( LA14_3 == 60 ) {
                                 return DFA14.this.s9;
                             }

                             NoViableAltException nvae = new NoViableAltException( "",
                                                                                   14,
                                                                                   3,
                                                                                   input );

                             throw nvae;
                         }
                     };
        DFA.State s1 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             switch ( input.LA( 1 ) ) {
                                 case 59 :
                                     return DFA14.this.s3;

                                 case EOL :
                                 case 15 :
                                     return DFA14.this.s4;

                                 case 24 :
                                 case 25 :
                                     return DFA14.this.s2;

                                 case ID :
                                 case 18 :
                                     return DFA14.this.s7;

                                 default :
                                     NoViableAltException nvae = new NoViableAltException( "",
                                                                                           14,
                                                                                           1,
                                                                                           input );

                                     throw nvae;
                             }
                         }
                     };
        DFA.State s0 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             int LA14_0 = input.LA( 1 );
                             if ( LA14_0 == RuleParser.ID ) {
                                 return DFA14.this.s1;
                             }
                             if ( LA14_0 == RuleParser.EOL || LA14_0 == 15 ) {
                                 return DFA14.this.s2;
                             }

                             NoViableAltException nvae = new NoViableAltException( "",
                                                                                   14,
                                                                                   0,
                                                                                   input );

                             throw nvae;
                         }
                     };

    }

    class DFA44 extends DFA {
        public int predict(final IntStream input) throws RecognitionException {
            return predict( input,
                            this.s0 );
        }

        DFA.State s3 = new DFA.State() {
                         {
                             this.alt = 1;
                         }
                     };
        DFA.State s4 = new DFA.State() {
                         {
                             this.alt = 2;
                         }
                     };
        DFA.State s2 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             switch ( input.LA( 1 ) ) {
                                 case 32 :
                                     return DFA44.this.s3;

                                 case EOL :
                                 case 15 :
                                     return DFA44.this.s2;

                                 case 23 :
                                     return DFA44.this.s4;

                                 default :
                                     NoViableAltException nvae = new NoViableAltException( "",
                                                                                           44,
                                                                                           2,
                                                                                           input );

                                     throw nvae;
                             }
                         }
                     };
        DFA.State s1 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             switch ( input.LA( 1 ) ) {
                                 case EOL :
                                 case 15 :
                                     return DFA44.this.s2;

                                 case 32 :
                                     return DFA44.this.s3;

                                 case 18 :
                                 case 23 :
                                 case 59 :
                                     return DFA44.this.s4;

                                 default :
                                     NoViableAltException nvae = new NoViableAltException( "",
                                                                                           44,
                                                                                           1,
                                                                                           input );

                                     throw nvae;
                             }
                         }
                     };
        DFA.State s0 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             int LA44_0 = input.LA( 1 );
                             if ( LA44_0 == RuleParser.ID ) {
                                 return DFA44.this.s1;
                             }

                             NoViableAltException nvae = new NoViableAltException( "",
                                                                                   44,
                                                                                   0,
                                                                                   input );

                             throw nvae;
                         }
                     };

    }

    class DFA47 extends DFA {
        public int predict(final IntStream input) throws RecognitionException {
            return predict( input,
                            this.s0 );
        }

        DFA.State s2 = new DFA.State() {
                         {
                             this.alt = 1;
                         }
                     };
        DFA.State s3 = new DFA.State() {
                         {
                             this.alt = 2;
                         }
                     };
        DFA.State s1 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             switch ( input.LA( 1 ) ) {
                                 case ID :
                                     return DFA47.this.s2;

                                 case EOL :
                                 case 15 :
                                     return DFA47.this.s1;

                                 case 25 :
                                     return DFA47.this.s3;

                                 default :
                                     NoViableAltException nvae = new NoViableAltException( "",
                                                                                           47,
                                                                                           1,
                                                                                           input );

                                     throw nvae;
                             }
                         }
                     };
        DFA.State s0 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             switch ( input.LA( 1 ) ) {
                                 case EOL :
                                 case 15 :
                                     return DFA47.this.s1;

                                 case ID :
                                     return DFA47.this.s2;

                                 case 25 :
                                     return DFA47.this.s3;

                                 default :
                                     NoViableAltException nvae = new NoViableAltException( "",
                                                                                           47,
                                                                                           0,
                                                                                           input );

                                     throw nvae;
                             }
                         }
                     };

    }

    class DFA50 extends DFA {
        public int predict(final IntStream input) throws RecognitionException {
            return predict( input,
                            this.s0 );
        }

        DFA.State s2 = new DFA.State() {
                         {
                             this.alt = 2;
                         }
                     };
        DFA.State s3 = new DFA.State() {
                         {
                             this.alt = 1;
                         }
                     };
        DFA.State s1 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             switch ( input.LA( 1 ) ) {
                                 case EOL :
                                 case 15 :
                                     return DFA50.this.s1;

                                 case 25 :
                                     return DFA50.this.s2;

                                 case 24 :
                                     return DFA50.this.s3;

                                 default :
                                     NoViableAltException nvae = new NoViableAltException( "",
                                                                                           50,
                                                                                           1,
                                                                                           input );

                                     throw nvae;
                             }
                         }
                     };
        DFA.State s0 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             switch ( input.LA( 1 ) ) {
                                 case EOL :
                                 case 15 :
                                     return DFA50.this.s1;

                                 case 25 :
                                     return DFA50.this.s2;

                                 case 24 :
                                     return DFA50.this.s3;

                                 default :
                                     NoViableAltException nvae = new NoViableAltException( "",
                                                                                           50,
                                                                                           0,
                                                                                           input );

                                     throw nvae;
                             }
                         }
                     };

    }

    class DFA51 extends DFA {
        public int predict(final IntStream input) throws RecognitionException {
            return predict( input,
                            this.s0 );
        }

        DFA.State s4 = new DFA.State() {
                         {
                             this.alt = 2;
                         }
                     };
        DFA.State s3 = new DFA.State() {
                         {
                             this.alt = 1;
                         }
                     };
        DFA.State s2 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             switch ( input.LA( 1 ) ) {
                                 case 24 :
                                 case 25 :
                                 case 42 :
                                 case 43 :
                                 case 44 :
                                 case 45 :
                                 case 46 :
                                 case 47 :
                                 case 48 :
                                 case 49 :
                                 case 50 :
                                     return DFA51.this.s4;

                                 case EOL :
                                 case 15 :
                                     return DFA51.this.s2;

                                 case 32 :
                                     return DFA51.this.s3;

                                 default :
                                     NoViableAltException nvae = new NoViableAltException( "",
                                                                                           51,
                                                                                           2,
                                                                                           input );

                                     throw nvae;
                             }
                         }
                     };
        DFA.State s1 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             switch ( input.LA( 1 ) ) {
                                 case EOL :
                                 case 15 :
                                     return DFA51.this.s2;

                                 case 32 :
                                     return DFA51.this.s3;

                                 case 24 :
                                 case 25 :
                                 case 42 :
                                 case 43 :
                                 case 44 :
                                 case 45 :
                                 case 46 :
                                 case 47 :
                                 case 48 :
                                 case 49 :
                                 case 50 :
                                     return DFA51.this.s4;

                                 default :
                                     NoViableAltException nvae = new NoViableAltException( "",
                                                                                           51,
                                                                                           1,
                                                                                           input );

                                     throw nvae;
                             }
                         }
                     };
        DFA.State s0 = new DFA.State() {
                         public DFA.State transition(IntStream input) throws RecognitionException {
                             int LA51_0 = input.LA( 1 );
                             if ( LA51_0 == RuleParser.ID ) {
                                 return DFA51.this.s1;
                             }

                             NoViableAltException nvae = new NoViableAltException( "",
                                                                                   51,
                                                                                   0,
                                                                                   input );

                             throw nvae;
                         }
                     };

    }

    public static final BitSet FOLLOW_set_in_opt_eol41                       = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_compilation_unit57          = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_prolog_in_compilation_unit61           = new BitSet( new long[]{0x0000000000628012L} );
    public static final BitSet FOLLOW_rule_in_compilation_unit70             = new BitSet( new long[]{0x0000000000628012L} );
    public static final BitSet FOLLOW_query_in_compilation_unit83            = new BitSet( new long[]{0x0000000000628012L} );
    public static final BitSet FOLLOW_extra_statement_in_compilation_unit91  = new BitSet( new long[]{0x0000000000628012L} );
    public static final BitSet FOLLOW_opt_eol_in_prolog115                   = new BitSet( new long[]{0x0000000000738012L} );
    public static final BitSet FOLLOW_package_statement_in_prolog123         = new BitSet( new long[]{0x0000000000728012L} );
    public static final BitSet FOLLOW_extra_statement_in_prolog138           = new BitSet( new long[]{0x0000000000728012L} );
    public static final BitSet FOLLOW_expander_in_prolog144                  = new BitSet( new long[]{0x0000000000728012L} );
    public static final BitSet FOLLOW_opt_eol_in_prolog156                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_16_in_package_statement180             = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_package_statement182        = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_dotted_name_in_package_statement186    = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_15_in_package_statement188             = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_package_statement191        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_17_in_import_statement207              = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_import_statement209         = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_import_name_in_import_statement213     = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_15_in_import_statement215              = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_import_statement218         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_import_name249                   = new BitSet( new long[]{0x00000000000C0002L} );
    public static final BitSet FOLLOW_18_in_import_name255                   = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_ID_in_import_name259                   = new BitSet( new long[]{0x00000000000C0002L} );
    public static final BitSet FOLLOW_19_in_import_name269                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_20_in_expander289                      = new BitSet( new long[]{0x0000000000008032L} );
    public static final BitSet FOLLOW_dotted_name_in_expander294             = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_15_in_expander298                      = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_expander301                 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_21_in_global325                        = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_dotted_name_in_global329               = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_ID_in_global333                        = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_15_in_global335                        = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_global338                   = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_22_in_function362                      = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_function364                 = new BitSet( new long[]{0x0000000000008032L} );
    public static final BitSet FOLLOW_dotted_name_in_function369             = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_function373                 = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_ID_in_function377                      = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_function379                 = new BitSet( new long[]{0x0000000000800000L} );
    public static final BitSet FOLLOW_23_in_function388                      = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_function390                 = new BitSet( new long[]{0x0000000002008032L} );
    public static final BitSet FOLLOW_dotted_name_in_function400             = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_function404                 = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_argument_in_function408                = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_function410                 = new BitSet( new long[]{0x0000000003000000L} );
    public static final BitSet FOLLOW_24_in_function424                      = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_function426                 = new BitSet( new long[]{0x0000000000008032L} );
    public static final BitSet FOLLOW_dotted_name_in_function431             = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_function435                 = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_argument_in_function439                = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_function441                 = new BitSet( new long[]{0x0000000003000000L} );
    public static final BitSet FOLLOW_25_in_function466                      = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_function470                 = new BitSet( new long[]{0x0000000004000000L} );
    public static final BitSet FOLLOW_26_in_function474                      = new BitSet( new long[]{0x3FFFFFFFFFFFFFF2L} );
    public static final BitSet FOLLOW_curly_chunk_in_function481             = new BitSet( new long[]{0x0000000008000000L} );
    public static final BitSet FOLLOW_27_in_function490                      = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_function498                 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_opt_eol_in_query522                    = new BitSet( new long[]{0x0000000010000000L} );
    public static final BitSet FOLLOW_28_in_query528                         = new BitSet( new long[]{0x2000001AF0020120L} );
    public static final BitSet FOLLOW_word_in_query532                       = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_query534                    = new BitSet( new long[]{0x3FFFFFFFFFFFFFF2L} );
    public static final BitSet FOLLOW_expander_lhs_block_in_query550         = new BitSet( new long[]{0x0000000020000000L} );
    public static final BitSet FOLLOW_normal_lhs_block_in_query558           = new BitSet( new long[]{0x0000000020000000L} );
    public static final BitSet FOLLOW_29_in_query573                         = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_query575                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_opt_eol_in_rule598                     = new BitSet( new long[]{0x0000000040000000L} );
    public static final BitSet FOLLOW_30_in_rule604                          = new BitSet( new long[]{0x2000001AF0020120L} );
    public static final BitSet FOLLOW_word_in_rule608                        = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_rule610                     = new BitSet( new long[]{0x0000000500008012L} );
    public static final BitSet FOLLOW_rule_attributes_in_rule621             = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_rule631                     = new BitSet( new long[]{0x00000000A0008012L} );
    public static final BitSet FOLLOW_31_in_rule640                          = new BitSet( new long[]{0x0000000100008012L} );
    public static final BitSet FOLLOW_32_in_rule642                          = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_rule645                     = new BitSet( new long[]{0x3FFFFFFFFFFFFFF2L} );
    public static final BitSet FOLLOW_expander_lhs_block_in_rule663          = new BitSet( new long[]{0x0000000020008012L} );
    public static final BitSet FOLLOW_normal_lhs_block_in_rule672            = new BitSet( new long[]{0x0000000020008012L} );
    public static final BitSet FOLLOW_opt_eol_in_rule695                     = new BitSet( new long[]{0x0000000200000000L} );
    public static final BitSet FOLLOW_33_in_rule699                          = new BitSet( new long[]{0x0000000100008012L} );
    public static final BitSet FOLLOW_32_in_rule701                          = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_rule705                     = new BitSet( new long[]{0x3FFFFFFFFFFFFFF0L} );
    public static final BitSet FOLLOW_29_in_rule751                          = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_rule753                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_import_statement_in_extra_statement773 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_global_in_extra_statement778           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_function_in_extra_statement783         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_34_in_rule_attributes802               = new BitSet( new long[]{0x0000000100008012L} );
    public static final BitSet FOLLOW_32_in_rule_attributes805               = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_rule_attributes808          = new BitSet( new long[]{0x000001F801000002L} );
    public static final BitSet FOLLOW_24_in_rule_attributes815               = new BitSet( new long[]{0x000001F800000000L} );
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes820   = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_rule_attributes822          = new BitSet( new long[]{0x000001F801000002L} );
    public static final BitSet FOLLOW_salience_in_rule_attribute861          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_no_loop_in_rule_attribute871           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute882      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_duration_in_rule_attribute895          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_activation_group_in_rule_attribute909  = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute920        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_35_in_salience953                      = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_salience955                 = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_INT_in_salience959                     = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_15_in_salience961                      = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_salience964                 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_36_in_no_loop999                       = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_no_loop1001                 = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_15_in_no_loop1003                      = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_no_loop1006                 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_36_in_no_loop1031                      = new BitSet( new long[]{0x0000000000000080L} );
    public static final BitSet FOLLOW_BOOL_in_no_loop1035                    = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_no_loop1037                 = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_15_in_no_loop1039                      = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_no_loop1042                 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_37_in_auto_focus1088                   = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_auto_focus1090              = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_15_in_auto_focus1092                   = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_auto_focus1095              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_37_in_auto_focus1120                   = new BitSet( new long[]{0x0000000000000080L} );
    public static final BitSet FOLLOW_BOOL_in_auto_focus1124                 = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_auto_focus1126              = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_15_in_auto_focus1128                   = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_auto_focus1131              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_38_in_activation_group1173             = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_activation_group1175        = new BitSet( new long[]{0x0000000000000100L} );
    public static final BitSet FOLLOW_STRING_in_activation_group1179         = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_15_in_activation_group1181             = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_activation_group1184        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_39_in_agenda_group1213                 = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_agenda_group1215            = new BitSet( new long[]{0x0000000000000100L} );
    public static final BitSet FOLLOW_STRING_in_agenda_group1219             = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_15_in_agenda_group1221                 = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_agenda_group1224            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_40_in_duration1256                     = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_duration1258                = new BitSet( new long[]{0x0000000000000040L} );
    public static final BitSet FOLLOW_INT_in_duration1262                    = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_15_in_duration1264                     = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_duration1267                = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1293            = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_normal_lhs_block1295        = new BitSet( new long[]{0x0700000000808032L} );
    public static final BitSet FOLLOW_opt_eol_in_normal_lhs_block1307        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_paren_chunk_in_expander_lhs_block1348  = new BitSet( new long[]{0x0000000000000010L} );
    public static final BitSet FOLLOW_EOL_in_expander_lhs_block1352          = new BitSet( new long[]{0x3FFFFFFFFFFFFFF2L} );
    public static final BitSet FOLLOW_lhs_or_in_lhs1404                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_fact_binding_in_lhs_column1432         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_fact_in_lhs_column1441                 = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_fact_binding1473                 = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_fact_binding1483            = new BitSet( new long[]{0x0000000100000000L} );
    public static final BitSet FOLLOW_32_in_fact_binding1485                 = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_fact_binding1487            = new BitSet( new long[]{0x0000000000800020L} );
    public static final BitSet FOLLOW_fact_expression_in_fact_binding1491    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_23_in_fact_expression1523              = new BitSet( new long[]{0x0000000000800020L} );
    public static final BitSet FOLLOW_fact_expression_in_fact_expression1527 = new BitSet( new long[]{0x0000000002000000L} );
    public static final BitSet FOLLOW_25_in_fact_expression1530              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_fact_in_fact_expression1541            = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_fact_expression1543         = new BitSet( new long[]{0x0000020000000002L} );
    public static final BitSet FOLLOW_41_in_fact_expression1555              = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_fact_in_fact_expression1569            = new BitSet( new long[]{0x0000020000000002L} );
    public static final BitSet FOLLOW_dotted_name_in_fact1608                = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_fact1616                    = new BitSet( new long[]{0x0000000000800000L} );
    public static final BitSet FOLLOW_23_in_fact1624                         = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_fact1627                    = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_constraints_in_fact1633                = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_fact1652                    = new BitSet( new long[]{0x0000000002000000L} );
    public static final BitSet FOLLOW_25_in_fact1654                         = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_fact1656                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_opt_eol_in_constraints1681             = new BitSet( new long[]{0x0000000000008032L} );
    public static final BitSet FOLLOW_constraint_in_constraints1686          = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_predicate_in_constraints1689           = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_constraints1697             = new BitSet( new long[]{0x0000000001000000L} );
    public static final BitSet FOLLOW_24_in_constraints1699                  = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_constraints1701             = new BitSet( new long[]{0x0000000000008032L} );
    public static final BitSet FOLLOW_constraint_in_constraints1704          = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_predicate_in_constraints1707           = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_constraints1715             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_opt_eol_in_constraint1734              = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_ID_in_constraint1742                   = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_constraint1744              = new BitSet( new long[]{0x0000000100000000L} );
    public static final BitSet FOLLOW_32_in_constraint1746                   = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_constraint1748              = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_ID_in_constraint1758                   = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_constraint1768              = new BitSet( new long[]{0x0007FC0000008012L} );
    public static final BitSet FOLLOW_set_in_constraint1776                  = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_constraint1863              = new BitSet( new long[]{0x00080000008003E0L} );
    public static final BitSet FOLLOW_ID_in_constraint1881                   = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_enum_constraint_in_constraint1906      = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_literal_constraint_in_constraint1938   = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_retval_constraint_in_constraint1958    = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_constraint1991              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_STRING_in_literal_constraint2018       = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_INT_in_literal_constraint2029          = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint2042        = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_BOOL_in_literal_constraint2053         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_51_in_literal_constraint2065           = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_enum_constraint2096              = new BitSet( new long[]{0x0000000000040000L} );
    public static final BitSet FOLLOW_18_in_enum_constraint2098              = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_ID_in_enum_constraint2102              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_23_in_retval_constraint2131            = new BitSet( new long[]{0x3FFFFFFFFFFFFFF2L} );
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint2135   = new BitSet( new long[]{0x0000000002000000L} );
    public static final BitSet FOLLOW_25_in_retval_constraint2137            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_predicate2155                    = new BitSet( new long[]{0x0000000100000000L} );
    public static final BitSet FOLLOW_32_in_predicate2157                    = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_ID_in_predicate2161                    = new BitSet( new long[]{0x0010000000000000L} );
    public static final BitSet FOLLOW_52_in_predicate2163                    = new BitSet( new long[]{0x0000000000800000L} );
    public static final BitSet FOLLOW_23_in_predicate2165                    = new BitSet( new long[]{0x3FFFFFFFFFFFFFF2L} );
    public static final BitSet FOLLOW_paren_chunk_in_predicate2169           = new BitSet( new long[]{0x0000000002000000L} );
    public static final BitSet FOLLOW_25_in_predicate2171                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_23_in_paren_chunk2217                  = new BitSet( new long[]{0x3FFFFFFFFFFFFFF2L} );
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk2221         = new BitSet( new long[]{0x0000000002000000L} );
    public static final BitSet FOLLOW_25_in_paren_chunk2223                  = new BitSet( new long[]{0x3FFFFFFFFFFFFFF2L} );
    public static final BitSet FOLLOW_23_in_paren_chunk22294                 = new BitSet( new long[]{0x3FFFFFFFFFFFFFF2L} );
    public static final BitSet FOLLOW_paren_chunk2_in_paren_chunk22298       = new BitSet( new long[]{0x0000000002000000L} );
    public static final BitSet FOLLOW_25_in_paren_chunk22300                 = new BitSet( new long[]{0x3FFFFFFFFFFFFFF2L} );
    public static final BitSet FOLLOW_26_in_curly_chunk2369                  = new BitSet( new long[]{0x3FFFFFFFFFFFFFF2L} );
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk2373         = new BitSet( new long[]{0x0000000008000000L} );
    public static final BitSet FOLLOW_27_in_curly_chunk2375                  = new BitSet( new long[]{0x3FFFFFFFFFFFFFF2L} );
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2433                  = new BitSet( new long[]{0x0020020000000002L} );
    public static final BitSet FOLLOW_set_in_lhs_or2442                      = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_lhs_or2447                  = new BitSet( new long[]{0x0700000000800020L} );
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2454                  = new BitSet( new long[]{0x0020020000000002L} );
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2494               = new BitSet( new long[]{0x00C0000000000002L} );
    public static final BitSet FOLLOW_set_in_lhs_and2503                     = new BitSet( new long[]{0x0000000000008012L} );
    public static final BitSet FOLLOW_opt_eol_in_lhs_and2508                 = new BitSet( new long[]{0x0700000000800020L} );
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2515               = new BitSet( new long[]{0x00C0000000000002L} );
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary2553             = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary2561               = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary2569              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary2577            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_23_in_lhs_unary2583                    = new BitSet( new long[]{0x0700000000800020L} );
    public static final BitSet FOLLOW_lhs_in_lhs_unary2587                   = new BitSet( new long[]{0x0000000002000000L} );
    public static final BitSet FOLLOW_25_in_lhs_unary2589                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_56_in_lhs_exist2619                    = new BitSet( new long[]{0x0000000000800020L} );
    public static final BitSet FOLLOW_23_in_lhs_exist2622                    = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist2626            = new BitSet( new long[]{0x0000000002000000L} );
    public static final BitSet FOLLOW_25_in_lhs_exist2628                    = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist2634            = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_57_in_lhs_not2664                      = new BitSet( new long[]{0x0000000000800020L} );
    public static final BitSet FOLLOW_23_in_lhs_not2667                      = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_lhs_column_in_lhs_not2671              = new BitSet( new long[]{0x0000000002000000L} );
    public static final BitSet FOLLOW_25_in_lhs_not2674                      = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_lhs_column_in_lhs_not2680              = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_58_in_lhs_eval2706                     = new BitSet( new long[]{0x0000000000800000L} );
    public static final BitSet FOLLOW_23_in_lhs_eval2710                     = new BitSet( new long[]{0x3FFFFFFFFFFFFFF2L} );
    public static final BitSet FOLLOW_paren_chunk2_in_lhs_eval2718           = new BitSet( new long[]{0x0000000002000000L} );
    public static final BitSet FOLLOW_25_in_lhs_eval2722                     = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_ID_in_dotted_name2754                  = new BitSet( new long[]{0x0800000000040002L} );
    public static final BitSet FOLLOW_18_in_dotted_name2760                  = new BitSet( new long[]{0x0000000000000020L} );
    public static final BitSet FOLLOW_ID_in_dotted_name2764                  = new BitSet( new long[]{0x0800000000040002L} );
    public static final BitSet FOLLOW_59_in_dotted_name2773                  = new BitSet( new long[]{0x1000000000000000L} );
    public static final BitSet FOLLOW_60_in_dotted_name2775                  = new BitSet( new long[]{0x0800000000000002L} );
    public static final BitSet FOLLOW_ID_in_argument2805                     = new BitSet( new long[]{0x0800000000000002L} );
    public static final BitSet FOLLOW_59_in_argument2811                     = new BitSet( new long[]{0x1000000000000000L} );
    public static final BitSet FOLLOW_60_in_argument2813                     = new BitSet( new long[]{0x0800000000000002L} );
    public static final BitSet FOLLOW_ID_in_word2841                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_17_in_word2853                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_61_in_word2862                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_30_in_word2874                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_28_in_word2885                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_35_in_word2895                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_36_in_word2903                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_31_in_word2911                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_33_in_word2922                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_29_in_word2933                         = new BitSet( new long[]{0x0000000000000002L} );
    public static final BitSet FOLLOW_STRING_in_word2947                     = new BitSet( new long[]{0x0000000000000002L} );

}