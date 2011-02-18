/**
 * Copyright 2005 JBoss Inc
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

package org.drools.runtime.rule;


import java.io.Serializable;

/**
 * A public interface to be implemented by all evaluators
 */
public interface Evaluator
    extends
    Serializable {

    /**
     * Returns the operator representation object for this evaluator
     * 
     * @return
     */
    public Operator getOperator();
    
    /**
     * Returns true if this evaluator implements a temporal evaluation,
     * i.e., a time sensitive evaluation whose properties of matching
     * only events within an specific time interval can be used for
     * determining event expirations automatically. 
     * 
     * @return true if the evaluator is a temporal evaluator. 
     */
    public boolean isTemporal();

}