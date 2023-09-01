/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drl.parser.lang;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;
import org.drools.drl.parser.DroolsParserException;
import org.drools.drl.ast.dsl.DescrFactory;
import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.descr.PackageDescr;
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
