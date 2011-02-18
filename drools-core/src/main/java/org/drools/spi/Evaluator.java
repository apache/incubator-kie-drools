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

package org.drools.spi;

import java.io.Serializable;

import org.drools.base.ValueType;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.runtime.rule.Operator;
import org.drools.time.Interval;

/**
 * A public interface to be implemented by all evaluators
 */
public interface Evaluator
    extends
    Serializable, org.drools.runtime.rule.Evaluator {

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
     * There are evaluators that operate on fact attributes,
     * there are evaluators that operate on fact handle attributes
     * (metadata), and there are evaluators that can operate in
     * either one. 
     * 
     * This method allows the evaluator to prepare the left object
     * for evaluation. That includes, unwrapping the object from the
     * handle, if necessary.
     * 
     * It is important to note that the concept of left and right
     * is based on the Rete notion of left and right, where right
     * corresponds to the current pattern, while left is a binding 
     * to a previous pattern.
     *  
     * @param handle
     * @return
     */
    public Object prepareLeftObject( InternalFactHandle handle );
    
    /**
     * There are evaluators that operate on fact attributes,
     * there are evaluators that operate on fact handle attributes
     * (metadata), and there are evaluators that can operate in
     * either one. 
     * 
     * This method allows the evaluator to prepare the right object
     * for evaluation. That includes, unwrapping the object from the
     * handle, if necessary.
     * 
     * It is important to note that the concept of left and right
     * is based on the Rete notion of left and right, where right
     * corresponds to the current pattern, while left is a binding 
     * to a previous pattern.
     *  
     * @param handle
     * @return
     */
    public Object prepareRightObject( InternalFactHandle handle );
    
    /**
     * Evaluates the expression using the provided parameters.
     * 
     * This method is used when evaluating alpha-constraints,
     * i.e., a fact attribute against a constant value. 
     * For instance:
     * 
     * Person( name == "Bob" )
     * 
     * So, it uses a constant value "Bob" that is sent into
     * the method as the FieldValue (value), and compares it
     * to the value of the name field, read by using the 
     * extractor on the fact instance (object1).
     *  
     * @param workingMemory
     *        The current working memory 
     * @param extractor 
     *        The extractor used to get the field value from the object
     * @param object
     *        The source object to evaluate, i.e., the fact
     * @param value
     *        The actual value to compare to, i.e., the constant value.
     * 
     * @return Returns true if evaluation is successful. false otherwise.
     */
    public boolean evaluate(InternalWorkingMemory workingMemory,
                            InternalReadAccessor extractor,
                            Object object,
                            FieldValue value);

    /**
     * Evaluates the expression using the provided parameters.
     * 
     * This method is used for internal indexing and hashing, 
     * when drools needs to extract and evaluate both left and
     * right values at once.
     *  
     * For instance:
     * 
     * Person( name == $someName )
     * 
     * This method will be used to extract and evaluate both
     * the "name" attribute and the "$someName" variable at once.
     *  
     * @param workingMemory
     *        The current working memory
     * @param leftExtractor
     *        The extractor to read the left value. In the above example,
     *        the "$someName" variable value.
     * @param left
     *        The source object from where the value of the variable is 
     *        extracted.
     * @param rightExtractor
     *        The extractor to read the right value. In the above example,
     *        the "name" attribute value. 
     * @param right
     *        The right object from where to extract the value. In the
     *        above example, that is the "Person" instance from where to 
     *        extract the "name" attribute.
     * 
     * @return Returns true if evaluation is successful. false otherwise.
     */
    public boolean evaluate(InternalWorkingMemory workingMemory,
                            InternalReadAccessor leftExtractor,
                            Object left,
                            InternalReadAccessor rightExtractor,
                            Object right);

    /**
     * Evaluates the expression using the provided parameters.
     * 
     * This method is used when evaluating left-activated 
     * beta-constraints, i.e., a fact attribute against a variable 
     * value, that is activated from the left.
     *  
     * For instance:
     * 
     * Person( name == $someName )
     * 
     * This method will be used when a new $someName variable is 
     * bound. So it will cache the value of $someName and will 
     * iterate over the right memory (Person instances) evaluating
     * each occurrence.
     *  
     * @param workingMemory
     *        The current working memory 
     * @param context 
     *        The previously cached context, including the left value 
     *        and the extractor for the right value.
     * @param right
     *        The right object, from where to extract the value. In the
     *        above example, that is the "Person" instance from where to 
     *        extract the "name" attribute.
     * 
     * @return Returns true if evaluation is successful. false otherwise.
     */
    public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                      VariableContextEntry context,
                                      Object right);

    /**
     * Evaluates the expression using the provided parameters.
     * 
     * This method is used when evaluating right-activated 
     * beta-constraints, i.e., a fact attribute against a variable 
     * value, that is activated from the right.
     *  
     * For instance:
     * 
     * Person( name == $someName )
     * 
     * This method will be used when a new Person instance is evaluated.
     * So it will cache the value of the "Person" instance and will 
     * iterate over the left memory comparing it to each "$someName" bound
     * values.
     *  
     * @param workingMemory
     *        The current working memory 
     * @param context 
     *        The previously cached context, including the right value 
     *        and the extractor for the left value.
     * @param left
     *        The left object, from where to extract the bound variable. 
     *        In the above example, that is the "$someName" variable value.
     * 
     * @return Returns true if evaluation is successful. false otherwise.
     */
    public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                       VariableContextEntry context,
                                       Object left);
    
    /**
     * Returns true if this evaluator implements a temporal evaluation,
     * i.e., a time sensitive evaluation whose properties of matching
     * only events within an specific time interval can be used for
     * determining event expirations automatically. 
     * 
     * @return true if the evaluator is a temporal evaluator. 
     */
    public boolean isTemporal();

    /**
     * In case this is a temporal evaluator, returns the interval 
     * in which this evaluator may match the target fact
     * 
     * @return
     */
    public Interval getInterval();

}
