package org.drools.spi;

/*
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

import java.io.Serializable;

import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.rule.VariableConstraint.VariableContextEntry;

public interface Evaluator
    extends
    Serializable {

    public ValueType getValueType();

    public Operator getOperator();

    /**
     * This method will extract the value from the object1 using the 
     * extractor and compare it with the object2.
     * 
     * @param extractor 
     *        The extractor used to get the source value from the object
     * @param object1
     *        The source object to evaluate
     * @param object2
     *        The actual value to compare to
     *        
     * @return Returns true if evaluation is successfull. false otherwise.
     */
    public boolean evaluate(Extractor extractor,
                            Object object1,
                            FieldValue value);

    /**
     * This method will extract the value from the object2 using the 
     * extractor and compare it with the object1.
     * 
     * @param object1
     *        The actual value to compare to
     * @param extractor 
     *        The extractor used to get the source value from the object
     * @param object2
     *        The source object to evaluate
     *        
     * @return Returns true if evaluation is successfull. false otherwise.
     */
    public boolean evaluate(FieldValue object1,
                            Extractor extractor,
                            Object object2);

    public boolean evaluateCachedLeft(VariableContextEntry context,
                                      Object object1);

    public boolean evaluateCachedRight(VariableContextEntry context,
                                       Object object2);

}