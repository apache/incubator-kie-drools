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

package org.drools.base;

import org.drools.base.extractors.SelfReferenceClassFieldReader;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.drools.time.Interval;

/**
 * An EvaluatorWrapper is used when executing MVEL expressions
 * that have operator calls rewritten as:
 * 
 * operator.evaluate( leftArg, rightArg )
 * 
 */
public class EvaluatorWrapper
    implements
    Evaluator {

    private static final long                          serialVersionUID = 520L;

    private static final SelfReferenceClassFieldReader extractor        = new SelfReferenceClassFieldReader( Object.class,
                                                                                                             "dummy" );

    private Evaluator                                  evaluator;
    private transient InternalWorkingMemory            workingMemory;

    private Declaration                                leftBinding;
    private Declaration                                rightBinding;

    private InternalFactHandle                         leftHandle;
    private InternalFactHandle                         rightHandle;

    public EvaluatorWrapper(Evaluator evaluator,
                            Declaration leftBinding,
                            Declaration rightBinding) {
        this.evaluator = evaluator;
        this.leftBinding = leftBinding;
        this.rightBinding = rightBinding;
    }

    /**
     * This method is called when operators are rewritten as function calls. For instance,
     * 
     * x after y
     * 
     * Is rewritten as
     * 
     * after.evaluate( x, y )
     * 
     * @return
     */
    public boolean evaluate(Object left,
                            Object right) {
        //        if( leftBinding != null ) {
        //            left = evaluator.prepareLeftObject( leftHandle ); 
        //        }
        //        if( rightBinding != null ) {
        //            right = evaluator.prepareRightObject( rightHandle );
        //        }

        return evaluator.evaluate( workingMemory,
                                   extractor,
                                   leftHandle,
                                   extractor,
                                   rightHandle );
    }

    /**
     * @return
     * @see org.kie.spi.Evaluator#getValueType()
     */
    public ValueType getValueType() {
        return evaluator.getValueType();
    }

    /**
     * @return
     * @see org.kie.spi.Evaluator#getOperator()
     */
    public org.kie.runtime.rule.Operator getOperator() {
        return evaluator.getOperator();
    }

    /**
     * @return
     * @see org.kie.spi.Evaluator#getCoercedValueType()
     */
    public ValueType getCoercedValueType() {
        return evaluator.getCoercedValueType();
    }

    /**
     * @param workingMemory
     * @param extractor
     * @param factHandle
     * @param value
     * @return
     * @see org.kie.spi.Evaluator#evaluate(org.kie.common.InternalWorkingMemory, org.kie.spi.InternalReadAccessor, InternalFactHandle, org.kie.spi.FieldValue)
     */
    public boolean evaluate(InternalWorkingMemory workingMemory,
                            InternalReadAccessor extractor,
                            InternalFactHandle factHandle,
                            FieldValue value) {
        return evaluator.evaluate( workingMemory,
                                   extractor,
                                   factHandle,
                                   value );
    }

    /**
     * @param workingMemory
     * @param leftExtractor
     * @param left
     * @param rightExtractor
     * @param right
     * @return
     * @see org.kie.spi.Evaluator#evaluate(org.kie.common.InternalWorkingMemory, org.kie.spi.InternalReadAccessor, InternalFactHandle, org.kie.spi.InternalReadAccessor, InternalFactHandle)
     */
    public boolean evaluate(InternalWorkingMemory workingMemory,
                            InternalReadAccessor leftExtractor,
                            InternalFactHandle left,
                            InternalReadAccessor rightExtractor,
                            InternalFactHandle right) {
        return evaluator.evaluate( workingMemory,
                                   leftExtractor,
                                   left,
                                   rightExtractor,
                                   right );
    }

    /**
     * @param workingMemory
     * @param context
     * @param right
     * @return
     * @see org.kie.spi.Evaluator#evaluateCachedLeft(org.kie.common.InternalWorkingMemory, org.kie.rule.VariableRestriction.VariableContextEntry, InternalFactHandle)
     */
    public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                      VariableContextEntry context,
                                      InternalFactHandle right) {
        return evaluator.evaluateCachedLeft( workingMemory,
                                             context,
                                             right );
    }

    /**
     * @param workingMemory
     * @param context
     * @param left
     * @return
     * @see org.kie.spi.Evaluator#evaluateCachedRight(org.kie.common.InternalWorkingMemory, org.kie.rule.VariableRestriction.VariableContextEntry, InternalFactHandle)
     */
    public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                       VariableContextEntry context,
                                       InternalFactHandle left) {
        return evaluator.evaluateCachedRight( workingMemory,
                                              context,
                                              left );
    }

    /**
     * @return
     * @see org.kie.spi.Evaluator#isTemporal()
     */
    public boolean isTemporal() {
        return evaluator.isTemporal();
    }

    /**
     * @return
     * @see org.kie.spi.Evaluator#getInterval()
     */
    public Interval getInterval() {
        return evaluator.getInterval();
    }

    /**
     * @return the workingMemory
     */
    public InternalWorkingMemory getWorkingMemory() {
        return workingMemory;
    }

    /**
     * @param workingMemory the workingMemory to set
     */
    public EvaluatorWrapper setWorkingMemory(InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
        return this;
    }

    /**
     * @return the leftHandle
     */
    public InternalFactHandle getLeftHandle() {
        return leftHandle;
    }

    /**
     * @param leftHandle the leftHandle to set
     */
    public EvaluatorWrapper setLeftHandle(InternalFactHandle leftHandle) {
        this.leftHandle = leftHandle;
        return this;
    }

    /**
     * @return the rightHandle
     */
    public InternalFactHandle getRightHandle() {
        return rightHandle;
    }

    /**
     * @param rightHandle the rightHandle to set
     */
    public EvaluatorWrapper setRightHandle(InternalFactHandle rightHandle) {
        this.rightHandle = rightHandle;
        return this;
    }

    /**
     * @return the leftBinding
     */
    public Declaration getLeftBinding() {
        return leftBinding;
    }

    /**
     * @param leftBinding the leftBinding to set
     */
    public void setLeftBinding(Declaration leftBinding) {
        this.leftBinding = leftBinding;
    }

    /**
     * @return the rightBinding
     */
    public Declaration getRightBinding() {
        return rightBinding;
    }

    /**
     * @param rightBinding the rightBinding to set
     */
    public void setRightBinding(Declaration rightBinding) {
        this.rightBinding = rightBinding;
    }

    @Override
    public String toString() {
        return this.evaluator.toString();
    }

    protected static SelfReferenceClassFieldReader getExtractor() {
        return extractor;
    }

    protected Evaluator getEvaluator() {
        return evaluator;
    }
}
