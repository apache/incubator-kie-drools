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
 * Created on Dec 6, 2007
 */
package org.drools.base.evaluators;

import org.drools.base.ValueType;
import org.drools.spi.Evaluator;

import java.io.Externalizable;

/**
 * An evaluator definition interface that allows for pluggable
 * evaluator implementation.
 *
 * This interface is the registry entry point for all available
 * evaluators and describes all evaluator capabilities
 *
 * @author etirelli
 */
public interface EvaluatorDefinition extends Externalizable {

    /**
     * Returns the list of identifies this
     * evaluator implementation supports
     *
     * @return
     */
    public String[] getEvaluatorIds();

    /**
     * My appologies to english speakers if the word "negatable" does not
     * exists. :)
     *
     * This method returns true if this evaluator supports negation. Example:
     *
     * the "matches" operator supports "not matches" and so is "negatable" (!?)
     *
     * @return
     */
    public boolean isNegatable();

    /**
     * Returns the evaluator instance for the given type and the
     * defined parameterText
     *
     * @param type the type of the attributes this evaluator will
     *             operate on. This is important because the evaluator
     *             may do optimizations and type coercion based on the
     *             types it is evaluating. It is also possible that
     *             this evaluator does not support a given type.
     *
     * @param operatorId the string identifier of the evaluator
     *
     * @param isNegated true if the evaluator instance to be returned is
     *                  the negated version of the evaluator.
     *
     * @param parameterText some evaluators support parameters and these
     *                      parameters are defined as a String that is
     *                      parsed by the evaluator itself.
     *
     * @return an Evaluator instance capable of evaluating expressions
     *         between values of the given type, or null in case the type
     *         is not supported.
     */
    public Evaluator getEvaluator(ValueType type,
                                  String operatorId,
                                  boolean isNegated,
                                  String parameterText);

    /**
     * Returns the evaluator instance for the given type and the
     * defined parameterText
     *
     * @param type the type of the attributes this evaluator will
     *             operate on. This is important because the evaluator
     *             may do optimizations and type coercion based on the
     *             types it is evaluating. It is also possible that
     *             this evaluator does not support a given type.
     *
     * @param operator the operator implemented by the evaluator
     *
     * @param parameterText some evaluators support parameters and these
     *                      parameters are defined as a String that is
     *                      parsed by the evaluator itself.
     *
     * @return an Evaluator instance capable of evaluating expressions
     *         between values of the given type, or null in case the type
     *         is not supported.
     */
    public Evaluator getEvaluator(ValueType type,
                                  Operator operator,
                                  String parameterText);

    /**
     * Returns the evaluator instance for the given type and the
     * defined parameterText
     *
     * @param type the type of the attributes this evaluator will
     *             operate on. This is important because the evaluator
     *             may do optimizations and type coercion based on the
     *             types it is evaluating. It is also possible that
     *             this evaluator does not support a given type.
     *
     * @param operator the operator implemented by the evaluator
     *
     * @return an Evaluator instance capable of evaluating expressions
     *         between values of the given type, or null in case the type
     *         is not supported.
     */
    public Evaluator getEvaluator(ValueType type,
                                  Operator operator);

    /**
     * Returns true in case this evaluator supports operations over values
     * of that specific type.
     *
     * @param type
     * @return
     */
    public boolean supportsType(ValueType type);

    /**
     * There are evaluators that operate on *fact handle* attributes and
     * evaluators that operate on *fact* attributes.
     *
     * @return true if this evaluator operates on fact handle attributes
     *         and false if it operates on fact attributes
     */
    public boolean operatesOnFactHandles();

}
