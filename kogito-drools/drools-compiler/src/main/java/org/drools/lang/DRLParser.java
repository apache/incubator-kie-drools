package org.drools.lang;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.MissingTokenException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.UnwantedTokenException;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.api.AccumulateDescrBuilder;
import org.drools.lang.api.AnnotatedDescrBuilder;
import org.drools.lang.api.AnnotationDescrBuilder;
import org.drools.lang.api.AttributeDescrBuilder;
import org.drools.lang.api.CEDescrBuilder;
import org.drools.lang.api.CollectDescrBuilder;
import org.drools.lang.api.DeclareDescrBuilder;
import org.drools.lang.api.DescrBuilder;
import org.drools.lang.api.DescrFactory;
import org.drools.lang.api.EvalDescrBuilder;
import org.drools.lang.api.FieldDescrBuilder;
import org.drools.lang.api.ForallDescrBuilder;
import org.drools.lang.api.FunctionDescrBuilder;
import org.drools.lang.api.GlobalDescrBuilder;
import org.drools.lang.api.ImportDescrBuilder;
import org.drools.lang.api.PackageDescrBuilder;
import org.drools.lang.api.ParameterSupportBuilder;
import org.drools.lang.api.PatternContainerDescrBuilder;
import org.drools.lang.api.PatternDescrBuilder;
import org.drools.lang.api.QueryDescrBuilder;
import org.drools.lang.api.RuleDescrBuilder;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.TypeDeclarationDescr;

public class DRLParser {

    private TokenStream           input;
    private RecognizerSharedState state;
    private ParserHelper          helper;
    private DRLExpressions        exprParser;

    public DRLParser(TokenStream input) {
        this.input = input;
        this.state = new RecognizerSharedState();
        this.helper = new ParserHelper( input,
                                        state );
        this.exprParser = new DRLExpressions( input,
                                              state,
                                              helper );
    }

    /* ------------------------------------------------------------------------------------------------
     *                         GENERAL INTERFACING METHODS
     * ------------------------------------------------------------------------------------------------ */
    public ParserHelper getHelper() {
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
        if ( state.backtracking == 0 ) {
            helper.reportError( ex );
        }
    }

    public void reportError( Exception ex ) {
        if ( state.backtracking == 0 ) {
            helper.reportError( ex );
        }
    }

    /* ------------------------------------------------------------------------------------------------
     *                         GRAMMAR RULES
     * ------------------------------------------------------------------------------------------------ */

    /**
     * Entry point method of a DRL compilation unit
     * 
     * compilationUnit := package_statement? statement*
     *   
     * @return a PackageDescr with the content of the whole compilation unit
     * 
     * @throws RecognitionException
     */
    public final PackageDescr compilationUnit() throws RecognitionException {
        PackageDescrBuilder pkg = DescrFactory.newPackage();

        try {
            helper.builderContext.push( pkg );

            // package declaration?
            if ( input.LA( 1 ) != DRLLexer.EOF && helper.validateIdentifierKey( DroolsSoftKeywords.PACKAGE ) ) {
                String pkgName = packageStatement();
                pkg.name( pkgName );
                if ( state.failed ) return pkg.getDescr();
            }

            // statements
            while ( input.LA( 1 ) != DRLLexer.EOF ) {
                int next = input.index();
                if ( helper.validateStatement( 1 ) ) {
                    statement();
                    if ( state.failed ) return pkg.getDescr();

                    if ( next == input.index() ) {
                        // no token consumed, so, report problem:
                        resyncToNextStatement();
                    }
                } else {
                    resyncToNextStatement();
                }

            }
        } catch ( RecognitionException e ) {
            helper.reportError( e );
        } catch ( Exception e ) {
            helper.reportError( e );
        } finally {
            helper.setEnd();
            helper.builderContext.pop();
        }
        return pkg.getDescr();
    }

    private void resyncToNextStatement() {
        helper.reportError( new DroolsMismatchedSetException( helper.getStatementKeywords(),
                                                              input ) );
        do {
            // error recovery: look for the next statement, skipping all tokens until then
            input.consume();
        } while ( input.LA( 1 ) != DRLLexer.EOF && !helper.validateStatement( 1 ) );
    }

    /**
     * Parses a package statement and returns the name of the package
     * or null if none is defined.
     * 
     * packageStatement := PACKAGE qualifiedIdentifier SEMICOLON?  
     * 
     * @return the name of the package or null if none is defined
     */
    public String packageStatement() throws RecognitionException {
        String pkgName = null;

        try {
            helper.start( PackageDescrBuilder.class,
                          null,
                          null );

            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.PACKAGE,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return pkgName;

            pkgName = qualifiedIdentifier();
            if ( state.failed ) return pkgName;
            if ( state.backtracking == 0 ) {
                helper.setParaphrasesValue( DroolsParaphraseTypes.PACKAGE,
                                            pkgName );
            }

            if ( input.LA( 1 ) == DRLLexer.SEMICOLON ) {
                match( input,
                       DRLLexer.SEMICOLON,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return pkgName;
            }

        } catch ( RecognitionException re ) {
            reportError( re );
        } finally {
            helper.end( PackageDescrBuilder.class,
                        null );
        }
        return pkgName;
    }

    /**
     * statement := importStatement
     *           |  globalStatement
     *           |  declare
     *           |  rule
     *           |  ruleAttribute
     *           |  function
     *           |  query
     *           ;
     *           
     * @throws RecognitionException
     */
    public BaseDescr statement() throws RecognitionException {
        BaseDescr descr = null;
        try {
            if ( helper.validateIdentifierKey( DroolsSoftKeywords.IMPORT ) ) {
                descr = importStatement();
                if ( state.failed ) return descr;
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.GLOBAL ) ) {
                descr = globalStatement();
                if ( state.failed ) return descr;
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.DECLARE ) ) {
                descr = declare();
                if ( state.failed ) return descr;
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.RULE ) ) {
                descr = rule();
                if ( state.failed ) return descr;
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.QUERY ) ) {
                descr = query();
                if ( state.failed ) return descr;
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.FUNCTION ) ) {
                descr = function();
                if ( state.failed ) return descr;
            } else if ( helper.validateAttribute( 1 ) ) {
                descr = attribute();
                if ( state.failed ) return descr;
            }
        } catch ( RecognitionException e ) {
            helper.reportError( e );
        } catch ( Exception e ) {
            helper.reportError( e );
        }
        return descr;
    }

    /* ------------------------------------------------------------------------------------------------
     *                         IMPORT STATEMENT
     * ------------------------------------------------------------------------------------------------ */

    /**
     * importStatement := IMPORT FUNCTION? qualifiedIdentifier (DOT STAR)? SEMICOLON?
     * 
     * @return
     * @throws RecognitionException
     */
    public ImportDescr importStatement() throws RecognitionException {
        ImportDescrBuilder imp = null;
        try {
            imp = helper.start( ImportDescrBuilder.class,
                                null,
                                null );

            // import
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.IMPORT,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            if ( helper.validateIdentifierKey( DroolsSoftKeywords.FUNCTION ) ) {
                // function
                match( input,
                       DRLLexer.ID,
                       DroolsSoftKeywords.FUNCTION,
                       null,
                       DroolsEditorType.KEYWORD );
                if ( state.failed ) return null;
            }

            // qualifiedIdentifier
            String target = qualifiedIdentifier();
            if ( state.failed ) return null;

            if ( input.LA( 1 ) == DRLLexer.DOT && input.LA( 2 ) == DRLLexer.STAR ) {
                // .*
                match( input,
                       DRLLexer.DOT,
                       null,
                       null,
                       DroolsEditorType.IDENTIFIER );
                if ( state.failed ) return null;
                match( input,
                       DRLLexer.STAR,
                       null,
                       null,
                       DroolsEditorType.IDENTIFIER );
                if ( state.failed ) return null;
                target += ".*";
            }
            if ( state.backtracking == 0 ) imp.target( target );

            if ( input.LA( 1 ) == DRLLexer.SEMICOLON ) {
                match( input,
                       DRLLexer.SEMICOLON,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return imp.getDescr();
            }

        } catch ( RecognitionException re ) {
            reportError( re );
        } finally {
            helper.end( ImportDescrBuilder.class,
                        null );
        }
        return (imp != null) ? imp.getDescr() : null;
    }

    /* ------------------------------------------------------------------------------------------------
     *                         GLOBAL STATEMENT
     * ------------------------------------------------------------------------------------------------ */

    /**
     * globalStatement := GLOBAL type ID SEMICOLON?
     * 
     * @return
     * @throws RecognitionException
     */
    public GlobalDescr globalStatement() throws RecognitionException {
        GlobalDescrBuilder global = null;
        try {
            global = helper.start( GlobalDescrBuilder.class,
                                   null,
                                   null );

            // 'global'
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.GLOBAL,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            // type
            String type = type();
            if ( state.backtracking == 0 ) global.type( type );
            if ( state.failed ) return null;

            // identifier
            Token id = match( input,
                              DRLLexer.ID,
                              null,
                              null,
                              DroolsEditorType.IDENTIFIER_TYPE );
            if ( state.failed ) return null;
            if ( state.backtracking == 0 ) {
                global.identifier( id.getText() );
                helper.setParaphrasesValue( DroolsParaphraseTypes.GLOBAL,
                                            id.getText() );
            }

            if ( input.LA( 1 ) == DRLLexer.SEMICOLON ) {
                match( input,
                       DRLLexer.SEMICOLON,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return global.getDescr();
            }

        } catch ( RecognitionException re ) {
            reportError( re );
        } finally {
            helper.end( GlobalDescrBuilder.class,
                        null );
        }
        return (global != null) ? global.getDescr() : null;
    }

    /* ------------------------------------------------------------------------------------------------
     *                         DECLARE STATEMENT
     * ------------------------------------------------------------------------------------------------ */

    /**
     * declare := DECLARE type annotation* field* END SEMICOLON?
     * 
     * @return
     * @throws RecognitionException
     */
    public TypeDeclarationDescr declare() throws RecognitionException {
        DeclareDescrBuilder declare = null;
        try {
            declare = helper.start( DeclareDescrBuilder.class,
                                    null,
                                    null );

            // 'declare'
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.DECLARE,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            // type
            String type = type();
            if ( state.failed ) return null;
            if ( state.backtracking == 0 ) declare.type( type );

            while ( input.LA( 1 ) == DRLLexer.AT ) {
                // metadata*
                annotation( declare );
                if ( state.failed ) return null;
            }

            while ( input.LA( 1 ) == DRLLexer.ID && !helper.validateIdentifierKey( DroolsSoftKeywords.END ) ) {
                // field*
                field( declare );
                if ( state.failed ) return null;
            }

            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.END,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            if ( input.LA( 1 ) == DRLLexer.SEMICOLON ) {
                match( input,
                       DRLLexer.SEMICOLON,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;
            }

        } catch ( RecognitionException re ) {
            reportError( re );
        } finally {
            helper.end( DeclareDescrBuilder.class,
                        null );
        }
        return (declare != null) ? declare.getDescr() : null;
    }

    /**
     * field := ID COLON type (EQUALS_ASSIGN expression)? annotation* SEMICOLON
     */
    private void field( DeclareDescrBuilder declare ) {
        FieldDescrBuilder field = null;
        try {
            // ID
            Token id = match( input,
                              DRLLexer.ID,
                              null,
                              null,
                              DroolsEditorType.IDENTIFIER );
            if ( state.failed ) return;

            field = helper.start( FieldDescrBuilder.class,
                                  id.getText(),
                                  null );

            match( input,
                   DRLLexer.COLON,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;

            // type
            String type = type();
            if ( state.failed ) return;
            if ( state.backtracking == 0 ) field.type( type );

            if ( input.LA( 1 ) == DRLLexer.EQUALS_ASSIGN ) {
                // EQUALS_ASSIGN
                match( input,
                       DRLLexer.EQUALS_ASSIGN,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return;

                int first = input.index();
                exprParser.conditionalExpression();
                if ( state.failed ) return;
                if ( state.backtracking == 0 && input.index() > first ) {
                    // expression consumed something
                    String value = input.toString( first,
                                                   input.LT( -1 ).getTokenIndex() );
                    field.initialValue( value );
                }
            }

            while ( input.LA( 1 ) == DRLLexer.AT ) {
                // annotation*
                annotation( field );
                if ( state.failed ) return;
            }

            if ( input.LA( 1 ) == DRLLexer.SEMICOLON ) {
                match( input,
                       DRLLexer.SEMICOLON,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return;
            }

        } catch ( RecognitionException re ) {
            reportError( re );
        } finally {
            helper.end( FieldDescrBuilder.class,
                        null );
        }
    }

    /* ------------------------------------------------------------------------------------------------
     *                         FUNCTION STATEMENT
     * ------------------------------------------------------------------------------------------------ */

    /**
     * function := FUNCTION type? ID arguments curly_chunk
     * 
     * @return
     * @throws RecognitionException
     */
    public FunctionDescr function() throws RecognitionException {
        FunctionDescrBuilder function = null;
        try {
            function = helper.start( FunctionDescrBuilder.class,
                                     null,
                                     null );

            // 'function'
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.FUNCTION,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            if ( input.LA( 1 ) != DRLLexer.ID || input.LA( 2 ) != DRLLexer.LEFT_PAREN ) {
                // type
                String type = type();
                if ( state.failed ) return null;
                if ( state.backtracking == 0 ) function.returnType( type );
            }

            // name
            Token id = match( input,
                              DRLLexer.ID,
                              null,
                              null,
                              DroolsEditorType.IDENTIFIER );
            if ( state.failed ) return null;
            if ( state.backtracking == 0 ) {
                function.name( id.getText() );
                helper.setParaphrasesValue( DroolsParaphraseTypes.FUNCTION,
                                            "\"" + id.getText() + "\"" );
            }

            // arguments
            parameters( function,
                        true );
            if ( state.failed ) return null;

            // body
            String body = chunk( DRLLexer.LEFT_CURLY,
                                 DRLLexer.RIGHT_CURLY );
            if ( state.failed ) return null;
            if ( state.backtracking == 0 ) function.body( body );

            if ( input.LA( 1 ) == DRLLexer.SEMICOLON ) {
                match( input,
                       DRLLexer.SEMICOLON,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;
            }

        } catch ( RecognitionException re ) {
            reportError( re );
        } finally {
            helper.end( DeclareDescrBuilder.class,
                        null );
        }
        return (function != null) ? function.getDescr() : null;
    }

    /**
     * parameters := LEFT_PAREN ( parameter ( COMMA parameter )* )? RIGHT_PAREN
     * @param statement
     * @param requiresType 
     * @throws RecognitionException 
     */
    private void parameters( ParameterSupportBuilder< ? > statement,
                             boolean requiresType ) throws RecognitionException {
        match( input,
               DRLLexer.LEFT_PAREN,
               null,
               null,
               DroolsEditorType.SYMBOL );
        if ( state.failed ) return;

        if ( input.LA( 1 ) != DRLLexer.RIGHT_PAREN ) {
            parameter( statement,
                       requiresType );
            if ( state.failed ) return;

            while ( input.LA( 1 ) == DRLLexer.COMMA ) {
                match( input,
                       DRLLexer.COMMA,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return;

                parameter( statement,
                           requiresType );
                if ( state.failed ) return;
            }
        }

        match( input,
               DRLLexer.RIGHT_PAREN,
               null,
               null,
               DroolsEditorType.SYMBOL );
        if ( state.failed ) return;
    }

    /**
     * parameter := ({requiresType}?=>type)? ID (LEFT_SQUARE RIGHT_SQUARE)*
     * @param statement
     * @param requiresType 
     * @throws RecognitionException
     */
    private void parameter( ParameterSupportBuilder< ? > statement,
                            boolean requiresType ) throws RecognitionException {
        String type = "Object";
        if ( requiresType ) {
            type = type();
            if ( state.failed ) return;
        }

        int start = input.index();
        match( input,
               DRLLexer.ID,
               null,
               null,
               DroolsEditorType.IDENTIFIER );
        if ( state.failed ) return;

        while ( input.LA( 1 ) == DRLLexer.LEFT_SQUARE ) {
            match( input,
                   DRLLexer.LEFT_SQUARE,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;

            match( input,
                   DRLLexer.RIGHT_SQUARE,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;
        }
        int end = input.LT( -1 ).getTokenIndex();

        if ( state.backtracking == 0 ) statement.parameter( type,
                                                            input.toString( start,
                                                                            end ) );
    }

    /* ------------------------------------------------------------------------------------------------
     *                         QUERY STATEMENT
     * ------------------------------------------------------------------------------------------------ */

    /**
     * query := QUERY stringId arguments? annotation* lhs END
     * 
     * @return
     * @throws RecognitionException
     */
    public RuleDescr query() throws RecognitionException {
        QueryDescrBuilder query = null;
        try {
            query = helper.start( QueryDescrBuilder.class,
                                  null,
                                  null );

            // 'query'
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.QUERY,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            String name = stringId();
            if ( state.backtracking == 0 ) query.name( name );
            if ( state.failed ) return null;

            if ( state.backtracking == 0 ) {
                helper.emit( Location.LOCATION_RULE_HEADER );
            }

            if ( speculateParameters( true ) ) {
                // parameters
                parameters( query,
                            true );
                if ( state.failed ) return null;
            } else if ( speculateParameters( false ) ) {
                // parameters
                parameters( query,
                            false );
                if ( state.failed ) return null;
            }

            while ( input.LA( 1 ) == DRLLexer.AT ) {
                // annotation*
                annotation( query );
                if ( state.failed ) return null;
            }

            lhsStatement( query != null ? query.lhs() : null );

            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.END,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            if ( input.LA( 1 ) == DRLLexer.SEMICOLON ) {
                match( input,
                       DRLLexer.SEMICOLON,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;
            }

        } catch ( RecognitionException re ) {
            reportError( re );
        } finally {
            helper.end( RuleDescrBuilder.class,
                        null );
        }
        return (query != null) ? query.getDescr() : null;
    }

    private boolean speculateParameters( boolean requiresType ) {
        state.backtracking++;
        int start = input.mark();
        try {
            parameters( null,
                        requiresType ); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
            re.printStackTrace();
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;
    }

    /* ------------------------------------------------------------------------------------------------
     *                         RULE STATEMENT
     * ------------------------------------------------------------------------------------------------ */

    /**
     * rule := RULE ruleId (EXTENDS ruleId)? annotation* attributes? lhs? rhs END
     * 
     * @return
     * @throws RecognitionException
     */
    public RuleDescr rule() throws RecognitionException {
        RuleDescrBuilder rule = null;
        try {
            rule = helper.start( RuleDescrBuilder.class,
                                 null,
                                 null );

            // 'rule'
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.RULE,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            String name = stringId();
            if ( state.backtracking == 0 ) {
                rule.name( name );
                helper.setParaphrasesValue( DroolsParaphraseTypes.RULE,
                                            "\"" + name + "\"" );
            }
            if ( state.failed ) return null;

            if ( helper.validateIdentifierKey( DroolsSoftKeywords.EXTENDS ) ) {
                // 'extends'
                match( input,
                       DRLLexer.ID,
                       DroolsSoftKeywords.EXTENDS,
                       null,
                       DroolsEditorType.KEYWORD );
                if ( state.failed ) return null;

                String parent = stringId();
                if ( state.backtracking == 0 ) rule.extendsRule( parent );
                if ( state.failed ) return null;
            }

            if ( state.backtracking == 0 ) {
                helper.emit( Location.LOCATION_RULE_HEADER );
            }

            while ( input.LA( 1 ) == DRLLexer.AT ) {
                // annotation*
                annotation( rule );
                if ( state.failed ) return null;
            }

            attributes( rule );

            if ( helper.validateIdentifierKey( DroolsSoftKeywords.WHEN ) ) {
                lhs( rule );
            } else {
                // creates an empty LHS
                rule.lhs();
            }

            rhs( rule );

            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.END,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            if ( input.LA( 1 ) == DRLLexer.SEMICOLON ) {
                match( input,
                       DRLLexer.SEMICOLON,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;
            }

        } catch ( RecognitionException re ) {
            reportError( re );
        } finally {
            helper.end( RuleDescrBuilder.class,
                        null );
        }
        return (rule != null) ? rule.getDescr() : null;
    }

    /**
     * ruleId := ( ID | STRING )
     * @return
     * @throws RecognitionException
     */
    private String stringId() throws RecognitionException {
        if ( input.LA( 1 ) == DRLLexer.ID ) {
            Token id = match( input,
                              DRLLexer.ID,
                              null,
                              null,
                              DroolsEditorType.IDENTIFIER );
            if ( state.failed ) return null;
            return id.getText();
        } else if ( input.LA( 1 ) == DRLLexer.STRING ) {
            Token id = match( input,
                              DRLLexer.STRING,
                              null,
                              null,
                              DroolsEditorType.IDENTIFIER );
            if ( state.failed ) return null;
            return safeStripStringDelimiters( id.getText() );
        } else {
            throw new MismatchedTokenException( DRLLexer.ID,
                                                input );
        }
    }

    /**
     * attributes := (ATTRIBUTES COMMA)? attribute ( COMMA? attribute )*
     * @param rule
     * @throws RecognitionException
     */
    private void attributes( RuleDescrBuilder rule ) throws RecognitionException {
        if ( helper.validateIdentifierKey( DroolsSoftKeywords.ATTRIBUTES ) ) {
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.ATTRIBUTES,
                   null,
                   DroolsEditorType.IDENTIFIER );
            if ( state.failed ) return;

            match( input,
                   DRLLexer.COLON,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;
        }

        if ( helper.validateAttribute( 1 ) ) {
            attribute();
            if ( state.failed ) return;

            if ( input.LA( 1 ) == DRLLexer.COMMA ) {
                match( input,
                       DRLLexer.COMMA,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return;
            }
            while ( helper.validateAttribute( 1 ) ) {
                attribute();
                if ( state.failed ) return;
            }
        }
    }

    /**
     * attribute :=
     *       salience 
     *   |   enabled 
     *   |   noLoop
     *   |   autoFocus 
     *   |   lockOnActive
     *   |   agendaGroup  
     *   |   activationGroup 
     *   |   ruleflowGroup 
     *   |   dateEffective 
     *   |   dateExpires 
     *   |   dialect 
     *   |   calendars    
     *   |   timer  
     * 
     * @return
     */
    public AttributeDescr attribute() {
        AttributeDescr attribute = null;
        try {
            if ( helper.validateIdentifierKey( DroolsSoftKeywords.SALIENCE ) ) {
                attribute = salience();
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.ENABLED ) ) {
                attribute = enabled();
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.NO ) &&
                        helper.validateLT( 2,
                                           "-" ) &&
                        helper.validateLT( 3,
                                           DroolsSoftKeywords.LOOP ) ) {
                attribute = booleanAttribute( new String[]{DroolsSoftKeywords.NO, "-", DroolsSoftKeywords.LOOP} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.AUTO ) &&
                        helper.validateLT( 2,
                                           "-" ) &&
                        helper.validateLT( 3,
                                           DroolsSoftKeywords.FOCUS ) ) {
                attribute = booleanAttribute( new String[]{DroolsSoftKeywords.AUTO, "-", DroolsSoftKeywords.FOCUS} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.LOCK ) &&
                        helper.validateLT( 2,
                                           "-" ) &&
                        helper.validateLT( 3,
                                           DroolsSoftKeywords.ON ) &&
                        helper.validateLT( 4,
                                           "-" ) &&
                        helper.validateLT( 5,
                                           DroolsSoftKeywords.ACTIVE ) ) {
                attribute = booleanAttribute( new String[]{DroolsSoftKeywords.LOCK, "-", DroolsSoftKeywords.ON, "-", DroolsSoftKeywords.ACTIVE} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.AGENDA ) &&
                        helper.validateLT( 2,
                                           "-" ) &&
                        helper.validateLT( 3,
                                           DroolsSoftKeywords.GROUP ) ) {
                attribute = stringAttribute( new String[]{DroolsSoftKeywords.AGENDA, "-", DroolsSoftKeywords.GROUP} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.ACTIVATION ) &&
                        helper.validateLT( 2,
                                           "-" ) &&
                        helper.validateLT( 3,
                                           DroolsSoftKeywords.GROUP ) ) {
                attribute = stringAttribute( new String[]{DroolsSoftKeywords.ACTIVATION, "-", DroolsSoftKeywords.GROUP} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.RULEFLOW ) &&
                        helper.validateLT( 2,
                                           "-" ) &&
                        helper.validateLT( 3,
                                           DroolsSoftKeywords.GROUP ) ) {
                attribute = stringAttribute( new String[]{DroolsSoftKeywords.RULEFLOW, "-", DroolsSoftKeywords.GROUP} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.DATE ) &&
                        helper.validateLT( 2,
                                           "-" ) &&
                        helper.validateLT( 3,
                                           DroolsSoftKeywords.EFFECTIVE ) ) {
                attribute = stringAttribute( new String[]{DroolsSoftKeywords.DATE, "-", DroolsSoftKeywords.EFFECTIVE} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.DATE ) &&
                        helper.validateLT( 2,
                                           "-" ) &&
                        helper.validateLT( 3,
                                           DroolsSoftKeywords.EXPIRES ) ) {
                attribute = stringAttribute( new String[]{DroolsSoftKeywords.DATE, "-", DroolsSoftKeywords.EXPIRES} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.DIALECT ) ) {
                attribute = stringAttribute( new String[]{DroolsSoftKeywords.DIALECT} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.CALENDARS ) ) {
                attribute = stringListAttribute( new String[]{DroolsSoftKeywords.CALENDARS} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.TIMER ) ) {
                attribute = intOrChunkAttribute( new String[]{DroolsSoftKeywords.TIMER} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.DURATION ) ) {
                attribute = intOrChunkAttribute( new String[]{DroolsSoftKeywords.DURATION} );
            }
        } catch ( RecognitionException re ) {
            reportError( re );
        }
        return attribute;
    }

    /**
     * salience := SALIENCE conditionalExpression
     * @param attribute
     * @throws RecognitionException
     */
    private AttributeDescr salience() throws RecognitionException {
        AttributeDescrBuilder attribute = null;
        try {
            // 'salience'
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.SALIENCE,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;
            if ( state.backtracking == 0 ) {
                attribute = helper.start( AttributeDescrBuilder.class,
                                          DroolsSoftKeywords.SALIENCE,
                                          null );
            }

            int first = input.index();
            exprParser.conditionalExpression();
            if ( state.failed ) return null;
            if ( state.backtracking == 0 && input.index() > first ) {
                // expression consumed something
                String value = input.toString( first,
                                               input.LT( -1 ).getTokenIndex() );
                attribute.value( value );
            }
        } finally {
            if ( attribute != null ) {
                helper.end( AttributeDescrBuilder.class,
                            null );
            }
        }
        return attribute != null ? attribute.getDescr() : null;
    }

    /**
     * enabled := ENABLED conditionalExpression
     * @param attribute
     * @throws RecognitionException
     */
    private AttributeDescr enabled() throws RecognitionException {
        AttributeDescrBuilder attribute = null;
        try {
            // 'enabled'
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.ENABLED,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;
            if ( state.backtracking == 0 ) {
                attribute = helper.start( AttributeDescrBuilder.class,
                                          DroolsSoftKeywords.ENABLED,
                                          null );
            }
            int first = input.index();
            exprParser.conditionalExpression();
            if ( state.failed ) return null;
            if ( state.backtracking == 0 && input.index() > first ) {
                // expression consumed something
                String value = input.toString( first,
                                               input.LT( -1 ).getTokenIndex() );
                attribute.value( value );
            }
        } finally {
            if ( attribute != null ) {
                helper.end( AttributeDescrBuilder.class,
                            null );
            }
        }
        return attribute != null ? attribute.getDescr() : null;
    }

    /**
     * booleanAttribute := attributeKey (BOOLEAN)?
     * @param attribute
     * @throws RecognitionException
     */
    private AttributeDescr booleanAttribute( String[] key ) throws RecognitionException {
        AttributeDescrBuilder attribute = null;
        try {
            StringBuilder builder = new StringBuilder();
            for ( String k : key ) {
                if ( "-".equals( k ) ) {
                    match( input,
                           DRLLexer.MINUS,
                           k,
                           null,
                           DroolsEditorType.KEYWORD ); // part of the keyword
                    if ( state.failed ) return null;
                } else {
                    match( input,
                           DRLLexer.ID,
                           k,
                           null,
                           DroolsEditorType.KEYWORD );
                    if ( state.failed ) return null;
                }
                builder.append( k );
            }
            if ( state.backtracking == 0 ) {
                attribute = helper.start( AttributeDescrBuilder.class,
                                          builder.toString(),
                                          null );
            }

            String value = "true";
            if ( input.LA( 1 ) == DRLLexer.BOOL ) {
                Token bool = match( input,
                                    DRLLexer.BOOL,
                                    null,
                                    null,
                                    DroolsEditorType.KEYWORD );
                if ( state.failed ) return null;
                value = bool.getText();
            }
            if ( state.backtracking == 0 ) {
                attribute.value( value );
            }
        } finally {
            if ( attribute != null ) {
                helper.end( AttributeDescrBuilder.class,
                            null );
            }
        }
        return attribute != null ? attribute.getDescr() : null;
    }

    /**
     * stringAttribute := attributeKey STRING
     * @param attribute
     * @throws RecognitionException
     */
    private AttributeDescr stringAttribute( String[] key ) throws RecognitionException {
        AttributeDescrBuilder attribute = null;
        try {
            StringBuilder builder = new StringBuilder();
            for ( String k : key ) {
                if ( "-".equals( k ) ) {
                    match( input,
                           DRLLexer.MINUS,
                           k,
                           null,
                           DroolsEditorType.KEYWORD ); // part of the keyword
                    if ( state.failed ) return null;
                } else {
                    match( input,
                           DRLLexer.ID,
                           k,
                           null,
                           DroolsEditorType.KEYWORD );
                    if ( state.failed ) return null;
                }
                builder.append( k );
            }
            if ( state.backtracking == 0 ) {
                attribute = helper.start( AttributeDescrBuilder.class,
                                          builder.toString(),
                                          null );
            }

            Token value = match( input,
                                 DRLLexer.STRING,
                                 null,
                                 null,
                                 DroolsEditorType.STRING_CONST );
            if ( state.failed ) return null;
            if ( state.backtracking == 0 ) {
                attribute.value( safeStripStringDelimiters( value.getText() ) );
            }
        } finally {
            if ( attribute != null ) {
                helper.end( AttributeDescrBuilder.class,
                            null );
            }
        }
        return attribute != null ? attribute.getDescr() : null;
    }

    /**
     * stringListAttribute := attributeKey STRING (COMMA STRING)*
     * @param attribute
     * @throws RecognitionException
     */
    private AttributeDescr stringListAttribute( String[] key ) throws RecognitionException {
        AttributeDescrBuilder attribute = null;
        try {
            StringBuilder builder = new StringBuilder();
            for ( String k : key ) {
                if ( "-".equals( k ) ) {
                    match( input,
                           DRLLexer.MINUS,
                           k,
                           null,
                           DroolsEditorType.KEYWORD ); // part of the keyword
                    if ( state.failed ) return null;
                } else {
                    match( input,
                           DRLLexer.ID,
                           k,
                           null,
                           DroolsEditorType.KEYWORD );
                    if ( state.failed ) return null;
                }
                builder.append( k );
            }
            if ( state.backtracking == 0 ) {
                attribute = helper.start( AttributeDescrBuilder.class,
                                          builder.toString(),
                                          null );
            }

            builder = new StringBuilder();
            builder.append( "[ " );
            Token value = match( input,
                                 DRLLexer.STRING,
                                 null,
                                 null,
                                 DroolsEditorType.STRING_CONST );
            if ( state.failed ) return null;
            builder.append( value.getText() );

            if ( input.LA( 1 ) == DRLLexer.COMMA ) {
                match( input,
                       DRLLexer.COMMA,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;
                builder.append( ", " );
                value = match( input,
                               DRLLexer.STRING,
                               null,
                               null,
                               DroolsEditorType.STRING_CONST );
                if ( state.failed ) return null;
                builder.append( value.getText() );
            }
            builder.append( " ]" );
            if ( state.backtracking == 0 ) {
                attribute.value( builder.toString() );
            }
        } finally {
            if ( attribute != null ) {
                helper.end( AttributeDescrBuilder.class,
                            null );
            }
        }
        return attribute != null ? attribute.getDescr() : null;
    }

    /**
     * intOrChunkAttribute := attributeKey (DECIMAL | parenChunk)
     * @param attribute
     * @throws RecognitionException
     */
    private AttributeDescr intOrChunkAttribute( String[] key ) throws RecognitionException {
        AttributeDescrBuilder attribute = null;
        try {
            StringBuilder builder = new StringBuilder();
            for ( String k : key ) {
                if ( "-".equals( k ) ) {
                    match( input,
                           DRLLexer.MINUS,
                           k,
                           null,
                           DroolsEditorType.KEYWORD ); // part of the keyword
                    if ( state.failed ) return null;
                } else {
                    match( input,
                           DRLLexer.ID,
                           k,
                           null,
                           DroolsEditorType.KEYWORD );
                    if ( state.failed ) return null;
                }
                builder.append( k );
            }
            if ( state.backtracking == 0 ) {
                attribute = helper.start( AttributeDescrBuilder.class,
                                          builder.toString(),
                                          null );
            }

            if ( input.LA( 1 ) == DRLLexer.LEFT_PAREN ) {
                String value = chunk( DRLLexer.LEFT_PAREN,
                                      DRLLexer.RIGHT_PAREN );
                if ( state.failed ) return null;
                if ( state.backtracking == 0 ) attribute.value( safeStripDelimiters( value,
                                                                                     new String[]{"(", ")"} ) );
            } else {
                String value = "";
                if ( input.LA( 1 ) == DRLLexer.PLUS ) {
                    Token sign = match( input,
                                        DRLLexer.PLUS,
                                        null,
                                        null,
                                        DroolsEditorType.NUMERIC_CONST );
                    if ( state.failed ) return null;
                    value += sign.getText();
                } else if ( input.LA( 1 ) == DRLLexer.MINUS ) {
                    Token sign = match( input,
                                        DRLLexer.MINUS,
                                        null,
                                        null,
                                        DroolsEditorType.NUMERIC_CONST );
                    if ( state.failed ) return null;
                    value += sign.getText();
                }
                Token nbr = match( input,
                                   DRLLexer.DECIMAL,
                                   null,
                                   null,
                                   DroolsEditorType.NUMERIC_CONST );
                if ( state.failed ) return null;
                value += nbr.getText();
                if ( state.backtracking == 0 ) attribute.value( value );
            }
        } finally {
            if ( attribute != null ) {
                helper.end( AttributeDescrBuilder.class,
                            null );
            }
        }
        return attribute != null ? attribute.getDescr() : null;
    }

    /**
     * lhs := WHEN COLON? lhsStatement*
     * @param rule
     * @throws RecognitionException
     */
    private void lhs( RuleDescrBuilder rule ) throws RecognitionException {
        match( input,
               DRLLexer.ID,
               DroolsSoftKeywords.WHEN,
               null,
               DroolsEditorType.KEYWORD );
        if ( state.failed ) return;

        if ( input.LA( 1 ) == DRLLexer.COLON ) {
            match( input,
                   DRLLexer.COLON,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;
        }

        lhsStatement( rule != null ? rule.lhs() : null );

    }

    /**
     * lhsStatement := lhsOr*
     * 
     * @param lhs
     * @throws RecognitionException 
     */
    private void lhsStatement( CEDescrBuilder< ? , AndDescr> lhs ) throws RecognitionException {
        while ( !helper.validateIdentifierKey( DroolsSoftKeywords.THEN ) &&
                !helper.validateIdentifierKey( DroolsSoftKeywords.END ) ) {
            helper.start( CEDescrBuilder.class,
                          null,
                          lhs );
            lhsOr( lhs );
            if ( state.failed ) return;
            helper.end( CEDescrBuilder.class,
                        lhs );
        }
    }

    /**
     * lhsOr := LEFT_PAREN OR lhsAnd+ RIGHT_PAREN
     *        | lhsAnd (OR lhsAnd)*
     *        
     * @param lhs
     * @throws RecognitionException 
     */
    private BaseDescr lhsOr( final CEDescrBuilder< ? , ? > ce ) throws RecognitionException {
        BaseDescr result = null;
        if ( input.LA( 1 ) == DRLLexer.LEFT_PAREN && helper.validateLT( 2,
                                                                        DroolsSoftKeywords.OR ) ) {
            CEDescrBuilder< ? , OrDescr> or = null;
            if ( state.backtracking == 0 ) {
                or = ce.or();
                result = or.getDescr();
                helper.start( CEDescrBuilder.class,
                              null,
                              or );
            }

            // prefixed OR
            match( input,
                   DRLLexer.LEFT_PAREN,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return null;

            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.OR,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            while ( input.LA( 1 ) != DRLLexer.RIGHT_PAREN ) {
                lhsAnd( or );
                if ( state.failed ) return null;
            }

            match( input,
                   DRLLexer.RIGHT_PAREN,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return null;

            if ( state.backtracking == 0 ) {
                helper.end( CEDescrBuilder.class,
                            or );
            }
        } else {
            // infix OR
            Token first = input.LT( 1 );

            result = lhsAnd( ce );
            if ( state.failed ) return null;

            if ( helper.validateIdentifierKey( DroolsSoftKeywords.OR ) ) {
                CEDescrBuilder< ? , OrDescr> or = null;
                if ( state.backtracking == 0 ) {
                    ((ConditionalElementDescr) ce.getDescr()).getDescrs().remove( result );
                    or = ce.or();
                    or.getDescr().addOrMerge( result );
                    result = or.getDescr();
                    helper.start( CEDescrBuilder.class,
                                  null,
                                  or );
                    // adjust start to the real start
                    helper.setStart( or,
                                     first );
                }
                while ( helper.validateIdentifierKey( DroolsSoftKeywords.OR ) ||
                        input.LA( 1 ) == DRLLexer.DOUBLE_PIPE ) {
                    if ( input.LA( 1 ) == DRLLexer.DOUBLE_PIPE ) {
                        match( input,
                               DRLLexer.DOUBLE_PIPE,
                               null,
                               null,
                               DroolsEditorType.SYMBOL );
                    } else {
                        match( input,
                               DRLLexer.ID,
                               DroolsSoftKeywords.OR,
                               null,
                               DroolsEditorType.KEYWORD );
                    }
                    if ( state.failed ) return null;

                    lhsAnd( or );
                    if ( state.failed ) return null;
                    if ( state.backtracking == 0 ) {
                        helper.end( CEDescrBuilder.class,
                                    or );
                    }
                }
            }
        }
        return result;
    }

    /**
     * lhsAnd:= LEFT_PAREN AND lhsUnary+ RIGHT_PAREN
     *        | lhsUnary (AND lhsUnary)*
     *        
     * @param ce
     * @throws RecognitionException 
     */
    private BaseDescr lhsAnd( final CEDescrBuilder< ? , ? > ce ) throws RecognitionException {
        BaseDescr result = null;
        if ( input.LA( 1 ) == DRLLexer.LEFT_PAREN && helper.validateLT( 2,
                                                                        DroolsSoftKeywords.AND ) ) {
            CEDescrBuilder< ? , AndDescr> and = null;
            if ( state.backtracking == 0 ) {
                and = ce.and();
                result = ce.getDescr();
                helper.start( CEDescrBuilder.class,
                              null,
                              and );
            }

            // prefixed AND
            match( input,
                   DRLLexer.LEFT_PAREN,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return null;

            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.AND,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            while ( input.LA( 1 ) != DRLLexer.RIGHT_PAREN ) {
                lhsUnary( and );
                if ( state.failed ) return null;
            }

            match( input,
                   DRLLexer.RIGHT_PAREN,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return null;

            if ( state.backtracking == 0 ) {
                helper.end( CEDescrBuilder.class,
                            and );
            }
        } else {
            // infix AND
            Token first = input.LT( 1 );

            result = lhsUnary( ce );
            if ( state.failed ) return null;

            if ( helper.validateIdentifierKey( DroolsSoftKeywords.AND ) ) {
                CEDescrBuilder< ? , AndDescr> and = null;
                if ( state.backtracking == 0 ) {
                    ((ConditionalElementDescr) ce.getDescr()).getDescrs().remove( result );
                    and = ce.and();
                    and.getDescr().addOrMerge( result );
                    result = and.getDescr();
                    helper.start( CEDescrBuilder.class,
                                  null,
                                  and );
                    // adjust start to the real start token
                    helper.setStart( and,
                                     first );
                }
                while ( helper.validateIdentifierKey( DroolsSoftKeywords.AND ) ||
                        input.LA( 1 ) == DRLLexer.DOUBLE_AMPER ) {
                    if ( input.LA( 1 ) == DRLLexer.DOUBLE_AMPER ) {
                        match( input,
                               DRLLexer.DOUBLE_AMPER,
                               null,
                               null,
                               DroolsEditorType.SYMBOL );
                    } else {
                        match( input,
                               DRLLexer.ID,
                               DroolsSoftKeywords.AND,
                               null,
                               DroolsEditorType.KEYWORD );
                    }
                    if ( state.failed ) return null;

                    lhsUnary( and );
                    if ( state.failed ) return null;

                    if ( state.backtracking == 0 ) {
                        helper.end( CEDescrBuilder.class,
                                    and );
                    }
                }
            }
        }
        return result;
    }

    /**
     * lhsUnary := 
     *           ( lhsExists
     *           | lhsNot
     *           | lhsEval
     *           | lhsForall
     *           | lhsAccumulate
     *           | LEFT_PAREN lhsOr RIGHT_PAREN
     *           | lhsPattern
     *           ) 
     *           SEMICOLON?
     * 
     * @param ce
     * @return
     */
    private BaseDescr lhsUnary( final CEDescrBuilder< ? , ? > ce ) throws RecognitionException {
        BaseDescr result = null;
        if ( helper.validateIdentifierKey( DroolsSoftKeywords.EXISTS ) ) {
            result = lhsExists( ce );
        } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.NOT ) ) {
            result = lhsNot( ce );
        } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.EVAL ) ) {
            result = lhsEval( ce );
        } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.FORALL ) ) {
            result = lhsForall( ce );
        } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.ACCUMULATE ) ) {
            // TODO: handle this
        } else if ( input.LA( 1 ) == DRLLexer.LEFT_PAREN ) {
            // the order here is very important: this if branch must come before the lhsPatternBind bellow
            result = lhsParen( ce );
        } else {
            result = lhsPatternBind( ce,
                                     true );
        }
        if ( input.LA( 1 ) == DRLLexer.SEMICOLON ) {
            match( input,
                   DRLLexer.SEMICOLON,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return null;
        }

        return result;
    }

    /**
     * lhsExists := EXISTS
     *           ( (LEFT_PAREN (or_key|and_key))=> lhsOr  // prevents '((' for prefixed and/or
     *           | LEFT_PAREN lhsOr RIGHT_PAREN 
     *           | lhsPattern
     *           )
     *  
     * @param ce
     * @return
     * @throws RecognitionException 
     */
    private BaseDescr lhsExists( CEDescrBuilder< ? , ? > ce ) throws RecognitionException {
        CEDescrBuilder< ? , ExistsDescr> exists = null;

        if ( state.backtracking == 0 ) {
            exists = ce.exists();
            helper.start( CEDescrBuilder.class,
                          null,
                          exists );
        }

        match( input,
               DRLLexer.ID,
               DroolsSoftKeywords.EXISTS,
               null,
               DroolsEditorType.KEYWORD );
        if ( state.failed ) return null;

        if ( input.LA( 1 ) == DRLLexer.LEFT_PAREN ) {
            boolean prefixed = helper.validateLT( 2,
                                                  DroolsSoftKeywords.AND ) || helper.validateLT( 2,
                                                                                                 DroolsSoftKeywords.OR );

            if ( !prefixed ) {
                match( input,
                        DRLLexer.LEFT_PAREN,
                        null,
                        null,
                        DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;
            }

            lhsOr( exists );
            if ( state.failed ) return null;

            if ( !prefixed ) {
                match( input,
                       DRLLexer.RIGHT_PAREN,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;
            }
        } else {

            lhsPatternBind( exists,
                            true );
            if ( state.failed ) return null;
        }

        if ( state.backtracking == 0 ) {
            helper.end( CEDescrBuilder.class,
                        exists );
        }

        return exists != null ? exists.getDescr() : null;
    }

    /**
     * lhsNot := NOT
     *           ( (LEFT_PAREN (or_key|and_key))=> lhsOr  // prevents '((' for prefixed and/or
     *           | LEFT_PAREN lhsOr RIGHT_PAREN 
     *           | lhsPattern
     *           )
     *  
     * @param ce
     * @return
     * @throws RecognitionException 
     */
    private BaseDescr lhsNot( CEDescrBuilder< ? , ? > ce ) throws RecognitionException {
        CEDescrBuilder< ? , NotDescr> not = null;

        if ( state.backtracking == 0 ) {
            not = ce.not();
            helper.start( CEDescrBuilder.class,
                          null,
                          not );
        }

        match( input,
               DRLLexer.ID,
               DroolsSoftKeywords.NOT,
               null,
               DroolsEditorType.KEYWORD );
        if ( state.failed ) return null;

        if ( input.LA( 1 ) == DRLLexer.LEFT_PAREN ) {
            boolean prefixed = helper.validateLT( 2,
                                                  DroolsSoftKeywords.AND ) || helper.validateLT( 2,
                                                                                                 DroolsSoftKeywords.OR );

            if ( !prefixed ) {
                match( input,
                        DRLLexer.LEFT_PAREN,
                        null,
                        null,
                        DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;
            }

            lhsOr( not );
            if ( state.failed ) return null;

            if ( !prefixed ) {
                match( input,
                       DRLLexer.RIGHT_PAREN,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;
            }
        } else {
            lhsPatternBind( not,
                            true );
            if ( state.failed ) return null;
        }

        if ( state.backtracking == 0 ) {
            helper.end( CEDescrBuilder.class,
                        not );
        }

        return not != null ? not.getDescr() : null;
    }

    /**
     * lhsForall := FORALL LEFT_PAREN lhsPattern+ RIGHT_PAREN 
     *  
     * @param ce
     * @return
     * @throws RecognitionException 
     */
    private BaseDescr lhsForall( CEDescrBuilder< ? , ? > ce ) throws RecognitionException {
        ForallDescrBuilder< ? > forall = helper.start( ForallDescrBuilder.class,
                                                       null,
                                                       null );

        try {
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.FORALL,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            match( input,
                   DRLLexer.LEFT_PAREN,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return null;

            do {
                lhsPatternBind( forall,
                                true );
                if ( state.failed ) return null;

                if ( input.LA( 1 ) == DRLLexer.COMMA ) {
                    match( input,
                           DRLLexer.COMMA,
                           null,
                           null,
                           DroolsEditorType.SYMBOL );
                    if ( state.failed ) return null;
                }
            } while ( input.LA( 1 ) != DRLLexer.EOF && input.LA( 1 ) != DRLLexer.RIGHT_PAREN );

            match( input,
                   DRLLexer.RIGHT_PAREN,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return null;
        } finally {
            helper.end( ForallDescrBuilder.class,
                        null );
        }

        return forall != null ? forall.getDescr() : null;
    }

    /**
     * lhsEval := EVAL LEFT_PAREN expression RIGHT_PAREN
     *  
     * @param ce
     * @return
     * @throws RecognitionException 
     */
    private BaseDescr lhsEval( CEDescrBuilder< ? , ? > ce ) throws RecognitionException {
        EvalDescrBuilder< ? > eval = null;

        try {
            eval = helper.start( EvalDescrBuilder.class,
                                 null,
                                 null );

            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.EVAL,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            match( input,
                   DRLLexer.LEFT_PAREN,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return null;

            String expr = conditionalExpression();
            if ( state.backtracking == 0 ) eval.constraint( expr );

            match( input,
                   DRLLexer.RIGHT_PAREN,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return null;
        } finally {
            helper.end( EvalDescrBuilder.class,
                        null );
        }

        return eval != null ? eval.getDescr() : null;
    }

    /**
     * lhsParen := LEFT_PAREN lhsOr RIGHT_PAREN 
     *  
     * @param ce
     * @return
     * @throws RecognitionException 
     */
    private BaseDescr lhsParen( CEDescrBuilder< ? , ? > ce ) throws RecognitionException {
        match( input,
               DRLLexer.LEFT_PAREN,
               null,
               null,
               DroolsEditorType.SYMBOL );
        if ( state.failed ) return null;

        BaseDescr descr = lhsOr( ce );
        if ( state.failed ) return null;

        match( input,
               DRLLexer.RIGHT_PAREN,
               null,
               null,
               DroolsEditorType.SYMBOL );
        if ( state.failed ) return null;

        return descr;
    }

    /**
     * lhsPatternBind := label? 
     *                ( LEFT_PAREN lhsPattern (OR pattern)* RIGHT_PAREN
     *                | lhsPattern )
     *  
     * @param ce
     * @return
     * @throws RecognitionException 
     */
    @SuppressWarnings("unchecked")
    private BaseDescr lhsPatternBind( PatternContainerDescrBuilder< ? , ? > ce,
                                      final boolean allowOr ) throws RecognitionException {
        PatternDescrBuilder< ? > pattern = null;
        CEDescrBuilder< ? , OrDescr> or = null;
        BaseDescr result = null;

        Token first = input.LT( 1 );
        pattern = helper.start( PatternDescrBuilder.class,
                                null,
                                null );
        if ( pattern != null ) {
            result = pattern.getDescr();
        }

        String label = null;
        if ( input.LA( 1 ) == DRLLexer.ID && input.LA( 2 ) == DRLLexer.COLON ) {
            label = label();
            if ( state.failed ) return null;
        }

        if ( input.LA( 1 ) == DRLLexer.LEFT_PAREN ) {
            try {
                match( input,
                       DRLLexer.LEFT_PAREN,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;

                lhsPattern( pattern,
                            label );
                if ( state.failed ) return null;

                if ( allowOr && helper.validateIdentifierKey( DroolsSoftKeywords.OR ) && ce instanceof CEDescrBuilder ) {
                    if ( state.backtracking == 0 ) {
                        // this is necessary because of the crappy bind with multi-pattern OR syntax 
                        or = ((CEDescrBuilder<DescrBuilder< ? >, OrDescr>) ce).or();
                        result = or.getDescr();

                        helper.end( PatternDescrBuilder.class,
                                    null );
                        helper.start( CEDescrBuilder.class,
                                      null,
                                      or );
                        // adjust real or starting token:
                        helper.setStart( or,
                                         first );

                        // remove original pattern from the parent CE child list:
                        ((ConditionalElementDescr) ce.getDescr()).getDescrs().remove( pattern.getDescr() );
                        // add pattern to the OR instead
                        or.getDescr().addDescr( pattern.getDescr() );
                    }

                    while ( helper.validateIdentifierKey( DroolsSoftKeywords.OR ) ) {
                        match( input,
                               DRLLexer.ID,
                               DroolsSoftKeywords.OR,
                               null,
                               DroolsEditorType.KEYWORD );
                        if ( state.failed ) return null;

                        pattern = helper.start( PatternDescrBuilder.class,
                                                null,
                                                null );
                        // new pattern, same binding
                        lhsPattern( pattern,
                                    label );
                        if ( state.failed ) return null;

                        helper.end( PatternDescrBuilder.class,
                                    null );
                    }
                }

                match( input,
                       DRLLexer.RIGHT_PAREN,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;

            } finally {
                if ( or != null ) {
                    helper.end( CEDescrBuilder.class,
                                or );
                } else {
                    helper.end( PatternDescrBuilder.class,
                                null );
                }
            }

        } else {
            try {
                lhsPattern( pattern,
                            label );
                if ( state.failed ) return null;

            } finally {
                helper.end( PatternDescrBuilder.class,
                            null );
            }
        }

        return result;
    }

    /**
     * lhsPattern := type LEFT_PAREN constraints? RIGHT_PAREN over? source?
     * 
     * @param pattern
     * @param label
     * @throws RecognitionException
     */
    private void lhsPattern( PatternDescrBuilder< ? > pattern,
                             String label ) throws RecognitionException {
        String type = type();
        if ( state.failed ) return;

        if ( state.backtracking == 0 ) {
            pattern.type( type );
            if ( label != null ) {
                pattern.id( label );
            }
        }

        match( input,
               DRLLexer.LEFT_PAREN,
               null,
               null,
               DroolsEditorType.SYMBOL );
        if ( state.failed ) return;

        if ( input.LA( 1 ) != DRLLexer.RIGHT_PAREN ) {
            constraints( pattern );
        }

        match( input,
               DRLLexer.RIGHT_PAREN,
               null,
               null,
               DroolsEditorType.SYMBOL );
        if ( state.failed ) return;

        if ( helper.validateIdentifierKey( DroolsSoftKeywords.OVER ) ) {
            // TODO: over clause
        }

        if ( helper.validateIdentifierKey( DroolsSoftKeywords.FROM ) ) {
            patternSource( pattern );
        }
    }

    /**
     * label := ID COLON
     * @return
     * @throws RecognitionException 
     */
    private String label() throws RecognitionException {
        Token label = match( input,
                             DRLLexer.ID,
                             null,
                             null,
                             DroolsEditorType.IDENTIFIER_PATTERN );
        if ( state.failed ) return null;

        match( input,
               DRLLexer.COLON,
               null,
               null,
               DroolsEditorType.SYMBOL );
        if ( state.failed ) return null;

        return label.getText();
    }

    /**
     * constraints := constraint (COMMA constraint)*
     * @param pattern
     * @throws RecognitionException 
     */
    private void constraints( PatternDescrBuilder< ? > pattern ) throws RecognitionException {
        constraint( pattern );
        if ( state.failed ) return;

        while ( input.LA( 1 ) == DRLLexer.COMMA ) {
            match( input,
                   DRLLexer.COMMA,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;

            constraint( pattern );
            if ( state.failed ) return;
        }
    }

    /**
     * constraint := conditionalExpression
     * @param pattern
     * @throws RecognitionException 
     */
    private void constraint( PatternDescrBuilder< ? > pattern ) throws RecognitionException {
        String bind = null;
        if ( input.LA( 1 ) == DRLLexer.ID && input.LA( 2 ) == DRLLexer.COLON ) {
            // bind
            Token id = match( input,
                              DRLLexer.ID,
                              null,
                              null,
                              DroolsEditorType.IDENTIFIER_VARIABLE );
            if ( state.failed ) return;

            bind = id.getText();

            match( input,
                   DRLLexer.COLON,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;
        }

        int first = input.index();
        exprParser.conditionalOrExpression();
        if ( state.failed ) return;

        if ( state.backtracking == 0 && input.index() > first ) {
            // expression consumed something
            String expr = input.toString( first,
                                          input.LT( -1 ).getTokenIndex() );
            if ( bind == null ) {
                // it is a constraint
                pattern.constraint( expr );
            } else {
                // it is a bind
                pattern.bind( bind,
                              expr );
            }
        }
    }

    /**
     * patternSource := FROM
     *                ( accumulate
     *                | collect
     *                | entryPoint
     *                | expression )
     * @param pattern
     * @throws RecognitionException 
     */
    private void patternSource( PatternDescrBuilder< ? > pattern ) throws RecognitionException {
        match( input,
               DRLLexer.ID,
               DroolsSoftKeywords.FROM,
               null,
               DroolsEditorType.KEYWORD );
        if ( state.failed ) return;

        if ( helper.validateIdentifierKey( DroolsSoftKeywords.ACCUMULATE ) ) {
            fromAccumulate( pattern );
        } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.COLLECT ) ) {
            fromCollect( pattern );
        } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.ENTRY ) &&
                    helper.validateLT( 2,
                                       "-" ) &&
                    helper.validateLT( 3,
                                       DroolsSoftKeywords.POINT ) ) {
            fromEntryPoint( pattern );
            if ( state.failed ) return;
        } else {
            fromExpression( pattern );
            if ( state.failed ) return;
        }
    }

    /**
     * fromExpression := conditionalExpression
     * 
     * @param pattern
     * @throws RecognitionException
     */
    private void fromExpression( PatternDescrBuilder< ? > pattern ) throws RecognitionException {
        String expr = conditionalExpression();
        if ( state.failed ) return;

        if ( state.backtracking == 0 ) {
            pattern.from().expression( expr );
        }
    }

    /**
     * fromEntryPoint := ENTRY-POINT (STRING | ID)
     * 
     * @param pattern
     * @throws RecognitionException
     */
    private void fromEntryPoint( PatternDescrBuilder< ? > pattern ) throws RecognitionException {
        String ep = "";

        match( input,
               DRLLexer.ID,
               DroolsSoftKeywords.ENTRY,
               null,
               DroolsEditorType.KEYWORD );
        if ( state.failed ) return;

        match( input,
               DRLLexer.MINUS,
               null,
               null,
               DroolsEditorType.KEYWORD );
        if ( state.failed ) return;

        match( input,
               DRLLexer.ID,
               DroolsSoftKeywords.POINT,
               null,
               DroolsEditorType.KEYWORD );
        if ( state.failed ) return;

        if ( input.LA( 1 ) == DRLLexer.STRING ) {
            Token epStr = match( input,
                                 DRLLexer.STRING,
                                 null,
                                 null,
                                 DroolsEditorType.STRING_CONST );
            ep = safeStripStringDelimiters( epStr.getText() );
        } else {
            Token epID = match( input,
                                DRLLexer.ID,
                                null,
                                null,
                                DroolsEditorType.IDENTIFIER );
            ep = epID.getText();
        }

        if ( state.backtracking == 0 ) {
            pattern.from().entryPoint( ep );
        }
    }

    /**
     * fromCollect := COLLECT LEFT_PAREN lhsPatternBind RIGHT_PAREN
     * 
     * @param pattern
     * @throws RecognitionException
     */
    private void fromCollect( PatternDescrBuilder< ? > pattern ) throws RecognitionException {
        CollectDescrBuilder< ? > collect = helper.start( CollectDescrBuilder.class,
                                                         null,
                                                         null );
        try {
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.COLLECT,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return;

            match( input,
                   DRLLexer.LEFT_PAREN,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;

            lhsPatternBind( collect,
                            false );
            if ( state.failed ) return;

            match( input,
                   DRLLexer.RIGHT_PAREN,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;
        } finally {
            helper.end( CollectDescrBuilder.class,
                        null );
        }
    }

    /**
     * fromAccumulate := ACCUMULATE LEFT_PAREN lhsAnd COMMA 
     *                   ( initBlock COMMA actionBlock COMMA (reverseBlock COMMA)? resultBlock
     *                   | accumulateFunction (COMMA accumulateFunction)* )
     *                   RIGHT_PAREN
     * 
     * @param pattern
     * @throws RecognitionException
     */
    private void fromAccumulate( PatternDescrBuilder< ? > pattern ) throws RecognitionException {
        AccumulateDescrBuilder< ? > accumulate = helper.start( AccumulateDescrBuilder.class,
                                                               null,
                                                               null );
        try {
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.ACCUMULATE,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return;

            match( input,
                   DRLLexer.LEFT_PAREN,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;

            lhsAnd( accumulate.source() );
            if ( state.failed ) return;

            match( input,
                   DRLLexer.COMMA,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;

            if ( helper.validateIdentifierKey( DroolsSoftKeywords.INIT ) ) {
                // custom code, inline accumulate

                // initBlock
                match( input,
                       DRLLexer.ID,
                       DroolsSoftKeywords.INIT,
                       null,
                       DroolsEditorType.KEYWORD );
                if ( state.failed ) return;

                String init = chunk( DRLLexer.LEFT_PAREN,
                                     DRLLexer.RIGHT_PAREN );
                if ( state.failed ) return;
                if ( state.backtracking == 0 ) accumulate.init( init );

                // actionBlock
                match( input,
                       DRLLexer.ID,
                       DroolsSoftKeywords.ACTION,
                       null,
                       DroolsEditorType.KEYWORD );
                if ( state.failed ) return;

                String action = chunk( DRLLexer.LEFT_PAREN,
                                       DRLLexer.RIGHT_PAREN );
                if ( state.failed ) return;
                if ( state.backtracking == 0 ) accumulate.action( action );

                // reverseBlock
                if ( helper.validateIdentifierKey( DroolsSoftKeywords.REVERSE ) ) {
                    match( input,
                           DRLLexer.ID,
                           DroolsSoftKeywords.ACTION,
                           null,
                           DroolsEditorType.KEYWORD );
                    if ( state.failed ) return;

                    String reverse = chunk( DRLLexer.LEFT_PAREN,
                                            DRLLexer.RIGHT_PAREN );
                    if ( state.failed ) return;
                    if ( state.backtracking == 0 ) accumulate.reverse( reverse );
                }

                // resultBlock
                match( input,
                       DRLLexer.ID,
                       DroolsSoftKeywords.RESULT,
                       null,
                       DroolsEditorType.KEYWORD );
                if ( state.failed ) return;

                String result = chunk( DRLLexer.LEFT_PAREN,
                                       DRLLexer.RIGHT_PAREN );
                if ( state.failed ) return;
                if ( state.backtracking == 0 ) accumulate.result( result );
            } else {
                // accumulate functions
                accumulateFunction( accumulate );
                if ( state.failed ) return;

                while ( input.LA( 1 ) == DRLLexer.COMMA ) {
                    match( input,
                           DRLLexer.COMMA,
                           null,
                           null,
                           DroolsEditorType.SYMBOL );
                    if ( state.failed ) return;

                    accumulateFunction( accumulate );
                    if ( state.failed ) return;
                }
            }

            match( input,
                   DRLLexer.RIGHT_PAREN,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;
        } finally {
            helper.end( AccumulateDescrBuilder.class,
                        null );
        }
    }

    /**
     * accumulateFunction := ID parameters
     * @param accumulate
     * @throws RecognitionException
     */
    private void accumulateFunction( AccumulateDescrBuilder< ? > accumulate ) throws RecognitionException {
        Token function = match( input,
                                DRLLexer.ID,
                                null,
                                null,
                                DroolsEditorType.KEYWORD );
        if ( state.failed ) return;

        List<String> parameters = parameters();
        if ( state.failed ) return;

        if ( state.backtracking == 0 ) {
            accumulate.function( function.getText(),
                                 parameters.toArray( new String[parameters.size()] ) );
        }
    }

    /**
     * parameters := LEFT_PAREN (conditionalExpression (COMMA conditionalExpression)* )? RIGHT_PAREN
     * 
     * @return
     * @throws RecognitionException
     */
    private List<String> parameters() throws RecognitionException {
        match( input,
               DRLLexer.LEFT_PAREN,
               null,
               null,
               DroolsEditorType.SYMBOL );
        if ( state.failed ) return null;

        List<String> parameters = new ArrayList<String>();
        if ( input.LA( 1 ) != DRLLexer.EOF && input.LA( 1 ) != DRLLexer.RIGHT_PAREN ) {
            String param = conditionalExpression();
            if ( state.failed ) return null;
            parameters.add( param );

            while ( input.LA( 1 ) == DRLLexer.COMMA ) {
                match( input,
                       DRLLexer.COMMA,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;

                param = conditionalExpression();
                if ( state.failed ) return null;
                parameters.add( param );
            }
        }

        match( input,
               DRLLexer.RIGHT_PAREN,
               null,
               null,
               DroolsEditorType.SYMBOL );
        if ( state.failed ) return null;
        return parameters;
    }

    /**
     * rhs := THEN (~END)*
     * @param rule
     * @throws RecognitionException
     */
    private void rhs( RuleDescrBuilder rule ) throws RecognitionException {
        String chunk = "";
        int first = -1;
        Token last = null;
        try {
            first = input.index();
            Token t = match( input,
                             DRLLexer.ID,
                             DroolsSoftKeywords.THEN,
                             null,
                             DroolsEditorType.KEYWORD );
            if ( state.failed ) return;

            if ( state.backtracking == 0 ) {
                rule.getDescr().setConsequenceLocation( t.getLine(),
                                                        t.getCharPositionInLine() );
            }

            while ( input.LA( 1 ) != DRLLexer.EOF && !helper.validateIdentifierKey( DroolsSoftKeywords.END ) ) {
                input.consume();
            }
            last = input.LT( 1 );
            if ( last.getTokenIndex() > first ) {
                chunk = input.toString( first,
                                        last.getTokenIndex() );
                if ( chunk.endsWith( DroolsSoftKeywords.END ) ) {
                    chunk = chunk.substring( 0,
                                             chunk.length() - DroolsSoftKeywords.END.length() );
                }
                // removing the "then" keyword any any subsequent space and line break
                int index = 4;
                while ( index < chunk.length() && Character.isWhitespace( chunk.charAt( index ) ) ) {
                    index++;
                    if ( chunk.charAt( index - 1 ) == '\r' || chunk.charAt( index - 1 ) == '\n' ) {
                        if ( index < chunk.length() && chunk.charAt( index - 1 ) == '\r' && chunk.charAt( index ) == '\n' ) {
                            index++;
                        }
                        break;
                    }
                }
                chunk = chunk.substring( index );
            }
            rule.rhs( chunk );

        } catch ( RecognitionException re ) {
            reportError( re );
        }
    }

    /* ------------------------------------------------------------------------------------------------
     *                         ANNOTATION
     * ------------------------------------------------------------------------------------------------ */
    /**
     * annotation := AT ID (elementValuePairs | parenChunk )?
     */
    private void annotation( AnnotatedDescrBuilder< ? > adb ) {
        try {
            // '@'
            Token at = match( input,
                              DRLLexer.AT,
                              null,
                              null,
                              DroolsEditorType.SYMBOL );
            if ( state.failed ) return;

            // identifier
            Token id = match( input,
                              DRLLexer.ID,
                              null,
                              null,
                              DroolsEditorType.IDENTIFIER );
            if ( state.failed ) return;

            AnnotationDescrBuilder annotation = null;
            if ( state.backtracking == 0 ) {
                annotation = adb.newAnnotation( id.getText() );
                helper.setStart( annotation,
                                 at );
                helper.builderContext.push( annotation );
            }

            try {
                if ( input.LA( 1 ) == DRLLexer.LEFT_PAREN ) {
                    if ( speculateElementValuePairs() ) {
                        elementValuePairs( annotation );
                        if ( state.failed ) return;
                    } else {
                        String value = chunk( DRLLexer.LEFT_PAREN,
                                              DRLLexer.RIGHT_PAREN );
                        if ( state.failed ) return;
                        if ( state.backtracking == 0 ) {
                            annotation.value( value );
                        }
                    }
                }
            } finally {
                if ( state.backtracking == 0 ) {
                    helper.setEnd();
                    helper.builderContext.pop();
                }
            }

        } catch ( RecognitionException re ) {
            reportError( re );
        }
    }

    /**
     * Invokes elementValuePairs() rule with backtracking
     * to check if the next token sequence matches it or not.
     * 
     * @return true if the sequence of tokens will match the
     *         elementValuePairs() syntax. false otherwise.
     */
    private boolean speculateElementValuePairs() {
        state.backtracking++;
        int start = input.mark();
        try {
            elementValuePairs( null ); // can never throw exception
        } catch ( RecognitionException re ) {
            System.err.println( "impossible: " + re );
            re.printStackTrace();
        }
        boolean success = !state.failed;
        input.rewind( start );
        state.backtracking--;
        state.failed = false;
        return success;

    }

    /**
     * elementValuePairs := LEFT_PAREN elementValuePair (COMMA elementValuePair)* RIGHT_PAREN
     * @param annotation
     */
    private void elementValuePairs( AnnotationDescrBuilder annotation ) throws RecognitionException {
        try {
            match( input,
                   DRLLexer.LEFT_PAREN,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;

            elementValuePair( annotation );
            if ( state.failed ) return;

            while ( input.LA( 1 ) == DRLLexer.COMMA ) {
                match( input,
                       DRLLexer.COMMA,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return;

                elementValuePair( annotation );
                if ( state.failed ) return;
            }

            match( input,
                   DRLLexer.RIGHT_PAREN,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;

        } catch ( RecognitionException re ) {
            reportError( re );
        }
    }

    /**
     * elementValuePair := (ID EQUALS)? elementValue
     * @param annotation
     */
    private void elementValuePair( AnnotationDescrBuilder annotation ) {
        try {
            String key = null;
            if ( input.LA( 1 ) == DRLLexer.ID && input.LA( 2 ) == DRLLexer.EQUALS_ASSIGN ) {
                Token id = match( input,
                                  DRLLexer.ID,
                                  null,
                                  null,
                                  DroolsEditorType.IDENTIFIER );
                if ( state.failed ) return;
                key = id.getText();

                match( input,
                       DRLLexer.EQUALS_ASSIGN,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return;
            }

            String value = safeStripStringDelimiters( elementValue() );
            if ( state.failed ) return;

            if ( state.backtracking == 0 ) {
                annotation.keyValue( key != null ? key : "value",
                                     value );
            }

        } catch ( RecognitionException re ) {
            reportError( re );
        }
    }

    /**
     * elementValue := elementValueArrayInitializer | conditionalExpression 
     * @return
     */
    private String elementValue() {
        String value = "";
        try {
            int first = input.index();

            if ( input.LA( 1 ) == DRLLexer.LEFT_CURLY ) {
                elementValueArrayInitializer();
                if ( state.failed ) return value;
            } else {
                exprParser.conditionalExpression();
                if ( state.failed ) return value;
            }
            value = input.toString( first,
                                    input.LT( -1 ).getTokenIndex() );
        } catch ( Exception re ) {
            reportError( re );
        }
        return value;
    }

    /**
     * elementValueArrayInitializer := LEFT_CURLY (elementValue (COMMA elementValue )*)? RIGHT_CURLY
     * @return
     */
    private String elementValueArrayInitializer() {
        String value = "";
        int first = input.index();
        try {
            match( input,
                   DRLLexer.LEFT_CURLY,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return value;

            if ( input.LA( 1 ) != DRLLexer.RIGHT_CURLY ) {
                elementValue();
                if ( state.failed ) return value;

                while ( input.LA( 1 ) == DRLLexer.COMMA ) {
                    match( input,
                           DRLLexer.COMMA,
                           null,
                           null,
                           DroolsEditorType.SYMBOL );
                    if ( state.failed ) return value;

                    elementValue();
                    if ( state.failed ) return value;
                }
            }
            match( input,
                   DRLLexer.RIGHT_CURLY,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return value;

        } catch ( RecognitionException re ) {
            reportError( re );
        } finally {
            value = input.toString( first,
                                    input.index() );
        }
        return value;
    }

    /* ------------------------------------------------------------------------------------------------
     *                         UTILITY RULES
     * ------------------------------------------------------------------------------------------------ */

    /**
     * Matches a type name
     * 
     * type := ID typeArguments? ( DOT ID typeArguments? )* (LEFT_SQUARE RIGHT_SQUARE)*
     * 
     * @return
     * @throws RecognitionException
     */
    public String type() throws RecognitionException {
        String type = "";
        try {
            int first = input.index(), last = first;
            match( input,
                   DRLLexer.ID,
                   null,
                   new int[]{DRLLexer.DOT, DRLLexer.LESS},
                   DroolsEditorType.IDENTIFIER );
            if ( state.failed ) return type;

            if ( input.LA( 1 ) == DRLLexer.LESS ) {
                typeArguments();
                if ( state.failed ) return type;
            }

            while ( input.LA( 1 ) == DRLLexer.DOT && input.LA( 2 ) == DRLLexer.ID ) {
                match( input,
                       DRLLexer.DOT,
                       null,
                       new int[]{DRLLexer.ID},
                       DroolsEditorType.IDENTIFIER );
                if ( state.failed ) return type;
                match( input,
                       DRLLexer.ID,
                       null,
                       new int[]{DRLLexer.DOT},
                       DroolsEditorType.IDENTIFIER );
                if ( state.failed ) return type;

                if ( input.LA( 1 ) == DRLLexer.LESS ) {
                    typeArguments();
                    if ( state.failed ) return type;
                }
            }

            while ( input.LA( 1 ) == DRLLexer.LEFT_SQUARE && input.LA( 2 ) == DRLLexer.RIGHT_SQUARE ) {
                match( input,
                               DRLLexer.LEFT_SQUARE,
                               null,
                               new int[]{DRLLexer.RIGHT_SQUARE},
                               DroolsEditorType.IDENTIFIER );
                if ( state.failed ) return type;
                match( input,
                               DRLLexer.RIGHT_SQUARE,
                               null,
                               null,
                               DroolsEditorType.IDENTIFIER );
                if ( state.failed ) return type;
            }
            last = input.LT( -1 ).getTokenIndex();
            type = input.toString( first,
                                   last );
        } catch ( RecognitionException re ) {
            reportError( re );
        }
        return type;
    }

    /**
     * Matches type arguments
     * 
     * typeArguments := LESS typeArgument (COMMA typeArgument)* GREATER
     * 
     * @return
     * @throws RecognitionException
     */
    public String typeArguments() throws RecognitionException {
        String typeArguments = "";
        try {
            int first = input.index();
            Token token = match( input,
                                 DRLLexer.LESS,
                                 null,
                                 new int[]{DRLLexer.QUESTION, DRLLexer.ID},
                                 DroolsEditorType.SYMBOL );
            if ( state.failed ) return typeArguments;

            typeArgument();
            if ( state.failed ) return typeArguments;

            while ( input.LA( 1 ) == DRLLexer.COMMA ) {
                token = match( input,
                               DRLLexer.COMMA,
                               null,
                               new int[]{DRLLexer.QUESTION, DRLLexer.ID},
                               DroolsEditorType.IDENTIFIER );
                if ( state.failed ) return typeArguments;

                typeArgument();
                if ( state.failed ) return typeArguments;
            }

            token = match( input,
                           DRLLexer.GREATER,
                           null,
                           null,
                           DroolsEditorType.SYMBOL );
            if ( state.failed ) return typeArguments;
            typeArguments = input.toString( first,
                                            token.getTokenIndex() );

        } catch ( RecognitionException re ) {
            reportError( re );
        }
        return typeArguments;
    }

    /**
     * Matches a type argument
     * 
     * typeArguments := QUESTION (( EXTENDS | SUPER ) type )? 
     *               |  type
     *               ;
     * 
     * @return
     * @throws RecognitionException
     */
    public String typeArgument() throws RecognitionException {
        String typeArgument = "";
        try {
            int first = input.index(), last = first;
            int next = input.LA( 1 );
            switch ( next ) {
                case DRLLexer.QUESTION :
                    match( input,
                           DRLLexer.QUESTION,
                           null,
                           null,
                           DroolsEditorType.SYMBOL );
                    if ( state.failed ) return typeArgument;

                    if ( helper.validateIdentifierKey( DroolsSoftKeywords.EXTENDS ) ) {
                        match( input,
                               DRLLexer.ID,
                               DroolsSoftKeywords.EXTENDS,
                               null,
                               DroolsEditorType.SYMBOL );
                        if ( state.failed ) return typeArgument;

                        type();
                        if ( state.failed ) return typeArgument;
                    } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.SUPER ) ) {
                        match( input,
                               DRLLexer.ID,
                               DroolsSoftKeywords.SUPER,
                               null,
                               DroolsEditorType.SYMBOL );
                        if ( state.failed ) return typeArgument;
                        type();
                        if ( state.failed ) return typeArgument;
                    }
                    break;
                case DRLLexer.ID :
                    type();
                    if ( state.failed ) return typeArgument;
                    break;
                default :
                    // TODO: raise error
            }
            last = input.LT( -1 ).getTokenIndex();
            typeArgument = input.toString( first,
                                           last );
        } catch ( RecognitionException re ) {
            reportError( re );
        }
        return typeArgument;
    }

    /**
     * Matches a qualified identifier
     * 
     * qualifiedIdentifier := ID ( DOT ID )*
     * 
     * @return
     * @throws RecognitionException
     */
    public String qualifiedIdentifier() throws RecognitionException {
        String qi = "";
        try {
            Token first = match( input,
                                 DRLLexer.ID,
                                 null,
                                 new int[]{DRLLexer.DOT},
                                 DroolsEditorType.IDENTIFIER );
            if ( state.failed ) return qi;

            Token last = first;
            while ( input.LA( 1 ) == DRLLexer.DOT && input.LA( 2 ) == DRLLexer.ID ) {
                last = match( input,
                               DRLLexer.DOT,
                               null,
                               new int[]{DRLLexer.ID},
                               DroolsEditorType.IDENTIFIER );
                if ( state.failed ) return qi;
                last = match( input,
                               DRLLexer.ID,
                               null,
                               new int[]{DRLLexer.DOT},
                               DroolsEditorType.IDENTIFIER );
                if ( state.failed ) return qi;
            }
            qi = input.toString( first,
                                 last );
        } catch ( RecognitionException re ) {
            reportError( re );
        }
        return qi;
    }

    /**
     * Matches a conditional expression
     * 
     * @return
     * @throws RecognitionException
     */
    public String conditionalExpression() throws RecognitionException {
        int first = input.index();
        exprParser.conditionalExpression();
        if ( state.failed ) return null;

        if ( state.backtracking == 0 && input.index() > first ) {
            // expression consumed something
            String expr = input.toString( first,
                                          input.LT( -1 ).getTokenIndex() );
            return expr;
        }
        return null;
    }

    /**
     * Matches a chunk started by the leftDelimiter and ended by the rightDelimiter.
     * 
     * @param leftDelimiter
     * @param rightDelimiter
     * @return the matched chunk without the delimiters
     */
    public String chunk( final int leftDelimiter,
                         final int rightDelimiter ) {
        String chunk = "";
        int first = -1, last = first;
        try {
            match( input,
                   leftDelimiter,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return chunk;
            int nests = 0;
            first = input.index();

            while ( input.LA( 1 ) != rightDelimiter || nests > 0 ) {
                if ( input.LA( 1 ) == rightDelimiter ) {
                    nests--;
                } else if ( input.LA( 1 ) == leftDelimiter ) {
                    nests++;
                }
                input.consume();
            }
            last = input.LT( -1 ).getTokenIndex();

            match( input,
                   rightDelimiter,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return chunk;

        } catch ( RecognitionException re ) {
            reportError( re );
        } finally {
            if ( last >= first ) {
                chunk = input.toString( first,
                                        last );
            }
        }
        return chunk;
    }

    /* ------------------------------------------------------------------------------------------------
      *                         GENERAL UTILITY METHODS
      * ------------------------------------------------------------------------------------------------ */
    /** 
     *  Match current input symbol against ttype and optionally
     *  check the text of the token against text.  Attempt
     *  single token insertion or deletion error recovery.  If
     *  that fails, throw MismatchedTokenException.
     */
    private Token match( TokenStream input,
                         int ttype,
                         String text,
                         int[] follow,
                         DroolsEditorType etype ) throws RecognitionException {
        Token matchedSymbol = null;
        matchedSymbol = input.LT( 1 );
        if ( input.LA( 1 ) == ttype && (text == null || text.equals( matchedSymbol.getText() )) ) {
            input.consume();
            state.errorRecovery = false;
            state.failed = false;
            helper.emit( matchedSymbol,
                         etype );
            return matchedSymbol;
        }
        if ( state.backtracking > 0 ) {
            state.failed = true;
            return matchedSymbol;
        }
        matchedSymbol = recoverFromMismatchedToken( input,
                                                    ttype,
                                                    text,
                                                    follow );
        helper.emit( matchedSymbol,
                     etype );
        return matchedSymbol;
    }

    /** Attempt to recover from a single missing or extra token.
    *
    *  EXTRA TOKEN
    *
    *  LA(1) is not what we are looking for.  If LA(2) has the right token,
    *  however, then assume LA(1) is some extra spurious token.  Delete it
    *  and LA(2) as if we were doing a normal match(), which advances the
    *  input.
    *
    *  MISSING TOKEN
    *
    *  If current token is consistent with what could come after
    *  ttype then it is ok to "insert" the missing token, else throw
    *  exception For example, Input "i=(3;" is clearly missing the
    *  ')'.  When the parser returns from the nested call to expr, it
    *  will have call chain:
    *
    *    stat -> expr -> atom
    *
    *  and it will be trying to match the ')' at this point in the
    *  derivation:
    *
    *       => ID '=' '(' INT ')' ('+' atom)* ';'
    *                          ^
    *  match() will see that ';' doesn't match ')' and report a
    *  mismatched token error.  To recover, it sees that LA(1)==';'
    *  is in the set of tokens that can follow the ')' token
    *  reference in rule atom.  It can assume that you forgot the ')'.
    */
    protected Token recoverFromMismatchedToken( TokenStream input,
                                                int ttype,
                                                String text,
                                                int[] follow )
                                                              throws RecognitionException {
        RecognitionException e = null;
        // if next token is what we are looking for then "delete" this token
        if ( mismatchIsUnwantedToken( input,
                                      ttype,
                                      text ) ) {
            e = new UnwantedTokenException( ttype,
                                            input );
            input.consume(); // simply delete extra token
            reportError( e ); // report after consuming so AW sees the token in the exception
            // we want to return the token we're actually matching
            Token matchedSymbol = input.LT( 1 );
            input.consume(); // move past ttype token as if all were ok
            return matchedSymbol;
        }
        // can't recover with single token deletion, try insertion
        if ( mismatchIsMissingToken( input,
                                     follow ) ) {
            e = new MissingTokenException( ttype,
                                           input,
                                           null );
            reportError( e ); // report after inserting so AW sees the token in the exception
            return null;
        }
        // even that didn't work; must throw the exception
        if ( text != null ) {
            e = new DroolsMismatchedTokenException( ttype,
                                                    text,
                                                    input );
        } else {
            e = new MismatchedTokenException( ttype,
                                              input );
        }
        throw e;
    }

    public boolean mismatchIsUnwantedToken( TokenStream input,
                                            int ttype,
                                            String text ) {
        return (input.LA( 2 ) == ttype && (text == null || text.equals( input.LT( 2 ).getText() )));
    }

    public boolean mismatchIsMissingToken( TokenStream input,
                                           int[] follow ) {
        if ( follow == null ) {
            // we have no information about the follow; we can only consume
            // a single token and hope for the best
            return false;
        }
        // TODO: implement this error recovery strategy
        return false;
    }

    private String safeStripDelimiters( String value,
                                        String[] delimiters ) {
        if ( value != null ) {
            value = value.trim();
            if ( value.length() > 1 && value.startsWith( delimiters[0] ) && value.endsWith( delimiters[1] ) ) {
                value = value.substring( 1,
                                         value.length() - 1 );
            }
        }
        return value;
    }

    private String safeStripStringDelimiters( String value ) {
        if ( value != null ) {
            value = value.trim();
            if ( value.length() > 1 &&
                    ((value.startsWith( "\"" ) && value.endsWith( "\"" )) ||
                            (value.startsWith( "'" ) && value.endsWith( "'" ))) ) {
                value = value.substring( 1,
                                         value.length() - 1 );
            }
        }
        return value;
    }

}