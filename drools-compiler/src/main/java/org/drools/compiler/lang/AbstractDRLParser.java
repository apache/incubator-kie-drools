package org.drools.compiler.lang;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.descr.PackageDescr;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.api.io.Resource;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractDRLParser implements DRLParser {

    protected TokenStream           input;
    protected RecognizerSharedState state;
    protected ParserHelper          helper;

    public AbstractDRLParser(TokenStream input) {
        this.input = input;
        this.state = new RecognizerSharedState();
        this.helper = new ParserHelper( input, state, getLanguageLevel() );
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

    /**
     * Entry point method of a DRL compilation unit
     *
     * compilationUnit := packageStatement? ( statement SEMICOLON? )*
     *
     * @return a PackageDescr with the content of the whole compilation unit
     *
     * @throws RecognitionException
     */
    public PackageDescr compilationUnit() throws RecognitionException {
        return compilationUnit(DescrFactory.newPackage());
    }

    public PackageDescr compilationUnit(Resource resource) throws RecognitionException {
        return compilationUnit(DescrFactory.newPackage(resource));
    }

    protected abstract PackageDescr compilationUnit(PackageDescrBuilder pkg) throws RecognitionException;

    protected abstract LanguageLevelOption getLanguageLevel();
}
