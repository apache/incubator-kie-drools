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

package org.drools.guvnor.server.util;

import java.util.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;

import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.DSLSentence;
import org.drools.lang.dsl.AbstractDSLMappingEntry;
import org.drools.lang.dsl.DSLMappingEntry;

/**
 * A builder to incrementally populate a SuggestionCompletionEngine
 *
 * @author etirelli
 */
public class SuggestionCompletionEngineBuilder {

    private SuggestionCompletionEngine instance              = new SuggestionCompletionEngine();
    private List                       factTypes             = new ArrayList();
    private Map                        fieldsForType         = new HashMap();
    private Map                        modifiersForType      = new HashMap();
    private Map                        fieldTypes            = new HashMap();
    private Map<String,Field>          fieldTypesField       = new HashMap<String,Field>();
    private Map                        globalTypes           = new HashMap();
    private List                       actionDSLSentences    = new ArrayList();
    private List                       conditionDSLSentences = new ArrayList();
    private List                       keywordDSLItems = new ArrayList();
    private List                       anyScopeDSLItems = new ArrayList();
    private List<String>                globalCollections = new ArrayList();
 

    public SuggestionCompletionEngineBuilder() {
    }

    /**
     * Start the creation of a new SuggestionCompletionEngine
     */
    public void newCompletionEngine() {
        this.instance = new SuggestionCompletionEngine();
        this.factTypes = new ArrayList();
        this.fieldsForType = new HashMap();
        this.modifiersForType = new HashMap();
        this.fieldTypes = new HashMap();
        this.fieldTypesField = new HashMap();
        this.globalTypes = new HashMap();
        this.actionDSLSentences = new ArrayList();
        this.conditionDSLSentences = new ArrayList();
        this.keywordDSLItems = new ArrayList();
        this.anyScopeDSLItems = new ArrayList();
        this.globalCollections = new ArrayList<String>();
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
     * Adds the list of modifiers for a given type
     *
     * @param type
     * @param fields
     */
    public void addModifiersForType(final String type,
                                 final String[] fields) {
        this.modifiersForType.put( type,
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
     * Adds a type declaration for a field
     *
     * @param field
     * @param type
     */
    public void addFieldTypeField(final String field,
                             final Field type) {
        this.fieldTypesField.put( field,
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

    public void addGlobalCollection(String global) {
        this.globalCollections.add(global);
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
        this.instance.modifiers = this.modifiersForType;
        this.instance.fieldTypes = this.fieldTypes;
        for (String fieldName  : this.fieldTypesField.keySet()){
            Field field =  (Field) this.fieldTypesField.get(fieldName);
            if (field != null){
                Type type = field.getGenericType();
                if (type instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) type;
                    Type goodType=null;
                    for (Type t : pt.getActualTypeArguments()) {
                        goodType = t;
                    }
                    int index = goodType.toString().lastIndexOf(".");
                    String className = goodType.toString().substring(index+1);
                    this.instance.fieldParametersType.put(fieldName,className);
                  }
            }
        }
        this.instance.globalTypes = this.globalTypes;
        this.instance.actionDSLSentences = makeArray(this.actionDSLSentences);
        this.instance.conditionDSLSentences = makeArray(this.conditionDSLSentences);
        this.instance.keywordDSLItems = makeArray(this.keywordDSLItems);
        this.instance.anyScopeDSLItems = makeArray(this.anyScopeDSLItems);
        this.instance.globalCollections = this.globalCollections.toArray(new String[globalCollections.size()]);
        return this.instance;
    }

	private DSLSentence[] makeArray(List ls) {
        return (DSLSentence[]) ls.toArray( new DSLSentence[ls.size()] );
    }

	public void addDSLMapping(DSLMappingEntry entry) {
		DSLSentence sen = new DSLSentence();
		sen.sentence = entry.getMappingKey();
		if (entry.getSection() == DSLMappingEntry.CONDITION) {
			this.conditionDSLSentences.add(sen);
		} else if (entry.getSection() == DSLMappingEntry.CONSEQUENCE) {
			this.actionDSLSentences.add(sen);
		} else if (entry.getSection() == DSLMappingEntry.KEYWORD) {
			this.keywordDSLItems.add(sen);
		} else if (entry.getSection() == DSLMappingEntry.ANY) {
			this.anyScopeDSLItems.add(sen);
		}

	}



}
