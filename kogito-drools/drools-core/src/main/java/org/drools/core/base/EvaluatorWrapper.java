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

package org.drools.core.base;

import org.drools.core.base.extractors.ConstantValueReader;
import org.drools.core.base.extractors.SelfReferenceClassFieldReader;
import org.drools.core.base.field.ObjectFieldImpl;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.VariableRestriction.VariableContextEntry;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.time.Interval;

import static org.drools.core.base.mvel.MVELCompilationUnit.getFactHandle;

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

    private InternalReadAccessor                       leftExtractor;
    private InternalReadAccessor                       rightExtractor;

    private boolean                                    selfLeft;
    private boolean                                    selfRight;

    public EvaluatorWrapper(Evaluator evaluator,
                            Declaration leftBinding,
                            Declaration rightBinding) {
        this.evaluator = evaluator;
        this.leftBinding = leftBinding;
        this.rightBinding = rightBinding;
        init();
    }

    private void init() {
        leftExtractor = leftBinding == null || leftBinding.getExtractor() == null ? extractor : leftBinding.getExtractor();
        rightExtractor = rightBinding == null || rightBinding.getExtractor() == null ? extractor : rightBinding.getExtractor();
        selfLeft = leftBinding == null || leftBinding.getIdentifier().equals("this");
        selfRight = rightBinding == null || rightBinding.getIdentifier().equals("this");
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
        if (rightBinding == null) {
            return evaluator.evaluate( workingMemory,
                                       leftBinding != null ? leftExtractor : new ConstantValueReader(left),
                                       leftHandle,
                                       new ObjectFieldImpl(right) );
        }
        return evaluator.evaluate( workingMemory,
                                   leftBinding != null ? leftExtractor : new ConstantValueReader(left),
                                   leftHandle,
                                   rightBinding != null ?
                                                        ( rightHandle != null ? rightExtractor : new ConstantValueReader( rightExtractor.getValue( workingMemory, right ) ) )
                                                        : new ConstantValueReader(right),
                                   rightHandle );
    }

    /**
     * @return
     * @see org.drools.core.spi.Evaluator#getValueType()
     */
    public ValueType getValueType() {
        return evaluator.getValueType();
    }

    /**
     * @return
     * @see org.drools.core.spi.Evaluator#getOperator()
     */
    public org.kie.api.runtime.rule.Operator getOperator() {
        return evaluator.getOperator();
    }

    /**
     * @return
     * @see org.drools.core.spi.Evaluator#getCoercedValueType()
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
     * @see org.drools.core.spi.Evaluator#evaluate(org.drools.core.common.InternalWorkingMemory, org.drools.core.spi.InternalReadAccessor, InternalFactHandle, org.drools.core.spi.FieldValue)
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
     * @see org.drools.core.spi.Evaluator#evaluate(org.drools.core.common.InternalWorkingMemory, org.drools.core.spi.InternalReadAccessor, InternalFactHandle, org.drools.core.spi.InternalReadAccessor, InternalFactHandle)
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
     * @see org.drools.core.spi.Evaluator#evaluateCachedLeft(org.drools.core.common.InternalWorkingMemory, org.drools.core.rule.VariableRestriction.VariableContextEntry, InternalFactHandle)
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
     * @see org.drools.core.spi.Evaluator#evaluateCachedRight(org.drools.core.common.InternalWorkingMemory, org.drools.core.rule.VariableRestriction.VariableContextEntry, InternalFactHandle)
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
     * @see org.drools.core.spi.Evaluator#isTemporal()
     */
    public boolean isTemporal() {
        return evaluator.isTemporal();
    }

    /**
     * @return
     * @see org.drools.core.spi.Evaluator#getInterval()
     */
    public Interval getInterval() {
        return evaluator.getInterval();
    }

    public void loadHandles(InternalWorkingMemory workingMemory, InternalFactHandle[] handles, InternalFactHandle rightHandle) {
        this.workingMemory = workingMemory;
        leftHandle = selfLeft ? null : getFactHandle(leftBinding, handles);
        if (leftHandle == null) {
            leftHandle = rightHandle;
        }
        this.rightHandle = selfRight ? rightHandle : getFactHandle(rightBinding, handles);
    }

    @Override
    public String toString() {
        return this.evaluator.toString();
    }


    public static SelfReferenceClassFieldReader getExtractor() {
        return extractor;
    }

    public Evaluator getEvaluator() {
        return evaluator;
    }

    public InternalWorkingMemory getWorkingMemory() {
        return workingMemory;
    }

    public Declaration getLeftBinding() {
        return leftBinding;
    }

    public Declaration getRightBinding() {
        return rightBinding;
    }

    public InternalFactHandle getLeftHandle() {
        return leftHandle;
    }

    public InternalFactHandle getRightHandle() {
        return rightHandle;
    }

    public InternalReadAccessor getLeftExtractor() {
        return leftExtractor;
    }

    public InternalReadAccessor getRightExtractor() {
        return rightExtractor;
    }

    public boolean isSelfLeft() {
        return selfLeft;
    }

    public boolean isSelfRight() {
        return selfRight;
    }
}
