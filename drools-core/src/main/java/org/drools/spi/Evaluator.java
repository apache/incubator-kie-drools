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
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.VariableRestriction.VariableContextEntry;

/**
 * A public interface to be implemented by all evaluators
 */
public interface Evaluator
    extends
    Serializable {

    /**
     * Returns the type of the values this evaluator operates upon.
     * 
     * @return
     */
    public ValueType getValueType();

    /**
     * Returns the operator representation object for this evaluator
     * 
     * @return
     */
    public Operator getOperator();
    
    /**
     * Returns the value type this evaluator will coerce
     * operands to, during evaluation. This is useful for
     * operators like "memberOf", that always convert to
     * Object when evaluating, independently of the source
     * operand value type.
     * 
     * @return
     */
    public ValueType getCoercedValueType();

    /**
     * There are evaluators that operate on fact attributes and
     * there are evaluators that operato on fact handle attributes
     * (metadata). 
     * 
     * This method allows the evaluator to prepare the object
     * to be evaluated. That includes, unwrapping the object if needed.
     *  
     * @param handle
     * @return
     */
    public Object prepareObject( InternalFactHandle handle );
    
    /**
     * This method will extract the value from the object1 using the 
     * extractor and compare it with the object2.
     * @param workingMemory 
     * @param extractor 
     *        The extractor used to get the source value from the object
     * @param object1
     *        The source object to evaluate
     * @param object2
     *        The actual value to compare to
     * 
     * @return Returns true if evaluation is successfull. false otherwise.
     */
    public boolean evaluate(InternalWorkingMemory workingMemory,
                            InternalReadAccessor extractor,
                            Object object1,
                            FieldValue value);

    public boolean evaluate(InternalWorkingMemory workingMemory,
                            InternalReadAccessor leftExtractor,
                            Object left,
                            InternalReadAccessor rightExtractor,
                            Object right);

    public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                      VariableContextEntry context,
                                      Object object1);

    public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                       VariableContextEntry context,
                                       Object object2);

}