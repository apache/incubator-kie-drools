/*
 * Copyright 2010 JBoss Inc
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

package org.drools.builder.conf;

import org.drools.runtime.rule.EvaluatorDefinition;

/**
 * A class for the evaluators configuration. 
 * 
 * Drools supports custom evaluators. After implementing an evaluator
 * use this option class to register it to the knowledge builder.
 */
public class EvaluatorOption implements MultiValueKnowledgeBuilderOption {

    private static final long serialVersionUID = 510l;

    /**
     * The prefix for the property name for evaluators
     */
    public static final String PROPERTY_NAME = "drools.evaluator.";
    
    /**
     * evaluator key
     */
    private final String key;
    
    /**
     * the evaluator instance
     */
    private final EvaluatorDefinition evaluator;
    
    /**
     * Private constructor to enforce the use of the factory method
     * @param key
     */
    private EvaluatorOption( final String key, final EvaluatorDefinition evaluator ) {
        this.key = key;
        this.evaluator = evaluator;
    }
    
    /**
     * This is a factory method for this EvaluatorOption configuration.
     * The factory method is a best practice for the case where the 
     * actual object construction is changed in the future.
     * 
     * @param key the key of the evaluator to be configured
     * @param evaluator the evaluator definition
     * 
     * @return the actual type safe default dialect configuration.
     */
    public static EvaluatorOption get( final String key, final EvaluatorDefinition evaluator ) {
        return new EvaluatorOption( key, evaluator );
    }
    
    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME+key;
    }
    
    /**
     * Returns the name of the dialect configured as default
     * 
     * @return
     */
    public String getName() {
        return key;
    }

    /**
     * Returns the accumulate function instance
     * @return
     */
    public EvaluatorDefinition getEvaluatorDefinition() {
        return evaluator;
    }
    
    @Override
    public String toString() {
        return "EvaluatorOption( name="+key+" evaluator="+evaluator+" )";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((evaluator == null) ? 0 : evaluator.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        EvaluatorOption other = (EvaluatorOption) obj;
        if ( evaluator == null ) {
            if ( other.evaluator != null ) {
                return false;
            }
        } else if ( other.evaluator == null ) {
            return false;
        } else if ( !evaluator.getClass().equals( other.evaluator.getClass() ) ) {
            return false;
        }
        if ( key == null ) {
            if ( other.key != null ) return false;
        } else if ( !key.equals( other.key ) ) return false;
        return true;
    }

}
