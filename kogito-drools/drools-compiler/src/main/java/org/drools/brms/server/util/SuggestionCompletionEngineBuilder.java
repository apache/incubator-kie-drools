/*
 * Copyright 2006 JBoss Inc
 *
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

package org.drools.brms.server.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brl.DSLSentence;

/**
 * A builder to incrementally populate a SuggestionCompletionEngine
 *
 * @author etirelli
 */
public class SuggestionCompletionEngineBuilder {

    private SuggestionCompletionEngine instance              = new SuggestionCompletionEngine();
    private List                       factTypes             = new ArrayList();
    private Map                        fieldsForType         = new HashMap();
    private Map                        fieldTypes            = new HashMap();
    private Map                        globalTypes           = new HashMap();
    private List                       actionDSLSentences    = new ArrayList();
    private List                       conditionDSLSentences = new ArrayList();

    public SuggestionCompletionEngineBuilder() {
    }

    /**
     * Start the creation of a new SuggestionCompletionEngine
     */
    public void newCompletionEngine() {
        this.instance = new SuggestionCompletionEngine();
        this.factTypes = new ArrayList();
        this.fieldsForType = new HashMap();
        this.fieldTypes = new HashMap();
        this.globalTypes = new HashMap();
        this.actionDSLSentences = new ArrayList();
        this.conditionDSLSentences = new ArrayList();
    }

    /**
     * Adds a fact type to the engine
     *
     * @param factType
     */
    public void addFactType(final String factType) {
        this.factTypes.add( factType );
    }

    /**
     * Adds the list of fields for a given type
     *
     * @param type
     * @param fields
     */
    public void addFieldsForType(final String type,
                                 final String[] fields) {
        this.fieldsForType.put( type,
                                fields );
    }

    /**
     * @return true if this has the type already registered (field information).
     */
    public boolean hasFieldsForType(final String type) {
        return this.fieldsForType.containsKey( type );
    }

    /**
     * Adds a type declaration for a field
     *
     * @param field
     * @param type
     */
    public void addFieldType(final String field,
                             final String type) {
        this.fieldTypes.put( field,
                             type );
    }

    /**
     * Adds a global and its corresponding type to the engine
     *
     * @param global
     * @param type
     */
    public void addGlobalType(final String global,
                              final String type) {
        this.globalTypes.put( global,
                              type );
    }

    /**
     * Add a DSL sentence for an action.
     */
    public void addDSLActionSentence(final String sentence) {
        final DSLSentence sen = new DSLSentence();
        sen.sentence = sentence;
        this.actionDSLSentences.add( sen );
    }

    /**
     * Add a DSL sentence for a condition.
     */
    public void addDSLConditionSentence(final String sentence) {
        final DSLSentence sen = new DSLSentence();
        sen.sentence = sentence;
        this.conditionDSLSentences.add( sen );
    }

    /**
     * Returns a SuggestionCompletionEngine instance populated with
     * all the data since last call to newCompletionEngine() method
     *
     * @return
     */
    public SuggestionCompletionEngine getInstance() {
        this.instance.factTypes = (String[]) this.factTypes.toArray( new String[this.factTypes.size()] );
        this.instance.fieldsForType = this.fieldsForType;
        this.instance.fieldTypes = this.fieldTypes;
        this.instance.globalTypes = this.globalTypes;
        this.instance.actionDSLSentences = (DSLSentence[]) this.actionDSLSentences.toArray( new DSLSentence[this.actionDSLSentences.size()] );
        this.instance.conditionDSLSentences = (DSLSentence[]) this.conditionDSLSentences.toArray( new DSLSentence[this.conditionDSLSentences.size()] );
        return this.instance;
    }




}
