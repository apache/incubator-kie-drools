/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.drools.core.util.StringUtils;
import org.drools.lang.api.AccumulateDescrBuilder;
import org.drools.lang.api.AnnotatedDescrBuilder;
import org.drools.lang.api.AnnotationDescrBuilder;
import org.drools.lang.api.AttributeDescrBuilder;
import org.drools.lang.api.AttributeSupportBuilder;
import org.drools.lang.api.BehaviorDescrBuilder;
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
import org.drools.lang.descr.AnnotationDescr;
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
            // package declaration?
            if ( input.LA( 1 ) != DRLLexer.EOF && helper.validateIdentifierKey( DroolsSoftKeywords.PACKAGE ) ) {
                String pkgName = packageStatement( pkg );
                pkg.name( pkgName );
                if ( state.failed ) return pkg.getDescr();
            }

            // statements
            while ( input.LA( 1 ) != DRLLexer.EOF ) {
                int next = input.index();
                if ( helper.validateStatement( 1 ) ) {
                    statement( pkg );
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
            helper.setEnd( pkg );
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
    public String packageStatement( PackageDescrBuilder pkg ) throws RecognitionException {
        String pkgName = null;

        try {
            helper.start( pkg,
                          PackageDescrBuilder.class,
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
                        pkg );
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
    public BaseDescr statement( PackageDescrBuilder pkg ) throws RecognitionException {
        BaseDescr descr = null;
        try {
            if ( helper.validateIdentifierKey( DroolsSoftKeywords.IMPORT ) ) {
                descr = importStatement( pkg );
                if ( state.failed ) return descr;
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.GLOBAL ) ) {
                descr = globalStatement( pkg );
                if ( state.failed ) return descr;
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.DECLARE ) ) {
                descr = declare( pkg );
                if ( state.failed ) return descr;
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.RULE ) ) {
                descr = rule( pkg );
                if ( state.failed ) return descr;
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.QUERY ) ) {
                descr = query( pkg );
                if ( state.failed ) return descr;
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.FUNCTION ) ) {
                descr = function( pkg );
                if ( state.failed ) return descr;
            } else if ( helper.validateAttribute( 1 ) ) {
                descr = attribute( pkg );
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
    public ImportDescr importStatement( PackageDescrBuilder pkg ) throws RecognitionException {
        ImportDescrBuilder imp = null;
        try {
            imp = helper.start( pkg,
                                ImportDescrBuilder.class,
                                null );

            // import
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.IMPORT,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            String kwd;
            if ( helper.validateIdentifierKey( kwd = DroolsSoftKeywords.FUNCTION ) ||
                 helper.validateIdentifierKey( kwd = DroolsSoftKeywords.STATIC ) ) {
                // function
                match( input,
                       DRLLexer.ID,
                       kwd,
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
                        imp );
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
    public GlobalDescr globalStatement( PackageDescrBuilder pkg ) throws RecognitionException {
        GlobalDescrBuilder global = null;
        try {
            global = helper.start( pkg,
                                   GlobalDescrBuilder.class,
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
                        global );
        }
        return (global != null) ? global.getDescr() : null;
    }

    /* ------------------------------------------------------------------------------------------------
     *                         DECLARE STATEMENT
     * ------------------------------------------------------------------------------------------------ */

    /**
     * declare := DECLARE type (EXTENDS type)? annotation* field* END SEMICOLON?
     * 
     * @return
     * @throws RecognitionException
     */
    public TypeDeclarationDescr declare( PackageDescrBuilder pkg ) throws RecognitionException {
        DeclareDescrBuilder declare = null;
        try {
            declare = helper.start( pkg,
                                    DeclareDescrBuilder.class,
                                    null );

            // 'declare'
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.DECLARE,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            // type may be qualified when adding metadata
            String type = qualifiedIdentifier();
            if ( state.failed ) return null;
            if ( state.backtracking == 0 ) declare.type( type );

            if ( helper.validateIdentifierKey( DroolsSoftKeywords.EXTENDS ) ) {
                match( input,
                       DRLLexer.ID,
                       DroolsSoftKeywords.EXTENDS,
                       null,
                       DroolsEditorType.KEYWORD );
                if ( !state.failed ) {
                    // Going for type includes generics, which is a no-no (JIRA-3040)
                    String superType = qualifiedIdentifier();
                    declare.superType( superType );
                }
            }

            while ( input.LA( 1 ) == DRLLexer.AT ) {
                // metadata*
                annotation( declare );
                if ( state.failed ) return null;
            }

            //boolean qualified = type.indexOf( '.' ) >= 0;
            while ( //! qualified &&
            input.LA( 1 ) == DRLLexer.ID && !helper.validateIdentifierKey( DroolsSoftKeywords.END ) ) {
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
                        declare );
        }
        return (declare != null) ? declare.getDescr() : null;
    }

    /**
     * field := label type (EQUALS_ASSIGN expression)? annotation* SEMICOLON?
     */
    private void field( DeclareDescrBuilder declare ) {
        FieldDescrBuilder field = null;
        String fname = null;
        try {
            fname = label( DroolsEditorType.IDENTIFIER );
            if ( state.failed ) return;
        } catch ( RecognitionException re ) {
            reportError( re );
        }

        try {
            field = helper.start( declare,
                                  FieldDescrBuilder.class,
                                  fname );

            // type
            String type = qualifiedIdentifier();
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
            field.processAnnotations();

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
                        field );
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
    public FunctionDescr function( PackageDescrBuilder pkg ) throws RecognitionException {
        FunctionDescrBuilder function = null;
        try {
            function = helper.start( pkg,
                                     FunctionDescrBuilder.class,
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
                                 DRLLexer.RIGHT_CURLY,
                                 -1 );
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
            helper.end( FunctionDescrBuilder.class,
                        function );
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
    public RuleDescr query( PackageDescrBuilder pkg ) throws RecognitionException {
        QueryDescrBuilder query = null;
        try {
            query = helper.start( pkg, 
                                  QueryDescrBuilder.class,
                                  null );

            // 'query'
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.QUERY,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            if ( helper.validateIdentifierKey( DroolsSoftKeywords.WHEN ) ||
                    helper.validateIdentifierKey( DroolsSoftKeywords.THEN ) ||
                    helper.validateIdentifierKey( DroolsSoftKeywords.END ) ) {
                failMissingTokenException();
                return null; // in case it is backtracking
            }

            String name = stringId();
            if ( state.backtracking == 0 ) query.name( name );
            if ( state.failed ) return null;

            if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
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

            if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
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
            helper.end( QueryDescrBuilder.class,
                        query );
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
    public RuleDescr rule( PackageDescrBuilder pkg ) throws RecognitionException {
        RuleDescrBuilder rule = null;
        try {
            rule = helper.start( pkg,
                                 RuleDescrBuilder.class,
                                 null );

            // 'rule'
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.RULE,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            if ( helper.validateIdentifierKey( DroolsSoftKeywords.WHEN ) ||
                 helper.validateIdentifierKey( DroolsSoftKeywords.THEN ) ||
                 helper.validateIdentifierKey( DroolsSoftKeywords.END ) ) {
                failMissingTokenException();
                return null; // in case it is backtracking
            }

            String name = stringId();
            if ( state.failed ) return null;
            if ( state.backtracking == 0 ) {
                rule.name( name );
                helper.setParaphrasesValue( DroolsParaphraseTypes.RULE,
                                            "\"" + name + "\"" );
                if ( input.LA( 1 ) != DRLLexer.EOF ) {
                    helper.emit( Location.LOCATION_RULE_HEADER );
                }
            }

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

            if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
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
                        rule );
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
            return StringUtils.unescapeJava( safeStripStringDelimiters( id.getText() ) );
        } else {
            throw new MismatchedTokenException( DRLLexer.ID,
                                                input );
        }
    }

    /**
     * attributes := (ATTRIBUTES COLON)? attribute ( COMMA? attribute )*
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
            attribute( rule );
            if ( state.failed ) return;

            while ( input.LA( 1 ) == DRLLexer.COMMA || helper.validateAttribute( 1 ) ) {
                if ( input.LA( 1 ) == DRLLexer.COMMA ) {
                    match( input,
                           DRLLexer.COMMA,
                           null,
                           null,
                           DroolsEditorType.SYMBOL );
                    if ( state.failed ) return;
                }
                attribute( rule );
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
    public AttributeDescr attribute( AttributeSupportBuilder<?> as ) {
        AttributeDescr attribute = null;
        try {
            if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                helper.emit( Location.LOCATION_RULE_HEADER_KEYWORD );
            }
            if ( helper.validateIdentifierKey( DroolsSoftKeywords.SALIENCE ) ) {
                attribute = salience( as );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.ENABLED ) ) {
                attribute = enabled( as );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.NO ) &&
                        helper.validateLT( 2,
                                           "-" ) &&
                        helper.validateLT( 3,
                                           DroolsSoftKeywords.LOOP ) ) {
                attribute = booleanAttribute( as, new String[]{DroolsSoftKeywords.NO, "-", DroolsSoftKeywords.LOOP} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.AUTO ) &&
                        helper.validateLT( 2,
                                           "-" ) &&
                        helper.validateLT( 3,
                                           DroolsSoftKeywords.FOCUS ) ) {
                attribute = booleanAttribute( as, new String[]{DroolsSoftKeywords.AUTO, "-", DroolsSoftKeywords.FOCUS} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.LOCK ) &&
                        helper.validateLT( 2,
                                           "-" ) &&
                        helper.validateLT( 3,
                                           DroolsSoftKeywords.ON ) &&
                        helper.validateLT( 4,
                                           "-" ) &&
                        helper.validateLT( 5,
                                           DroolsSoftKeywords.ACTIVE ) ) {
                attribute = booleanAttribute( as, new String[]{DroolsSoftKeywords.LOCK, "-", DroolsSoftKeywords.ON, "-", DroolsSoftKeywords.ACTIVE} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.AGENDA ) &&
                        helper.validateLT( 2,
                                           "-" ) &&
                        helper.validateLT( 3,
                                           DroolsSoftKeywords.GROUP ) ) {
                attribute = stringAttribute( as, new String[]{DroolsSoftKeywords.AGENDA, "-", DroolsSoftKeywords.GROUP} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.ACTIVATION ) &&
                        helper.validateLT( 2,
                                           "-" ) &&
                        helper.validateLT( 3,
                                           DroolsSoftKeywords.GROUP ) ) {
                attribute = stringAttribute( as, new String[]{DroolsSoftKeywords.ACTIVATION, "-", DroolsSoftKeywords.GROUP} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.RULEFLOW ) &&
                        helper.validateLT( 2,
                                           "-" ) &&
                        helper.validateLT( 3,
                                           DroolsSoftKeywords.GROUP ) ) {
                attribute = stringAttribute( as, new String[]{DroolsSoftKeywords.RULEFLOW, "-", DroolsSoftKeywords.GROUP} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.DATE ) &&
                        helper.validateLT( 2,
                                           "-" ) &&
                        helper.validateLT( 3,
                                           DroolsSoftKeywords.EFFECTIVE ) ) {
                attribute = stringAttribute( as, new String[]{DroolsSoftKeywords.DATE, "-", DroolsSoftKeywords.EFFECTIVE} );
                attribute.setType( AttributeDescr.Type.DATE );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.DATE ) &&
                        helper.validateLT( 2,
                                           "-" ) &&
                        helper.validateLT( 3,
                                           DroolsSoftKeywords.EXPIRES ) ) {
                attribute = stringAttribute( as, new String[]{DroolsSoftKeywords.DATE, "-", DroolsSoftKeywords.EXPIRES} );
                attribute.setType( AttributeDescr.Type.DATE );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.DIALECT ) ) {
                attribute = stringAttribute( as, new String[]{DroolsSoftKeywords.DIALECT} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.CALENDARS ) ) {
                attribute = stringListAttribute( as, new String[]{DroolsSoftKeywords.CALENDARS} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.TIMER ) ) {
                attribute = intOrChunkAttribute( as, new String[]{DroolsSoftKeywords.TIMER} );
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.DURATION ) ) {
                attribute = intOrChunkAttribute( as, new String[]{DroolsSoftKeywords.DURATION} );
            }
        } catch ( RecognitionException re ) {
            reportError( re );
        }
        if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
            helper.emit( Location.LOCATION_RULE_HEADER );
        }
        return attribute;
    }

    /**
     * salience := SALIENCE conditionalExpression
     * @throws RecognitionException
     */
    private AttributeDescr salience( AttributeSupportBuilder<?> as ) throws RecognitionException {
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
                attribute = helper.start( as,
                                          AttributeDescrBuilder.class,
                                          DroolsSoftKeywords.SALIENCE );
            }

            boolean hasParen = input.LA( 1 ) == DRLLexer.LEFT_PAREN;
            int first = input.index();
            if ( hasParen ) {
                match( input,
                       DRLLexer.LEFT_PAREN,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;
            }

            String value = conditionalExpression();
            if ( state.failed ) return null;

            if ( hasParen ) {
                match( input,
                       DRLLexer.RIGHT_PAREN,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;
            }
            if ( state.backtracking == 0 ) {
                if ( hasParen ) {
                    value = input.toString( first,
                                            input.LT( -1 ).getTokenIndex() );
                }
                attribute.value( value );
                attribute.type( AttributeDescr.Type.EXPRESSION );
            }

        } finally {
            if ( attribute != null ) {
                helper.end( AttributeDescrBuilder.class,
                            attribute );
            }
        }
        return attribute != null ? attribute.getDescr() : null;
    }

    /**
     * enabled := ENABLED conditionalExpression
     * @throws RecognitionException
     */
    private AttributeDescr enabled( AttributeSupportBuilder<?> as ) throws RecognitionException {
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
                attribute = helper.start( as,
                                          AttributeDescrBuilder.class,
                                          DroolsSoftKeywords.ENABLED );
            }

            boolean hasParen = input.LA( 1 ) == DRLLexer.LEFT_PAREN;
            int first = input.index();
            if ( hasParen ) {
                match( input,
                       DRLLexer.LEFT_PAREN,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;
            }

            String value = conditionalExpression();
            if ( state.failed ) return null;

            if ( hasParen ) {
                match( input,
                       DRLLexer.RIGHT_PAREN,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;
            }
            if ( state.backtracking == 0 ) {
                if ( hasParen ) {
                    value = input.toString( first,
                                            input.LT( -1 ).getTokenIndex() );
                }
                attribute.value( value );
                attribute.type( AttributeDescr.Type.EXPRESSION );
            }

        } finally {
            if ( attribute != null ) {
                helper.end( AttributeDescrBuilder.class,
                            attribute );
            }
        }
        return attribute != null ? attribute.getDescr() : null;
    }

    /**
     * booleanAttribute := attributeKey (BOOLEAN)?
     * @param key
     * @throws RecognitionException
     */
    private AttributeDescr booleanAttribute( AttributeSupportBuilder<?> as, String[] key ) throws RecognitionException {
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
                attribute = helper.start( as,
                                          AttributeDescrBuilder.class,
                                          builder.toString() );
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
                attribute.type( AttributeDescr.Type.BOOLEAN );
            }
        } finally {
            if ( attribute != null ) {
                helper.end( AttributeDescrBuilder.class,
                            attribute );
            }
        }
        return attribute != null ? attribute.getDescr() : null;
    }

    /**
     * stringAttribute := attributeKey STRING
     * @param key
     * @throws RecognitionException
     */
    private AttributeDescr stringAttribute( AttributeSupportBuilder<?> as, String[] key ) throws RecognitionException {
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
                attribute = helper.start( as,
                                          AttributeDescrBuilder.class,
                                          builder.toString() );
            }

            Token value = match( input,
                                 DRLLexer.STRING,
                                 null,
                                 null,
                                 DroolsEditorType.STRING_CONST );
            if ( state.failed ) return null;
            if ( state.backtracking == 0 ) {
                attribute.value( StringUtils.unescapeJava( safeStripStringDelimiters( value.getText() ) ) );
                attribute.type( AttributeDescr.Type.STRING );
            }
        } finally {
            if ( attribute != null ) {
                helper.end( AttributeDescrBuilder.class,
                            attribute );
            }
        }
        return attribute != null ? attribute.getDescr() : null;
    }

    /**
     * stringListAttribute := attributeKey STRING (COMMA STRING)*
     * @param key
     * @throws RecognitionException
     */
    private AttributeDescr stringListAttribute( AttributeSupportBuilder<?> as, String[] key ) throws RecognitionException {
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
                attribute = helper.start( as,
                                          AttributeDescrBuilder.class,
                                          builder.toString() );
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

            while ( input.LA( 1 ) == DRLLexer.COMMA ) {
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
                attribute.type( AttributeDescr.Type.LIST );
            }
        } finally {
            if ( attribute != null ) {
                helper.end( AttributeDescrBuilder.class,
                            attribute );
            }
        }
        return attribute != null ? attribute.getDescr() : null;
    }

    /**
     * intOrChunkAttribute := attributeKey (DECIMAL | parenChunk)
     * @param key
     * @throws RecognitionException
     */
    private AttributeDescr intOrChunkAttribute( AttributeSupportBuilder<?> as, String[] key ) throws RecognitionException {
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
                attribute = helper.start( as,
                                          AttributeDescrBuilder.class,
                                          builder.toString() );
            }

            if ( input.LA( 1 ) == DRLLexer.LEFT_PAREN ) {
                String value = chunk( DRLLexer.LEFT_PAREN,
                                      DRLLexer.RIGHT_PAREN,
                                      -1 );
                if ( state.failed ) return null;
                if ( state.backtracking == 0 ) {
                    attribute.value( safeStripDelimiters( value,
                                                          "(",
                                                          ")" ) );
                    attribute.type( AttributeDescr.Type.EXPRESSION );
                }
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
                if ( state.backtracking == 0 ) {
                    attribute.value( value );
                    attribute.type( AttributeDescr.Type.NUMBER );
                }
            }
        } finally {
            if ( attribute != null ) {
                helper.end( AttributeDescrBuilder.class,
                            attribute );
            }
        }
        return attribute != null ? attribute.getDescr() : null;
    }

    /**
     * lhs := WHEN COLON? lhsStatement
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
        helper.start( lhs,
                      CEDescrBuilder.class,
                      null );
        if ( state.backtracking == 0 ) {
            helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
        }
        try {
            while ( input.LA( 1 ) != DRLLexer.EOF &&
                    !helper.validateIdentifierKey( DroolsSoftKeywords.THEN ) &&
                    !helper.validateIdentifierKey( DroolsSoftKeywords.END ) ) {
                if ( state.backtracking == 0 ) {
                    helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
                }
                lhsOr( lhs,
                       true );
                if ( lhs.getDescr() != null && lhs.getDescr() instanceof ConditionalElementDescr ) {
                    ConditionalElementDescr root = (ConditionalElementDescr) lhs.getDescr();
                    BaseDescr[] descrs = root.getDescrs().toArray( new BaseDescr[root.getDescrs().size()] );
                    root.getDescrs().clear();
                    for ( int i = 0; i < descrs.length; i++ ) {
                        root.addOrMerge( descrs[i] );
                    }
                }
                if ( state.failed ) return;
            }
        } finally {
            helper.end( CEDescrBuilder.class,
                        lhs );
        }
    }

    /**
     * lhsOr := LEFT_PAREN OR lhsAnd+ RIGHT_PAREN
     *        | lhsAnd (OR lhsAnd)*
     *        
     * @param ce
     * @param allowOr
     * @throws RecognitionException 
     */
    private BaseDescr lhsOr( final CEDescrBuilder< ? , ? > ce,
                             boolean allowOr ) throws RecognitionException {
        BaseDescr result = null;
        if ( allowOr && input.LA( 1 ) == DRLLexer.LEFT_PAREN && helper.validateLT( 2,
                                                                                   DroolsSoftKeywords.OR ) ) {
            // prefixed OR
            CEDescrBuilder< ? , OrDescr> or = null;
            if ( state.backtracking == 0 ) {
                or = ce.or();
                result = or.getDescr();
                helper.start( or,
                              CEDescrBuilder.class,
                              null );
            }
            try {
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

                if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                    helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                }
                while ( input.LA( 1 ) != DRLLexer.RIGHT_PAREN ) {
                    lhsAnd( or,
                            allowOr );
                    if ( state.failed ) return null;
                }

                match( input,
                        DRLLexer.RIGHT_PAREN,
                        null,
                        null,
                        DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;
            } finally {
                if ( state.backtracking == 0 ) {
                    helper.end( CEDescrBuilder.class,
                                or );
                }
            }
        } else {
            // infix OR

            // create an OR anyway, as if it is not an OR we remove it later
            CEDescrBuilder< ? , OrDescr> or = null;
            if ( state.backtracking == 0 ) {
                or = ce.or();
                result = or.getDescr();
                helper.start( or,
                              CEDescrBuilder.class,
                              null );
            }
            try {
                lhsAnd( or,
                        allowOr );
                if ( state.failed ) return null;

                if ( allowOr &&
                        (helper.validateIdentifierKey( DroolsSoftKeywords.OR )
                                ||
                                input.LA( 1 ) == DRLLexer.DOUBLE_PIPE) ) {
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
                        if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                            helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                        }

                        lhsAnd( or,
                                allowOr );
                        if ( state.failed ) return null;
                    }
                } else if ( allowOr ) {
                    if ( state.backtracking == 0 ) {
                        // if no OR present, then remove it and add children to parent
                        ((ConditionalElementDescr) ce.getDescr()).getDescrs().remove( or.getDescr() );
                        for ( BaseDescr base : or.getDescr().getDescrs() ) {
                            ((ConditionalElementDescr) ce.getDescr()).addDescr( base );
                        }
                        result = ce.getDescr();
                    }
                }
            } finally {
                if ( state.backtracking == 0 ) {
                    helper.end( CEDescrBuilder.class,
                                or );
                }
            }
        }
        return result;
    }

    /**
     * lhsAnd := LEFT_PAREN AND lhsUnary+ RIGHT_PAREN
     *         | lhsUnary (AND lhsUnary)*
     *        
     * @param ce
     * @throws RecognitionException 
     */
    private BaseDescr lhsAnd( final CEDescrBuilder< ? , ? > ce,
                              boolean allowOr ) throws RecognitionException {
        BaseDescr result = null;
        if ( input.LA( 1 ) == DRLLexer.LEFT_PAREN && helper.validateLT( 2,
                                                                        DroolsSoftKeywords.AND ) ) {
            // prefixed AND
            CEDescrBuilder< ? , AndDescr> and = null;
            if ( state.backtracking == 0 ) {
                and = ce.and();
                result = ce.getDescr();
                helper.start( and,
                              CEDescrBuilder.class,
                              null );
            }
            try {
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

                if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                    helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                }
                while ( input.LA( 1 ) != DRLLexer.RIGHT_PAREN ) {
                    lhsUnary( and,
                              allowOr );
                    if ( state.failed ) return null;
                }

                match( input,
                        DRLLexer.RIGHT_PAREN,
                        null,
                        null,
                        DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;
            } finally {
                if ( state.backtracking == 0 ) {
                    helper.end( CEDescrBuilder.class,
                                and );
                }
            }
        } else {
            // infix AND

            // create an AND anyway, since if it is not an AND we remove it later
            CEDescrBuilder< ? , AndDescr> and = null;
            if ( state.backtracking == 0 ) {
                and = ce.and();
                result = and.getDescr();
                helper.start( and,
                              CEDescrBuilder.class,
                              null );
            }
            try {
                lhsUnary( and,
                          allowOr );
                if ( state.failed ) return null;

                if ( helper.validateIdentifierKey( DroolsSoftKeywords.AND ) ||
                        input.LA( 1 ) == DRLLexer.DOUBLE_AMPER ) {
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

                        if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                            helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                        }
                        lhsUnary( and,
                                  allowOr );
                        if ( state.failed ) return null;
                    }
                } else {
                    if ( state.backtracking == 0 ) {
                        // if no AND present, then remove it and add children to parent
                        ((ConditionalElementDescr) ce.getDescr()).getDescrs().remove( and.getDescr() );
                        for ( BaseDescr base : and.getDescr().getDescrs() ) {
                            ((ConditionalElementDescr) ce.getDescr()).addDescr( base );
                        }
                        result = ce.getDescr();
                    }
                }
            } finally {
                if ( state.backtracking == 0 ) {
                    helper.end( CEDescrBuilder.class,
                                and );
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
    private BaseDescr lhsUnary( final CEDescrBuilder< ? , ? > ce,
                                boolean allowOr ) throws RecognitionException {
        BaseDescr result = null;
        if ( helper.validateIdentifierKey( DroolsSoftKeywords.EXISTS ) ) {
            result = lhsExists( ce,
                                allowOr );
        } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.NOT ) ) {
            result = lhsNot( ce,
                             allowOr );
        } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.EVAL ) ) {
            result = lhsEval( ce );
        } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.FORALL ) ) {
            result = lhsForall( ce );
        } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.ACCUMULATE ) ) {
            result = lhsAcc( ce );
        } else if ( input.LA( 1 ) == DRLLexer.LEFT_PAREN ) {
            // the order here is very important: this if branch must come before the lhsPatternBind below
            result = lhsParen( ce,
                               allowOr );
        } else if ( input.LA( 1 ) == DRLLexer.ID || input.LA( 1 ) == DRLLexer.QUESTION ) {
            result = lhsPatternBind( ce,
                                     allowOr );
        } else {
            failMismatchedTokenException();
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
    private BaseDescr lhsExists( CEDescrBuilder< ? , ? > ce,
                                 boolean allowOr ) throws RecognitionException {
        CEDescrBuilder< ? , ExistsDescr> exists = null;

        if ( state.backtracking == 0 ) {
            exists = ce.exists();
            helper.start( exists,
                          CEDescrBuilder.class,
                          null );
        }
        try {
            match( input,
                    DRLLexer.ID,
                    DroolsSoftKeywords.EXISTS,
                    null,
                    DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS );
            }
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

                lhsOr( exists,
                        allowOr );
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

        } finally {
            if ( state.backtracking == 0 ) {
                helper.end( CEDescrBuilder.class,
                            exists );
            }
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
    private BaseDescr lhsNot( CEDescrBuilder< ? , ? > ce,
                              boolean allowOr ) throws RecognitionException {
        CEDescrBuilder< ? , NotDescr> not = null;

        if ( state.backtracking == 0 ) {
            not = ce.not();
            helper.start( not,
                          CEDescrBuilder.class,
                          null );
        }

        try {
            match( input,
                    DRLLexer.ID,
                    DroolsSoftKeywords.NOT,
                    null,
                    DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

            if ( state.backtracking == 0 ) {
                helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT );
            }
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
                if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                    helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
                }

                lhsOr( not,
                        allowOr );
                if ( state.failed ) return null;

                if ( !prefixed ) {
                    match( input,
                            DRLLexer.RIGHT_PAREN,
                            null,
                            null,
                            DroolsEditorType.SYMBOL );
                    if ( state.failed ) return null;
                }
            } else if ( input.LA( 1 ) != DRLLexer.EOF ) {
                lhsPatternBind( not,
                                true );
                if ( state.failed ) return null;
            }

        } finally {
            if ( state.backtracking == 0 ) {
                helper.end( CEDescrBuilder.class,
                            not );
            }
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
        ForallDescrBuilder< ? > forall = helper.start( ce,
                                                       ForallDescrBuilder.class,
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
                                false );
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
                        forall );
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
            eval = helper.start( ce,
                                 EvalDescrBuilder.class,
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

            if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                helper.emit( Location.LOCATION_LHS_INSIDE_EVAL );
            }
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
                        eval );
            if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
            }
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
    private BaseDescr lhsParen( CEDescrBuilder< ? , ? > ce,
                                boolean allowOr ) throws RecognitionException {
        match( input,
               DRLLexer.LEFT_PAREN,
               null,
               null,
               DroolsEditorType.SYMBOL );
        if ( state.failed ) return null;

        if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
            helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
        }
        BaseDescr descr = lhsOr( ce,
                                 allowOr );
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
        pattern = helper.start( ce,
                                PatternDescrBuilder.class,
                                null );
        if ( pattern != null ) {
            result = pattern.getDescr();
        }

        String label = null;
        boolean isUnification = false;
        if ( input.LA( 1 ) == DRLLexer.ID && input.LA( 2 ) == DRLLexer.COLON && !helper.validateCEKeyword( 1 ) ) {
            label = label( DroolsEditorType.IDENTIFIER_PATTERN );
            if ( state.failed ) return null;
        } else if ( input.LA( 1 ) == DRLLexer.ID && input.LA( 2 ) == DRLLexer.UNIFY && !helper.validateCEKeyword( 1 ) ) {
            label = unif( DroolsEditorType.IDENTIFIER_PATTERN );
            if ( state.failed ) return null;
            isUnification = true;
        }

        if ( input.LA( 1 ) == DRLLexer.LEFT_PAREN ) {
            try {
                match( input,
                       DRLLexer.LEFT_PAREN,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;

                if ( helper.validateCEKeyword( 1 ) ) {
                    failMismatchedTokenException();
                    return null; // in case it is backtracking
                }

                lhsPattern( pattern,
                            label,
                            isUnification );
                if ( state.failed ) return null;

                if ( allowOr && helper.validateIdentifierKey( DroolsSoftKeywords.OR ) && ce instanceof CEDescrBuilder ) {
                    if ( state.backtracking == 0 ) {
                        // this is necessary because of the crappy bind with multi-pattern OR syntax 
                        or = ((CEDescrBuilder<DescrBuilder< ? >, OrDescr>) ce).or();
                        result = or.getDescr();

                        helper.end( PatternDescrBuilder.class,
                                    pattern );
                        helper.start( or,
                                      CEDescrBuilder.class,
                                      null );
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

                        pattern = helper.start( or,
                                                PatternDescrBuilder.class,
                                                null );
                        // new pattern, same binding
                        lhsPattern( pattern,
                                    label,
                                    isUnification );
                        if ( state.failed ) return null;

                        helper.end( PatternDescrBuilder.class,
                                    pattern );
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
                                pattern );
                }
            }

        } else {
            try {
                lhsPattern( pattern,
                            label,
                            isUnification );
                if ( state.failed ) return null;

            } finally {
                helper.end( PatternDescrBuilder.class,
                            pattern );
            }
        }

        return result;
    }

    /**
     * lhsAccumulate := ACCUMULATE LEFT_PAREN lhsAnd COMMA
     *                      accumulateFunction (COMMA accumulateFunction)*
     *                  RIGHT_PAREN SEMICOLON?
     *  
     * @param ce
     * @return
     * @throws RecognitionException 
     */
    private BaseDescr lhsAcc( PatternContainerDescrBuilder< ? , ? > ce ) throws RecognitionException {
        PatternDescrBuilder< ? > pattern = null;
        BaseDescr result = null;

        pattern = helper.start( ce,
                                PatternDescrBuilder.class,
                                null );
        if ( pattern != null ) {
            result = pattern.getDescr();
        }

        try {
            if ( state.backtracking == 0 ) {
                pattern.type( "Object[]" );
                pattern.isQuery( false );
                // might have to add the implicit bindings as well
            }

            AccumulateDescrBuilder< ? > accumulate = helper.start( pattern,
                                                                   AccumulateDescrBuilder.class,
                                                                   null );
            try {
                match( input,
                       DRLLexer.ID,
                       DroolsSoftKeywords.ACCUMULATE,
                       null,
                       DroolsEditorType.KEYWORD );
                if ( state.failed ) return null;

                if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                    helper.emit( Location.LOCATION_LHS_FROM_ACCUMULATE );
                }
                match( input,
                       DRLLexer.LEFT_PAREN,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;

                CEDescrBuilder< ? , AndDescr> source = accumulate.source();
                try {
                    helper.start( source,
                                  CEDescrBuilder.class,
                                  null );
                    lhsAnd( source,
                            false );
                    if ( state.failed ) return null;

                    if ( source.getDescr() != null && source.getDescr() instanceof ConditionalElementDescr ) {
                        ConditionalElementDescr root = (ConditionalElementDescr) source.getDescr();
                        BaseDescr[] descrs = root.getDescrs().toArray( new BaseDescr[root.getDescrs().size()] );
                        root.getDescrs().clear();
                        for ( int i = 0; i < descrs.length; i++ ) {
                            root.addOrMerge( descrs[i] );
                        }
                    }
                } finally {
                    helper.end( CEDescrBuilder.class,
                                source );
                }

                match( input,
                       DRLLexer.COMMA,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;

                // accumulate functions
                accumulateFunction( accumulate );
                if ( state.failed ) return null;

                while ( input.LA( 1 ) == DRLLexer.COMMA ) {
                    match( input,
                           DRLLexer.COMMA,
                           null,
                           null,
                           DroolsEditorType.SYMBOL );
                    if ( state.failed ) return null;

                    accumulateFunction( accumulate );
                    if ( state.failed ) return null;
                }

                match( input,
                       DRLLexer.RIGHT_PAREN,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return null;
            } finally {
                helper.end( AccumulateDescrBuilder.class,
                            accumulate );
                if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                    helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
                }
            }
        } finally {
            helper.end( PatternDescrBuilder.class,
                        pattern );
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

    private void failMismatchedTokenException() throws DroolsMismatchedTokenException {
        if ( state.backtracking > 0 ) {
            state.failed = true;
        } else {
            DroolsMismatchedTokenException mte = new DroolsMismatchedTokenException( input.LA( 1 ),
                                                                                     input.LT( 1 ).getText(),
                                                                                     input );
            input.consume();
            throw mte;
        }
    }

    private void failMissingTokenException() throws MissingTokenException {
        if ( state.backtracking > 0 ) {
            state.failed = true;
        } else {
            throw new MissingTokenException( DRLLexer.STRING,
                                             input,
                                             null );
        }
    }

    /**
     * lhsPattern := QUESTION? type LEFT_PAREN constraints? RIGHT_PAREN over? source?
     * 
     * @param pattern
     * @param label
     * @param isUnification
     * @throws RecognitionException
     */
    private void lhsPattern( PatternDescrBuilder< ? > pattern,
                             String label,
                             boolean isUnification ) throws RecognitionException {
        boolean query = false;
        if ( input.LA( 1 ) == DRLLexer.QUESTION ) {
            match( input,
                   DRLLexer.QUESTION,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;
            query = true;
        }

        String type = this.qualifiedIdentifier();
        if ( state.failed ) return;

        if ( state.backtracking == 0 ) {
            pattern.type( type );
            pattern.isQuery( query );
            if ( label != null ) {
                pattern.id( label,
                            isUnification );
            }
        }

        match( input,
               DRLLexer.LEFT_PAREN,
               null,
               null,
               DroolsEditorType.SYMBOL );
        if ( state.failed ) return;

        if ( input.LA( 1 ) != DRLLexer.RIGHT_PAREN && speculatePositionalConstraints() ) {
            positionalConstraints( pattern );
        }

        if ( input.LA( 1 ) != DRLLexer.RIGHT_PAREN ) {
            constraints( pattern );
        }

        match( input,
               DRLLexer.RIGHT_PAREN,
               null,
               null,
               DroolsEditorType.SYMBOL );
        if ( state.failed ) return;

        if ( helper.validateIdentifierKey( DroolsSoftKeywords.OVER ) || input.LA( 1 ) == DRLLexer.PIPE ) {
            patternBehavior( pattern );
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
    private String label( DroolsEditorType edType ) throws RecognitionException {
        Token label = match( input,
                             DRLLexer.ID,
                             null,
                             null,
                             edType );
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
     * unif := ID UNIFY
     * @return
     * @throws RecognitionException 
     */
    private String unif( DroolsEditorType edType ) throws RecognitionException {
        Token label = match( input,
                             DRLLexer.ID,
                             null,
                             null,
                             edType );
        if ( state.failed ) return null;

        match( input,
               DRLLexer.UNIFY,
               null,
               null,
               DroolsEditorType.SYMBOL );
        if ( state.failed ) return null;

        return label.getText();
    }

    private boolean speculatePositionalConstraints() {
        state.backtracking++;
        int start = input.mark();
        try {
            positionalConstraints( null ); // can never throw exception
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
     * positionalConstraints := constraint (COMMA constraint)* SEMICOLON
     * @param pattern
     * @throws RecognitionException 
     */
    private void positionalConstraints( PatternDescrBuilder< ? > pattern ) throws RecognitionException {
        constraint( pattern,
                    true );
        if ( state.failed ) return;

        while ( input.LA( 1 ) == DRLLexer.COMMA ) {
            match( input,
                   DRLLexer.COMMA,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;

            constraint( pattern,
                        true );
            if ( state.failed ) return;
        }

        match( input,
               DRLLexer.SEMICOLON,
               null,
               null,
               DroolsEditorType.SYMBOL );
        if ( state.failed ) return;
    }

    /**
     * constraints := constraint (COMMA constraint)*
     * @param pattern
     * @throws RecognitionException 
     */
    private void constraints( PatternDescrBuilder< ? > pattern ) throws RecognitionException {
        constraint( pattern,
                    false );
        if ( state.failed ) return;

        while ( input.LA( 1 ) == DRLLexer.COMMA ) {
            match( input,
                   DRLLexer.COMMA,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;

            constraint( pattern,
                        false );
            if ( state.failed ) return;
        }
    }

    /**
     * constraint := label? conditionalExpression
     * @param pattern
     * @throws RecognitionException 
     */
    private void constraint( PatternDescrBuilder< ? > pattern,
                             boolean positional ) throws RecognitionException {
        if ( state.backtracking == 0 && !state.errorRecovery ) {
            helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_START );
        }
        String bind = null;
        boolean unification = false;
        if ( input.LA( 1 ) == DRLLexer.ID && input.LA( 2 ) == DRLLexer.COLON ) {
            bind = label( DroolsEditorType.IDENTIFIER_VARIABLE );
            if ( state.failed ) return;
        } else if ( input.LA( 1 ) == DRLLexer.ID && input.LA( 2 ) == DRLLexer.UNIFY ) {
            bind = unif( DroolsEditorType.IDENTIFIER_VARIABLE );
            if ( state.failed ) return;
            unification = true;
        }

        int first = input.index();
        exprParser.getHelper().setHasOperator( false ); // resetting
        exprParser.conditionalOrExpression();
        if ( state.backtracking == 0 ) {
            if ( input.LA( 1 ) != DRLLexer.EOF || input.get( input.index() - 1 ).getType() == DRLLexer.WS ) {
                helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_END );
            }
        }
        if ( state.failed ) return;

        if ( state.backtracking == 0 && input.index() > first ) {
            // expression consumed something
            String expr = input.toString( first,
                                          input.LT( -1 ).getTokenIndex() );
            if ( bind == null ) {
                // it is a constraint
                pattern.constraint( expr,
                                    positional );
            } else {
                // it is a bind
                pattern.bind( bind,
                              expr,
                              unification );
            }
        }
    }

    /**
     * patternBehavior := ( PIPE behaviorDef )+
     *                    | OVER behaviorDef 
     * @param pattern
     * @throws RecognitionException 
     */
    private void patternBehavior( PatternDescrBuilder< ? > pattern ) throws RecognitionException {
        if ( input.LA( 1 ) == DRLLexer.PIPE ) {
            while ( input.LA( 1 ) == DRLLexer.PIPE ) {
                match( input,
                       DRLLexer.PIPE,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return;

                behaviorDef( pattern );
                if ( state.failed ) return;
            }
        } else {
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.OVER,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return;

            behaviorDef( pattern );
            if ( state.failed ) return;
        }
    }

    /**
     * behaviorDef := label ID LEFT_PAREN expression RIGHT_PAREN                    
     * @param pattern
     * @throws RecognitionException 
     */
    private void behaviorDef( PatternDescrBuilder< ? > pattern ) throws RecognitionException {
        BehaviorDescrBuilder< ? > behavior = helper.start( pattern,
                                                           BehaviorDescrBuilder.class,
                                                           null );
        try {
            String bName = label( DroolsEditorType.IDENTIFIER_PATTERN );
            if ( state.failed ) return;

            Token subtype = match( input,
                                   DRLLexer.ID,
                                   null,
                                   null,
                                   DroolsEditorType.IDENTIFIER_PATTERN );
            if ( state.failed ) return;

            if ( state.backtracking == 0 ) {
                behavior.type( bName,
                               subtype.getText() );
            }

            List<String> parameters = parameters();
            if ( state.failed ) return;

            if ( state.backtracking == 0 ) {
                behavior.parameters( parameters );
            }
        } finally {
            helper.end( BehaviorDescrBuilder.class,
                        behavior );
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

        if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
            helper.emit( Location.LOCATION_LHS_FROM );
        }

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
        if ( input.LA( 1 ) == DRLLexer.SEMICOLON ) {
            match( input,
                   DRLLexer.SEMICOLON,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
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
        String expr = conditionalOrExpression();
        if ( state.failed ) return;

        if ( state.backtracking == 0 ) {
            pattern.from().expression( expr );
            if ( input.LA( 1 ) != DRLLexer.EOF ) {
                helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
            }
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
            ep = StringUtils.unescapeJava( safeStripStringDelimiters( epStr.getText() ) );
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
            if ( input.LA( 1 ) != DRLLexer.EOF ) {
                helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
            }
        }
    }

    /**
     * fromCollect := COLLECT LEFT_PAREN lhsPatternBind RIGHT_PAREN
     * 
     * @param pattern
     * @throws RecognitionException
     */
    private void fromCollect( PatternDescrBuilder< ? > pattern ) throws RecognitionException {
        CollectDescrBuilder< ? > collect = helper.start( pattern,
                                                         CollectDescrBuilder.class,
                                                         null );
        try {
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.COLLECT,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return;
            if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                helper.emit( Location.LOCATION_LHS_FROM_COLLECT );
            }

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
                        collect );
            if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
            }
        }
    }

    /**
     * fromAccumulate := ACCUMULATE LEFT_PAREN lhsAnd COMMA 
     *                   ( initBlock COMMA actionBlock COMMA (reverseBlock COMMA)? resultBlock
     *                   | accumulateFunction 
     *                   RIGHT_PAREN
     * 
     * @param pattern
     * @throws RecognitionException
     */
    private void fromAccumulate( PatternDescrBuilder< ? > pattern ) throws RecognitionException {
        AccumulateDescrBuilder< ? > accumulate = helper.start( pattern,
                                                               AccumulateDescrBuilder.class,
                                                               null );
        try {
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.ACCUMULATE,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return;

            if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                helper.emit( Location.LOCATION_LHS_FROM_ACCUMULATE );
            }
            match( input,
                   DRLLexer.LEFT_PAREN,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;

            CEDescrBuilder< ? , AndDescr> source = accumulate.source();
            try {
                helper.start( source,
                              CEDescrBuilder.class,
                              null );
                lhsAnd( source,
                        false );
                if ( state.failed ) return;

                if ( source.getDescr() != null && source.getDescr() instanceof ConditionalElementDescr ) {
                    ConditionalElementDescr root = (ConditionalElementDescr) source.getDescr();
                    BaseDescr[] descrs = root.getDescrs().toArray( new BaseDescr[root.getDescrs().size()] );
                    root.getDescrs().clear();
                    for ( int i = 0; i < descrs.length; i++ ) {
                        root.addOrMerge( descrs[i] );
                    }
                }
            } finally {
                helper.end( CEDescrBuilder.class,
                            source );
            }

            if ( input.LA( 1 ) == DRLLexer.COMMA ) {
                match( input,
                       DRLLexer.COMMA,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
                if ( state.failed ) return;
            }

            if ( helper.validateIdentifierKey( DroolsSoftKeywords.INIT ) ) {
                // custom code, inline accumulate

                // initBlock
                match( input,
                       DRLLexer.ID,
                       DroolsSoftKeywords.INIT,
                       null,
                       DroolsEditorType.KEYWORD );
                if ( state.failed ) return;
                if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                    helper.emit( Location.LOCATION_LHS_FROM_ACCUMULATE_INIT );
                }

                String init = chunk( DRLLexer.LEFT_PAREN,
                                     DRLLexer.RIGHT_PAREN,
                                     Location.LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE );
                if ( state.failed ) return;
                if ( state.backtracking == 0 ) accumulate.init( init );

                if ( input.LA( 1 ) == DRLLexer.COMMA ) {
                    match( input,
                           DRLLexer.COMMA,
                           null,
                           null,
                           DroolsEditorType.SYMBOL );
                    if ( state.failed ) return;
                }

                // actionBlock
                match( input,
                       DRLLexer.ID,
                       DroolsSoftKeywords.ACTION,
                       null,
                       DroolsEditorType.KEYWORD );
                if ( state.failed ) return;
                if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                    helper.emit( Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION );
                }

                String action = chunk( DRLLexer.LEFT_PAREN,
                                       DRLLexer.RIGHT_PAREN,
                                       Location.LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE );
                if ( state.failed ) return;
                if ( state.backtracking == 0 ) accumulate.action( action );

                if ( input.LA( 1 ) == DRLLexer.COMMA ) {
                    match( input,
                           DRLLexer.COMMA,
                           null,
                           null,
                           DroolsEditorType.SYMBOL );
                    if ( state.failed ) return;
                }

                // reverseBlock
                if ( helper.validateIdentifierKey( DroolsSoftKeywords.REVERSE ) ) {
                    match( input,
                           DRLLexer.ID,
                           DroolsSoftKeywords.REVERSE,
                           null,
                           DroolsEditorType.KEYWORD );
                    if ( state.failed ) return;
                    if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                        helper.emit( Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE );
                    }

                    String reverse = chunk( DRLLexer.LEFT_PAREN,
                                            DRLLexer.RIGHT_PAREN,
                                            Location.LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE );
                    if ( state.failed ) return;
                    if ( state.backtracking == 0 ) accumulate.reverse( reverse );

                    if ( input.LA( 1 ) == DRLLexer.COMMA ) {
                        match( input,
                               DRLLexer.COMMA,
                               null,
                               null,
                               DroolsEditorType.SYMBOL );
                        if ( state.failed ) return;
                    }
                }

                // resultBlock
                match( input,
                       DRLLexer.ID,
                       DroolsSoftKeywords.RESULT,
                       null,
                       DroolsEditorType.KEYWORD );
                if ( state.failed ) return;

                if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                    helper.emit( Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT );
                }

                String result = chunk( DRLLexer.LEFT_PAREN,
                                       DRLLexer.RIGHT_PAREN,
                                       Location.LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE );
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
                        accumulate );
            if ( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF ) {
                helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
            }
        }
    }

    /**
     * accumulateFunction := label? ID parameters
     * @param accumulate
     * @throws RecognitionException
     */
    private void accumulateFunction( AccumulateDescrBuilder< ? > accumulate ) throws RecognitionException {
        String label = null;
        if ( input.LA( 1 ) == DRLLexer.ID && input.LA( 2 ) == DRLLexer.COLON && !helper.validateCEKeyword( 1 ) ) {
            label = label( DroolsEditorType.IDENTIFIER_VARIABLE );
            if ( state.failed ) return;
        }

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
                                 label,
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
                helper.emit( Location.LOCATION_RHS );
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
                // remove the "then" keyword and any subsequent spaces and line breaks
                // keep indendation of 1st non-blank line
                chunk = chunk.replaceFirst( "^then\\s*\\r?\\n?",
                                            "" );
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
        AnnotationDescrBuilder annotation = null;
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

            if ( state.backtracking == 0 ) {
                annotation = adb.newAnnotation( id.getText() );
                helper.setStart( annotation,
                                 at );
            }

            try {
                if ( input.LA( 1 ) == DRLLexer.LEFT_PAREN ) {
                    if ( speculateElementValuePairs() ) {
                        elementValuePairs( annotation );
                        if ( state.failed ) return;
                    } else {
                        String value = chunk( DRLLexer.LEFT_PAREN,
                                              DRLLexer.RIGHT_PAREN,
                                              -1 ).trim();
                        if ( state.failed ) return;
                        if ( state.backtracking == 0 ) {
                            if ( value.startsWith( "\"" ) && value.endsWith( "\"" ) ) {
                                value = StringUtils.unescapeJava( value );
                            }
                            annotation.value( value );
                        }
                    }
                }
            } finally {
                if ( state.backtracking == 0 ) {
                    helper.setEnd( annotation );
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
     * elementValuePair := ID EQUALS elementValue
     * @param annotation
     */
    private void elementValuePair( AnnotationDescrBuilder annotation ) {
        try {
            Token id = match( input,
                              DRLLexer.ID,
                              null,
                              null,
                              DroolsEditorType.IDENTIFIER );
            if ( state.failed ) return;
            String key = id.getText();

            match( input,
                   DRLLexer.EQUALS_ASSIGN,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return;

            String value = elementValue();
            if ( value.startsWith( "\"" ) && value.endsWith( "\"" ) ) {
                value = StringUtils.unescapeJava( value );
            }
            if ( state.failed ) return;

            if ( state.backtracking == 0 ) {
                String actKey = key != null ? key : "value";
                String actVal = annotation.getDescr().getValue( actKey );
                if ( actVal != null ) {
                    // TODO: error message?
                    value = "\"" + AnnotationDescr.unquote( actVal ) + AnnotationDescr.unquote( value ) + "\"";
                }
                annotation.keyValue( actKey,
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
     * @param doQualify set to true if qualification is acceptable
     * @param doGenPar  set to true if generic arguments and brackets are acceptable
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
            type = type.replace( " ",
                                 "" );
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
            qi = qi.replace( " ",
                             "" );
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
     * Matches a conditional || expression
     * 
     * @return
     * @throws RecognitionException
     */
    public String conditionalOrExpression() throws RecognitionException {
        int first = input.index();
        exprParser.conditionalOrExpression();
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
     * @param location
     * @return the matched chunk without the delimiters
     */
    public String chunk( final int leftDelimiter,
                         final int rightDelimiter,
                         final int location ) {
        String chunk = "";
        int first = -1, last = first;
        try {
            match( input,
                   leftDelimiter,
                   null,
                   null,
                   DroolsEditorType.SYMBOL );
            if ( state.failed ) return chunk;
            if ( state.backtracking == 0 && location >= 0 ) {
                helper.emit( location );
            }
            int nests = 0;
            first = input.index();

            while ( input.LA( 1 ) != DRLLexer.EOF && input.LA( 1 ) != rightDelimiter || nests > 0 ) {
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
                                        String left,
                                        String right ) {
        if ( value != null ) {
            value = value.trim();
            if ( value.length() >= left.length() + right.length() &&
                 value.startsWith( left ) && value.endsWith( right ) ) {
                value = value.substring( left.length(),
                                          value.length() - right.length() );
            }
        }
        return value;
    }

    private String safeStripStringDelimiters( String value ) {
        if ( value != null ) {
            value = value.trim();
            if ( value.length() >= 2 && value.startsWith( "\"" ) && value.endsWith( "\"" ) ) {
                value = value.substring( 1,
                                         value.length() - 1 );
            }
        }
        return value;
    }

}
