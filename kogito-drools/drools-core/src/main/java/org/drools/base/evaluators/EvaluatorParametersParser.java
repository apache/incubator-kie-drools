/**
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

package org.drools.base.evaluators;

/**
 * An interface for Evaluator Parameters Parser.
 * 
 * Evaluators may optionally have parameters. This parameters are passed into the
 * EvaluatorDefinition as a String that needs to eventually be parsed. This interface
 * defines the operations a parser implementation must expose. 
 * 
 * @author etirelli
 */
public interface EvaluatorParametersParser {
    
    /**
     * Parses the given paramText and return an array
     * of objects where each position in the array corresponds
     * to the appropriate parameter in the parameter string.
     * 
     * @param paramText the string of parameters
     * 
     * @return the array of objects corresponding to each parameter
     */
    public Object[] parse( final String paramText );

}
