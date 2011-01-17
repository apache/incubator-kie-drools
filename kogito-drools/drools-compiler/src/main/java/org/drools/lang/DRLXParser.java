package org.drools.lang;

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
import org.drools.lang.api.DescrFactory;
import org.drools.lang.api.GlobalDescrBuilder;
import org.drools.lang.api.ImportDescrBuilder;
import org.drools.lang.api.PackageDescrBuilder;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;

public class DRLXParser {

    private TokenStream           input;
    private RecognizerSharedState state;
    private ParserXHelper         helper;

    public DRLXParser(TokenStream input) {
        this.input = input;
        this.state = new RecognizerSharedState();
        this.helper = new ParserXHelper( DRLXTokens.tokenNames,
                                         input,
                                         state );
    }

    public String[] getTokenNames() {
        return DRLXTokens.tokenNames;
    }

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
            int next = input.LA( 1 );
            switch ( next ) {
                case DRLLexer.EOF :
                    return pkg.getDescr();
                case DRLLexer.ID :
                    // package declaration?
                    if ( helper.validateIdentifierKey( DroolsSoftKeywords.PACKAGE ) ) {
                        String pkgName = packageStatement();
                        pkg.name( pkgName );
                    }

                    // statements
                    int index;
                    do {
                        index = input.index();
                        statement();
                        // TODO: error handling and recovery
                    } while ( input.LA( 1 ) == DRLLexer.ID && index != input.index() );
                    break;
                default :
                    // TODO: error handling and recovery
                    break;
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
            helper.start( PackageDescrBuilder.class );

            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.PACKAGE,
                   null,
                   DroolsEditorType.KEYWORD );

            pkgName = qualifiedIdentifier();
            helper.setParaphrasesValue( DroolsParaphraseTypes.PACKAGE,
                                        pkgName );

            if ( input.LA( 1 ) == DRLLexer.SEMICOLON ) {
                match( input,
                       DRLLexer.SEMICOLON,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
            }

        } catch ( RecognitionException re ) {
            reportError( re );
        } finally {
            helper.end( PackageDescrBuilder.class );
        }
        return pkgName;
    }

    /**
     * statement := importStatement
     *           |  global 
     *           |  function
     *           |  typeDeclaration
     *           |  rule
     *           |  query
     *           |  ruleAttribute
     *           ;
     *           
     * @throws RecognitionException
     */
    public BaseDescr statement() throws RecognitionException {
        BaseDescr descr = null;
        try {
            if ( helper.validateIdentifierKey( DroolsSoftKeywords.IMPORT ) ) {
                descr = importStatement();
            } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.GLOBAL ) ) {
                descr = globalStatement();
            }
        } catch ( RecognitionException e ) {
            helper.reportError( e );
        } catch ( Exception e ) {
            helper.reportError( e );
        }
        return descr;
    }

    /**
     * importStatement := IMPORT FUNCTION? qualifiedIdentifier (DOT STAR)? SEMICOLON?
     * 
     * @return
     * @throws RecognitionException
     */
    public ImportDescr importStatement() throws RecognitionException {
        ImportDescrBuilder imp = null;
        try {
            imp = helper.start( ImportDescrBuilder.class );

            // import
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.IMPORT,
                   null,
                   DroolsEditorType.KEYWORD );

            if ( helper.validateIdentifierKey( DroolsSoftKeywords.FUNCTION ) ) {
                // function
                match( input,
                       DRLLexer.ID,
                       DroolsSoftKeywords.FUNCTION,
                       null,
                       DroolsEditorType.KEYWORD );
            }

            // qualifiedIdentifier
            String target = qualifiedIdentifier();

            if ( input.LA( 1 ) == DRLLexer.DOT && input.LA( 2 ) == DRLLexer.STAR ) {
                // .*
                match( input,
                       DRLLexer.DOT,
                       null,
                       null,
                       DroolsEditorType.IDENTIFIER );
                match( input,
                       DRLLexer.STAR,
                       null,
                       null,
                       DroolsEditorType.IDENTIFIER );
                target += ".*";
            }
            imp.target( target );

            if ( input.LA( 1 ) == DRLLexer.SEMICOLON ) {
                match( input,
                       DRLLexer.SEMICOLON,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
            }

        } catch ( RecognitionException re ) {
            reportError( re );
        } finally {
            helper.end( ImportDescrBuilder.class );
        }
        return (imp != null) ? imp.getDescr() : null;
    }

    /**
     * globalStatement := GLOBAL type ID SEMICOLON?
     * 
     * @return
     * @throws RecognitionException
     */
    public GlobalDescr globalStatement() throws RecognitionException {
        GlobalDescrBuilder global = null;
        try {
            global = helper.start( GlobalDescrBuilder.class );

            // 'global'
            match( input,
                   DRLLexer.ID,
                   DroolsSoftKeywords.GLOBAL,
                   null,
                   DroolsEditorType.KEYWORD );

            // type
            String type = type();
            global.type( type );

            // name
            match( input,
                   DRLLexer.ID,
                   null,
                   null,
                   DroolsEditorType.IDENTIFIER_TYPE );

            if ( input.LA( 1 ) == DRLLexer.SEMICOLON ) {
                match( input,
                       DRLLexer.SEMICOLON,
                       null,
                       null,
                       DroolsEditorType.SYMBOL );
            }

        } catch ( RecognitionException re ) {
            reportError( re );
        } finally {
            helper.end( GlobalDescrBuilder.class );
        }
        return (global != null) ? global.getDescr() : null;
    }

    /**
     * Matches a type name
     * 
     * type := ID typeArguments? ( DOT ID typeArguments? )* (LEFT_SQUARE RIGHT_SQUARE)*
     * 
     * @return
     * @throws RecognitionException
     */
    public String type() throws RecognitionException {
        StringBuilder type = new StringBuilder();
        try {
            Token token = match( input,
                                 DRLLexer.ID,
                                 null,
                                 new int[]{DRLLexer.DOT, DRLLexer.LESS},
                                 DroolsEditorType.IDENTIFIER );
            type.append( token.getText() );

            if ( input.LA( 1 ) == DRLLexer.LESS ) {
                type.append( typeArguments() );
            }

            while ( input.LA( 1 ) == DRLLexer.DOT && input.LA( 2 ) == DRLLexer.ID ) {
                token = match( input,
                               DRLLexer.DOT,
                               null,
                               new int[]{DRLLexer.ID},
                               DroolsEditorType.IDENTIFIER );
                type.append( token.getText() );
                token = match( input,
                               DRLLexer.ID,
                               null,
                               new int[]{DRLLexer.DOT},
                               DroolsEditorType.IDENTIFIER );
                type.append( token.getText() );

                if ( input.LA( 1 ) == DRLLexer.LESS ) {
                    type.append( typeArguments() );
                }
            }

            while ( input.LA( 1 ) == DRLLexer.LEFT_SQUARE && input.LA( 2 ) == DRLLexer.RIGHT_SQUARE ) {
                token = match( input,
                               DRLLexer.LEFT_SQUARE,
                               null,
                               new int[]{DRLLexer.RIGHT_SQUARE},
                               DroolsEditorType.IDENTIFIER );
                type.append( token.getText() );
                token = match( input,
                               DRLLexer.RIGHT_SQUARE,
                               null,
                               null,
                               DroolsEditorType.IDENTIFIER );
                type.append( token.getText() );
            }

        } catch ( RecognitionException re ) {
            reportError( re );
        }
        return type.toString();
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
        StringBuilder typeArguments = new StringBuilder();
        try {
            Token token = match( input,
                                 DRLLexer.LESS,
                                 null,
                                 new int[]{DRLLexer.QUESTION, DRLLexer.ID},
                                 DroolsEditorType.SYMBOL );
            typeArguments.append( token.getText() );

            typeArguments.append( typeArgument() );

            while ( input.LA( 1 ) == DRLLexer.COMMA ) {
                token = match( input,
                               DRLLexer.COMMA,
                               null,
                               new int[]{DRLLexer.QUESTION, DRLLexer.ID},
                               DroolsEditorType.IDENTIFIER );
                typeArguments.append( token.getText() );

                typeArguments.append( typeArgument() );

            }

            token = match( input,
                           DRLLexer.GREATER,
                           null,
                           null,
                           DroolsEditorType.SYMBOL );
            typeArguments.append( token.getText() );

        } catch ( RecognitionException re ) {
            reportError( re );
        }
        return typeArguments.toString();
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
        StringBuilder typeArgument = new StringBuilder();
        try {
            int next = input.LA( 1 );
            switch ( next ) {
                case DRLLexer.QUESTION :
                    Token token = match( input,
                                         DRLLexer.QUESTION,
                                         null,
                                         null,
                                         DroolsEditorType.SYMBOL );
                    typeArgument.append( token.getText() );

                    if ( helper.validateIdentifierKey( DroolsSoftKeywords.EXTENDS ) ) {
                        token = match( input,
                                       DRLLexer.ID,
                                       DroolsSoftKeywords.EXTENDS,
                                       null,
                                       DroolsEditorType.SYMBOL );
                        typeArgument.append( " " );
                        typeArgument.append( token.getText() );
                        typeArgument.append( " " );

                        typeArgument.append( type() );
                    } else if ( helper.validateIdentifierKey( DroolsSoftKeywords.SUPER ) ) {
                        token = match( input,
                                       DRLLexer.ID,
                                       DroolsSoftKeywords.SUPER,
                                       null,
                                       DroolsEditorType.SYMBOL );
                        typeArgument.append( " " );
                        typeArgument.append( token.getText() );
                        typeArgument.append( " " );

                        typeArgument.append( type() );
                    }
                    break;
                case DRLLexer.ID :
                    typeArgument.append( type() );
                    break;
                default :
                    // TODO: raise error
            }
        } catch ( RecognitionException re ) {
            reportError( re );
        }
        return typeArgument.toString();
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
        StringBuilder qi = new StringBuilder();
        try {
            Token token = match( input,
                                 DRLLexer.ID,
                                 null,
                                 new int[]{DRLLexer.DOT},
                                 DroolsEditorType.IDENTIFIER );
            qi.append( token.getText() );
            while ( input.LA( 1 ) == DRLLexer.DOT && input.LA( 2 ) == DRLLexer.ID ) {
                token = match( input,
                               DRLLexer.DOT,
                               null,
                               new int[]{DRLLexer.ID},
                               DroolsEditorType.IDENTIFIER );
                qi.append( token.getText() );
                token = match( input,
                               DRLLexer.ID,
                               null,
                               new int[]{DRLLexer.DOT},
                               DroolsEditorType.IDENTIFIER );
                qi.append( token.getText() );
            }
        } catch ( RecognitionException re ) {
            reportError( re );
        }
        return qi.toString();
    }

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
        try {
            matchedSymbol = input.LT( 1 );
            if ( input.LA( 1 ) == ttype && (text == null || text.equals( matchedSymbol.getText() )) ) {
                input.consume();
                state.errorRecovery = false;
                state.failed = false;
                return matchedSymbol;
            }
            matchedSymbol = recoverFromMismatchedToken( input,
                                                        ttype,
                                                        text,
                                                        follow );
            return matchedSymbol;
        } catch ( RecognitionException re ) {
            state.failed = true;
            state.errorRecovery = false;
            throw re;
        } finally {
            helper.emit( matchedSymbol,
                         etype );
        }
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
        e = new MismatchedTokenException( ttype,
                                          input );
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

}