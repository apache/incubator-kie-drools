/*
 * Copyright 2007 JBoss Inc
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
 *
 * Created on Dec 1, 2007
 */
package org.drools.lang.descr;

import java.util.Collections;
import java.util.List;

/**
 * This is a super class for all restrictions that are based on
 * evaluators.
 */
public class EvaluatorBasedRestrictionDescr extends RestrictionDescr {

    private static final long serialVersionUID = 150l;

    private String            evaluator;
    private boolean           negated;
    private List<String>      parameters;

    /**
     * Creates a new EvaluatorBasedRestriction
     */
    public EvaluatorBasedRestrictionDescr() {
    }

    /**
     * Creates a new EvaluatorBasedRestriction
     * 
     * @param evaluator the evaluator ID to be used in this restriction
     * @param isNegated true if the evaluator is boolean negated
     * @param parameterText the parameter text, in case there is any. null otherwise.
     */
    public EvaluatorBasedRestrictionDescr(final String evaluator,
                                          final boolean isNegated,
                                          final String parameterText) {
        this( evaluator,
              isNegated,
              Collections.singletonList( parameterText ) );
    }

    /**
     * Creates a new EvaluatorBasedRestriction
     * 
     * @param evaluator the evaluator ID to be used in this restriction
     * @param isNegated true if the evaluator is boolean negated
     * @param parameterText the list of parameters texts, in case there is any. null otherwise.
     */
    public EvaluatorBasedRestrictionDescr(final String evaluator,
                                          final boolean isNegated,
                                          final List<String> parameters) {
        this.evaluator = evaluator;
        this.negated = isNegated;
        this.parameters = parameters;
    }

    /**
     * Returns the evaluator ID for this restriction
     * @return
     */
    public String getEvaluator() {
        return this.evaluator;
    }

    /**
     * Returns true if this evaluator is boolean negated.
     * Example: "contains" is boolean negated if you want to check the elements that are not contained ("not contains")
     * 
     * @return the negated
     */
    public boolean isNegated() {
        return negated;
    }

    /**
     * In case there is any parameter text, this method returns it. Returns null otherwise.
     * A parameter text is evaluator parameters like "after[1,10]". In the previous example,
     * the parameter text will be "1,10".
     * 
     * @return the parameterText
     */
    public String getParameterText() {
        if( parameters != null ) {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for( String param : parameters ) {
                if( first ) {
                    first = false;
                } else {
                    builder.append( "," );
                }
                builder.append( param );
            }
            return builder.toString();
        }
        return null;
    }
    
    public List<String> getParameters() {
        return parameters;
    }
    
    public void setParameters( List<String> parameters ) {
        this.parameters = parameters;
    }

    /**
     * Sets the evaluator ID for this restriction
     */
    public void setEvaluator( String evaluator ) {
        this.evaluator = evaluator;
    }

    /**
     * Sets if this evaluator is negated.
     * Example: "contains" is boolean negated if you want to check the elements that are not contained ("not contains")
     * 
     * @param negated the negated
     */
    public void setNegated( boolean negated ) {
        this.negated = negated;
    }

    public String toString() {
        return (this.isNegated() ? "not " : "") + this.getEvaluator() + (this.getParameterText() != null ? "[" + this.getParameterText() + "]" : "");
    }
}
